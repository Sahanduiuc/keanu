package io.improbable.keanu.vertices.dbltensor.nonprobabilistic.unary;

import io.improbable.keanu.vertices.dbltensor.DoubleTensorVertex;
import org.junit.Test;

import static io.improbable.keanu.vertices.dbltensor.nonprobabilistic.unary.UnaryOperationTestHelpers.*;

public class TensorArcSinVertexTest {

    @Test
    public void asinScalarVertexValues() {
        operatesOnScalarVertexValue(
            Math.PI,
            Math.asin(Math.PI),
            DoubleTensorVertex::asin
        );
    }

    @Test
    public void calculatesDualNumberOfTwoScalarsAsin() {
        calculatesDualNumberOfScalar(
            0.5,
            1.0 / Math.sqrt(1.0 - 0.5 * 0.5),
            DoubleTensorVertex::asin
        );
    }

    @Test
    public void asinMatrixVertexValues() {
        operatesOn2x2MatrixVertexValues(
            new double[]{0.0, 0.1, 0.2, 0.3},
            new double[]{Math.asin(0.0), Math.asin(0.1), Math.asin(0.2), Math.asin(0.3)},
            DoubleTensorVertex::asin
        );
    }

    @Test
    public void calculatesDualNumberOfTwoMatricesElementWiseAsin() {
        calculatesDualNumberOfMatrixElementWiseOperator(
            new double[]{0.1, 0.2, 0.3, 0.4},
            new double[]{1.0 / Math.sqrt(1.0 - 0.1 * 0.1),
                1.0 / Math.sqrt(1.0 - 0.2 * 0.2),
                1.0 / Math.sqrt(1.0 - 0.3 * 0.3),
                1.0 / Math.sqrt(1.0 - 0.4 * 0.4)
            },
            DoubleTensorVertex::asin
        );
    }

}
