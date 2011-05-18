package ch.raffael.util.binding.convert;

import java.text.DecimalFormat;
import java.text.ParseException;

import ch.raffael.util.binding.InvalidValueException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class LongToStringConverter implements Converter<Long, String> {

    private final DecimalFormat format = new DecimalFormat("#,###"); // FIXME: configurable

    @Override
    public String sourceToTarget(Long value) {
        if ( value == null ) {
            return null;
        }
        return format.format(value);
    }

    @Override
    public Long targetToSource(String value) {
        if ( value == null ) {
            return null;
        }
        try {
            Number result;
            result = format.parse(value);
            // see javadoc: Most values will be returned as Long; Double will be used,
            // if the number doesn't fit into Long => throw number too big
            if ( result instanceof Double ) {
                throw new InvalidValueException("Number '" + value + "' too big");
            }
            return (Long)result; // see above, we can cast safely
        }
        catch ( ParseException e ) {
            throw new InvalidValueException("Invalid number '" + value + "': " + e.getLocalizedMessage(), e);
        }
    }
}
