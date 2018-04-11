package io.improbable.keanu.algorithms.mcmc;

import io.improbable.keanu.algorithms.NetworkSamples;
import io.improbable.keanu.algorithms.graphtraversal.MarkovBlanket;
import io.improbable.keanu.network.BayesNet;
import io.improbable.keanu.vertices.Vertex;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Metropolis Hastings is a Markov Chain Monte Carlo method for obtaining samples from a probability distribution
 */
public class MetropolisHastings {

    public static NetworkSamples getPosteriorSamples(BayesNet bayesNet,
                                                     List<? extends Vertex<?>> fromVertices,
                                                     int sampleCount) {
        return getPosteriorSamples(bayesNet, fromVertices, sampleCount, new Random());
    }

    /**
     * @param bayesNet
     * @param fromVertices the vertices to include in the returned samples
     * @param sampleCount
     * @param random
     * @return Samples for each vertex ordered by MCMC iteration
     */
    public static NetworkSamples getPosteriorSamples(final BayesNet bayesNet,
                                                     final List<? extends Vertex<?>> fromVertices,
                                                     final int sampleCount,
                                                     final Random random) {
        if (bayesNet.isInImpossibleState()) {
            throw new RuntimeException("Cannot start optimizer on zero probability network");
        }

        Map<String, List<?>> samplesByVertex = new HashMap<>();
        List<? extends Vertex<?>> latentVertices = bayesNet.getLatentVertices();
        Map<Vertex<?>, Set<Vertex<?>>> affectedVerticesCache = getVerticesAffectedByLatents(latentVertices);

        double logP = bayesNet.getLogOfMasterP();
        for (int sampleNum = 0; sampleNum < sampleCount; sampleNum++) {

            Vertex<?> chosenVertex = latentVertices.get(sampleNum % latentVertices.size());
            Set<Vertex<?>> affectedVertices = affectedVerticesCache.get(chosenVertex);
            logP = nextSample(chosenVertex, logP, affectedVertices, 1.0, random);

            takeSamples(samplesByVertex, fromVertices);
        }

        return new NetworkSamples(samplesByVertex, sampleCount);
    }

    static <T> double nextSample(final Vertex<T> chosenVertex,
                                 final double logPOld,
                                 final Set<Vertex<?>> affectedVertices,
                                 final double T,
                                 final Random random) {

        final double affectedVerticesLogPOld = sumLogP(affectedVertices);

        final T oldValue = chosenVertex.getValue();
        final T proposedValue = chosenVertex.sample();

        chosenVertex.setAndCascade(proposedValue);

        final double affectedVerticesLogPNew = sumLogP(affectedVertices);

        final double logPNew = logPOld - affectedVerticesLogPOld + affectedVerticesLogPNew;

        final double pqxOld = chosenVertex.logDensity(oldValue);
        final double pqxNew = chosenVertex.logDensity(proposedValue);

        final double logr = (logPNew * (1.0 / T) + pqxOld) - (logPOld * (1.0 / T) + pqxNew);
        final double r = Math.exp(logr);

        final boolean shouldReject = r < random.nextDouble();

        if (shouldReject) {
            chosenVertex.setAndCascade(oldValue);
            return logPOld;
        }

        return logPNew;
    }

    static Map<Vertex<?>, Set<Vertex<?>>> getVerticesAffectedByLatents(List<? extends Vertex<?>> latentVertices) {
        return latentVertices.stream()
                .collect(Collectors.toMap(
                        v -> v,
                        v -> {
                            Set<Vertex<?>> affectedVertices = new HashSet<>();
                            affectedVertices.add(v);
                            affectedVertices.addAll(MarkovBlanket.getDownstreamProbabilisticVertices(v));
                            return affectedVertices;
                        }));
    }

    static double sumLogP(Set<Vertex<?>> vertices) {
        return vertices.stream()
                .mapToDouble(Vertex::logDensityAtValue)
                .sum();
    }

    private static void takeSamples(Map<String, List<?>> samples, List<? extends Vertex<?>> fromVertices) {
        fromVertices.forEach(vertex -> addSampleForVertex(vertex, samples));
    }

    private static <T> void addSampleForVertex(Vertex<T> vertex, Map<String, List<?>> samples) {
        List<T> samplesForVertex = (List<T>) samples.computeIfAbsent(vertex.getId(), v -> new ArrayList<T>());
        samplesForVertex.add(vertex.getValue());
    }

}