package io.improbable.keanu.vertices.intgr.nonprobabilistic;

import io.improbable.keanu.tensor.intgr.IntegerTensor;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.dbl.KeanuRandom;

public class CastIntegerVertex extends NonProbabilisticInteger {

    private final Vertex<IntegerTensor> inputVertex;

    public CastIntegerVertex(Vertex<IntegerTensor> inputVertex) {
        super(v -> ((CastIntegerVertex) v).inputVertex.getValue());
        this.inputVertex = inputVertex;
        setParents(inputVertex);
    }

    @Override
    public IntegerTensor sample(KeanuRandom random) {
        return inputVertex.sample(random);
    }

}
