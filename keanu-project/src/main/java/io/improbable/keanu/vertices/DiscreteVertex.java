package io.improbable.keanu.vertices;

import io.improbable.keanu.tensor.Tensor;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.update.ValueUpdater;

import java.util.Map;

public abstract class DiscreteVertex<T extends Tensor> extends Vertex<T> {

    public DiscreteVertex(ValueUpdater<T> valueUpdater) {
        super(valueUpdater);
    }

    @Override
    public final double logProb(T value) {
        return logPmf(value);
    }

    @Override
    public final Map<Long, DoubleTensor> dLogProb(T value) {
        return dLogPmf(value);
    }

    public abstract double logPmf(T value);

    public abstract Map<Long, DoubleTensor> dLogPmf(T value);
}
