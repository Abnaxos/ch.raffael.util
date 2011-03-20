package ch.raffael.util.binding.convert;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


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
        long result = 0;
        for ( int i = 0; i < value.length(); i++ ) {
            char c = value.charAt(i);
            if ( Character.isWhitespace(c) || c == DecimalFormatSymbols.getInstance().getGroupingSeparator() ) {
                // ignore
            }
            else if ( c >= '0' && c <= '9' ) {
                result = result * 10 + (c - '0');
            }
            else {
                return result;
            }
        }
        return result;
    }
}
