package io.improbable.keanu.vertices.bool.nonprobabilistic.operators.binary;

import io.improbable.keanu.tensor.Tensor;
import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.vertices.NonProbabilistic;
import io.improbable.keanu.vertices.Vertex;
import io.improbable.keanu.vertices.bool.BoolVertex;
import io.improbable.keanu.vertices.dbl.KeanuRandom;
import io.improbable.keanu.vertices.update.NonProbabilisticValueUpdater;

public abstract class BoolBinaryOpVertex<A extends Tensor, B extends Tensor> extends BoolVertex implements NonProbabilistic<BooleanTensor> {

    protected final Vertex<A> a;
    protected final Vertex<B> b;

    public BoolBinaryOpVertex(int[] shape, Vertex<A> a, Vertex<B> b) {
        super(new NonProbabilisticValueUpdater<>(v -> ((BoolBinaryOpVertex<A, B>) v).op(a.getValue(), b.getValue())));

        this.a = a;
        this.b = b;
        setParents(a, b);
        setValue(BooleanTensor.placeHolder(shape));
    }

    @Override
    public BooleanTensor sample(KeanuRandom random) {
        return op(a.sample(random), b.sample(random));
    }

    protected abstract BooleanTensor op(A a, B b);

    @Override
    public boolean contradictsObservation() {
        return isObserved() && !op(a.getValue(), b.getValue()).elementwiseEquals(getValue()).allTrue();
    }
}