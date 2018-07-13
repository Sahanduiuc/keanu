package io.improbable.keanu.vertices.dbl.probabilistic;

import static io.improbable.keanu.tensor.TensorShapeValidation.checkHasSingleNonScalarShapeOrAllScalar;
import static io.improbable.keanu.tensor.TensorShapeValidation.checkTensorsMatchNonScalarShapeOrAreScalar;

import java.util.List;
import java.util.Map;

import io.improbable.keanu.distributions.continuous.InverseGamma;
import io.improbable.keanu.tensor.dbl.DoubleTensor;
import io.improbable.keanu.vertices.dbl.DoubleVertex;
import io.improbable.keanu.vertices.dbl.KeanuRandom;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.ConstantDoubleVertex;
import io.improbable.keanu.vertices.dbl.nonprobabilistic.diff.PartialDerivatives;

public class InverseGammaVertex extends ProbabilisticDouble {

    private final DoubleVertex alpha;
    private final DoubleVertex beta;

    /**
     * One alpha or beta or both driving an arbitrarily shaped tensor of Inverse Gamma
     * <p>
     * If all provided parameters are scalar then the proposed shape determines the shape
     *
     * @param tensorShape the desired shape of the vertex
     * @param alpha       the alpha of the Inverse Gamma with either the same shape as specified for this vertex or alpha scalar
     * @param beta        the beta of the Inverse Gamma with either the same shape as specified for this vertex or alpha scalar
     */
    public InverseGammaVertex(int[] tensorShape, DoubleVertex alpha, DoubleVertex beta) {
        checkTensorsMatchNonScalarShapeOrAreScalar(tensorShape, alpha.getShape(), beta.getShape());

        this.alpha = alpha;
        this.beta = beta;
        setParents(alpha, beta);
        setValue(DoubleTensor.placeHolder(tensorShape));
    }

    /**
     * One to one constructor for mapping some shape of alpha and beta to
     * alpha matching shaped Inverse Gamma.
     *
     * @param alpha the alpha of the Inverse Gamma with either the same shape as specified for this vertex or alpha scalar
     * @param beta  the beta of the Inverse Gamma with either the same shape as specified for this vertex or alpha scalar
     */
    public InverseGammaVertex(DoubleVertex alpha, DoubleVertex beta) {
        this(checkHasSingleNonScalarShapeOrAllScalar(alpha.getShape(), beta.getShape()), alpha, beta);
    }

    public InverseGammaVertex(DoubleVertex alpha, double beta) {
        this(alpha, new ConstantDoubleVertex(beta));
    }

    public InverseGammaVertex(double alpha, DoubleVertex beta) {
        this(new ConstantDoubleVertex(alpha), beta);
    }

    public InverseGammaVertex(double alpha, double beta) {
        this(new ConstantDoubleVertex(alpha), new ConstantDoubleVertex(beta));
    }

    public InverseGammaVertex(int[] tensorShape, DoubleVertex alpha, double beta) {
        this(tensorShape, alpha, new ConstantDoubleVertex(beta));
    }

    public InverseGammaVertex(int[] tensorShape, double alpha, DoubleVertex beta) {
        this(tensorShape, new ConstantDoubleVertex(alpha), beta);
    }

    public InverseGammaVertex(int[] tensorShape, double alpha, double beta) {
        this(tensorShape, new ConstantDoubleVertex(alpha), new ConstantDoubleVertex(beta));
    }

    @Override
    public double logProb(DoubleTensor value) {
        DoubleTensor alphaValues = alpha.getValue();
        DoubleTensor betaValues = beta.getValue();

        DoubleTensor logPdfs = InverseGamma.withParameters(alphaValues, betaValues).logProb(value);
        return logPdfs.sum();
    }

    @Override
    public Map<Long, DoubleTensor> dLogProb(DoubleTensor value) {
        List<DoubleTensor> dlnP = InverseGamma.withParameters(alpha.getValue(), beta.getValue()).dLogProb(value);

        return convertDualNumbersToDiff(dlnP.get(0), dlnP.get(1), dlnP.get(2));
    }

    private Map<Long, DoubleTensor> convertDualNumbersToDiff(DoubleTensor dPdalpha,
                                                             DoubleTensor dPdbeta,
                                                             DoubleTensor dPdx) {

        Differentiator differentiator = new Differentiator();
        PartialDerivatives dPdInputsFromA = differentiator.calculateDual((Differentiable) alpha).getPartialDerivatives().multiplyBy(dPdalpha);
        PartialDerivatives dPdInputsFromB = differentiator.calculateDual((Differentiable) beta).getPartialDerivatives().multiplyBy(dPdbeta);
        PartialDerivatives dPdInputs = dPdInputsFromA.add(dPdInputsFromB);

        if (!this.isObserved()) {
            dPdInputs.putWithRespectTo(getId(), dPdx);
        }

        return dPdInputs.asMap();
    }

    @Override
    public DoubleTensor sample(KeanuRandom random) {
        return InverseGamma.withParameters(alpha.getValue(), beta.getValue()).sample(getShape(), random);
    }

}
