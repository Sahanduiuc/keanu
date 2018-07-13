package io.improbable.keanu.vertices.dbl.nonprobabilistic.operators.binary;


import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.Observable;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.KeanuRandom;
import io.improbable.keanu.vertices.update.NonProbabilisticValueUpdater;

public abstract class DoubleBinaryOpVertex extends DoubleVertex {

    protected final DoubleVertex a;
    protected final DoubleVertex b;

    /**
     * A vertex that performs a user defined operation on two vertices
     *
     * @param shape the shape of the resulting vertex
     * @param a a vertex
     * @param b a vertex
     */
    public DoubleBinaryOpVertex(int[] shape, DoubleVertex a, DoubleVertex b) {
        super(
            new NonProbabilisticValueUpdater<>(v -> ((DoubleBinaryOpVertex) v).op(a.getValue(), b.getValue())),
            Observable.observableTypeFor(DoubleBinaryOpVertex.class)
        );
        this.a = a;
        this.b = b;
        setParents(a, b);
        setValue(DoubleTensor.placeHolder(shape));
    }

    @Override
    public DoubleTensor sample(KeanuRandom random) {
        return op(a.sample(random), b.sample(random));
    }

    protected abstract DoubleTensor op(DoubleTensor a, DoubleTensor b);

}
