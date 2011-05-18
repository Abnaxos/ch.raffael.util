package ch.raffael.util.binding.convert;


import ch.raffael.util.binding.InvalidValueException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class IntToStringConverter implements Converter<Integer, String> {

    private final LongToStringConverter longToString = new LongToStringConverter();

    @Override
    public String sourceToTarget(Integer value) {
        if ( value == null ) {
            return null;
        }
        return longToString.sourceToTarget(Long.valueOf(value));
    }

    @Override
    public Integer targetToSource(String value) {
        if ( value == null ) {
            return null;
        }
        long result = longToString.targetToSource(value);
        if ( result > Integer.MAX_VALUE || result < Integer.MIN_VALUE ) {
            throw new InvalidValueException("Number '" + value + "' too big");
        }
        return (int)result;
    }
}
