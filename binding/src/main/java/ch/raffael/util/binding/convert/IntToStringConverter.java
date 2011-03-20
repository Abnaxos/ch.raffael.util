package ch.raffael.util.binding.convert;

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
        return longToString.targetToSource(value).intValue();
    }
}
