package io.improbable.keanu.vertices.bool.nonprobabilistic.operators.binary;

import static io.improbable.keanu.tensor.TensorShapeValidation.checkHasSingleNonScalarShapeOrAllScalar;

import io.improbable.keanu.tensor.bool.BooleanTensor;
import io.improbable.keanu.vertices.Vertex;

public class OrBinaryVertex extends BoolBinaryOpVertex<BooleanTensor, BooleanTensor> {

    public OrBinaryVertex(Vertex<BooleanTensor> a, Vertex<BooleanTensor> b) {
        super(checkHasSingleNonScalarShapeOrAllScalar(a.getShape(), b.getShape()), a, b);
    }

    /**
     * Returns true if a or b are true
     */
    @Override
    protected BooleanTensor op(BooleanTensor a, BooleanTensor b) {
        return a.or(b);
    }
}
