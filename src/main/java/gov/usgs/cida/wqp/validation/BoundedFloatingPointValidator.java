package gov.usgs.cida.wqp.validation;

import org.apache.log4j.Logger;

import gov.usgs.cida.wqp.parameter.Parameters;
import gov.usgs.cida.wqp.parameter.transform.SplitDoubleTransformer;


/**
 *
 * @author tkunicki
 */
public class BoundedFloatingPointValidator extends AbstractValidator<double[]> {
	private final Logger log = Logger.getLogger(getClass());

    protected final double minBound;
    protected final double maxBound;
    protected ValidationResult<double[]> vr = new ValidationResult<double[]>();

    public BoundedFloatingPointValidator(Parameters inParameter)  {
        super(inParameter);
    	log.trace(getClass());
        minBound = -Double.MAX_VALUE;
        maxBound = Double.MAX_VALUE;
    }
    
    public BoundedFloatingPointValidator(Parameters inParameter, String inMinBound, String inMaxBound)  {
        this(inParameter, DEFAULT_MIN_OCCURS, DEFAULT_MAX_OCCURS, DEFAULT_DELIMITER, inMinBound, inMaxBound);
    }
    
    public BoundedFloatingPointValidator(Parameters inParameter, int minOccurs, int maxOccurs, String delimiter, String inMinBound, String inMaxBound)  {
        super(inParameter, minOccurs, maxOccurs, delimiter);
    	log.trace(getClass());
        try {
            minBound = Double.parseDouble(inMinBound);
        } catch (Exception e) {
            throw new IllegalArgumentException("minBound is not a valid number.", e);
        }
        try {
            maxBound = Double.parseDouble(inMaxBound);
        } catch (Exception e) {
            throw new IllegalArgumentException("maxBound is not a valid number.", e);
        }
        if (maxBound < minBound) {
            throw new IllegalArgumentException("minBound must be less than maxBound.");
        }
        setTransformer(new SplitDoubleTransformer(delimiter));
    }

    @Override
    public ValidationResult<double[]> validate(String value) {
        vr = new ValidationResult<double[]>();
        double[] doubles = transformer.transform(value);
        if (doubles.length < getMinOccurs() || doubles.length > getMaxOccurs()) {
            vr.setValid(false);
            vr.getValidationMessages().add(getErrorMessage(value, IS_NOT_BETWEEN + getMinOccurs() + AND + getMaxOccurs() + " occurances."));
        }
        for (double d : doubles) {
            if (d < minBound || d > maxBound) {
                vr.setValid(false);
                vr.getValidationMessages().add(getErrorMessage(String.valueOf(d), IS_NOT_BETWEEN + minBound + AND + maxBound));
            }
        }
        
        ValidationResult<double[]> vrd = transformer.getValdiationResult();
        // cannot use merge because the transform does not know about parameters
        for (String msgNeedParam : vrd.getValidationMessages() ) {
        	vr.setValid(false);
        	String msgWithParam = msgNeedParam.replace("PARAM", parameter.toString());
        	vr.getValidationMessages().add(msgWithParam);
        }
        vr.setTransformedValue(doubles);
        return vr;
    }

}