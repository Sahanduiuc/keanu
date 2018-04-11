package io.improbable.keanu.vertices.dbl.probabilistic;

import io.improbable.keanu.distributions.continuous.Gamma;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.Infinitesimal;

import java.util.Map;
import java.util.Random;

public class GammaVertex extends ProbabilisticDouble {

    private final DoubleVertex a;
    private final DoubleVertex theta;
    private final DoubleVertex k;
    private final Random random;

    /**
     * @param a      location
     * @param theta      scale
     * @param k      shape
     * @param random
     */
    public GammaVertex(DoubleVertex a, DoubleVertex theta, DoubleVertex k, Random random) {
        this.a = a;
        this.theta = theta;
        this.k = k;
        this.random = random;
        setValue(sample());
        setParents(a, theta, k);
    }

    public GammaVertex(DoubleVertex a, DoubleVertex theta, DoubleVertex c) {
        this(a, theta, c, new Random());
    }

    @Override
    public double density(Double value) {
        return Gamma.pdf(a.getValue(), theta.getValue(), k.getValue(), value);
    }

    public double logDensity(Double value) {
        return Gamma.logPdf(a.getValue(), theta.getValue(), k.getValue(), value);
    }

    @Override
    public Map<String, Double> dDensityAtValue() {
        Gamma.Diff diff = Gamma.dPdf(a.getValue(), theta.getValue(), k.getValue(), getValue());
        return convertDualNumbersToDiff(diff.dPda, diff.dPdtheta, diff.dPdk, diff.dPdx);
    }

    @Override
    public Map<String, Double> dlnDensityAtValue() {
        Gamma.Diff diff = Gamma.dlnPdf(a.getValue(), theta.getValue(), k.getValue(), getValue());
        return convertDualNumbersToDiff(diff.dPda, diff.dPdtheta, diff.dPdk, diff.dPdx);
    }

    private Map<String, Double> convertDualNumbersToDiff(double dPda, double dPdtheta, double dPdk, double dPdx) {
        Infinitesimal dPdInputsFromA = a.getDualNumber().getInfinitesimal().multiplyBy(dPda);
        Infinitesimal dPdInputsFromTheta = theta.getDualNumber().getInfinitesimal().multiplyBy(dPdtheta);
        Infinitesimal dPdInputsFromK = k.getDualNumber().getInfinitesimal().multiplyBy(dPdk);
        Infinitesimal dPdInputs = dPdInputsFromA.add(dPdInputsFromTheta).add(dPdInputsFromK);
        dPdInputs.getInfinitesimals().put(getId(), dPdx);

        return dPdInputs.getInfinitesimals();
    }

    @Override
    public Double sample() {
        return Gamma.sample(a.getValue(), theta.getValue(), k.getValue(), random);
    }

}