package gov.usgs.cida.wqp.parameter.transform;

import gov.usgs.cida.wqp.validation.ValidationResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 *
 * @author tkunicki
 */
public class SplitAndRegexGroupTransformer implements Transformer<String[][]> {
	private final Logger log = Logger.getLogger(getClass());

    private final Pattern regexPattern;
    private final SplitTransformer splitter;

    public SplitAndRegexGroupTransformer(String delimiter, String regex) {
        splitter = new SplitTransformer(delimiter);
    	log.trace(getClass());
        regexPattern = Pattern.compile(regex);
    }

    @Override
    public String[][] transform(String value) {
        if (null != value) {
            String[] split = splitter.split(value);
            String[][] strings = new String[split.length][];
            for (int i = 0; i < split.length; ++i) {
                Matcher matcher = regexPattern.matcher(split[i]);
                matcher.find();
                int groupCount = matcher.groupCount();
                strings[i] = new String[groupCount];
                for (int j = 0; j < groupCount; ++j) {
                    strings[i][j] = matcher.group(j + 1);
                }
            }
            return strings;
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    @Override
    public ValidationResult<String[][]> getValdiationResult() {
    	return new ValidationResult<String[][]>();
    }
    

}