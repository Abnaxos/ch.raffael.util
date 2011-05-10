package ch.raffael.util.binding.convert;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class StringToUrlConverter implements Converter<String, URL> {

    @Override
    public URL sourceToTarget(String value) {
        if ( value == null ) {
            return null;
        }
        try {
            return new URL(value);
        }
        catch ( MalformedURLException e ) {
            return null;
        }
    }

    @Override
    public String targetToSource(URL value) {
        if ( value == null ) {
            return null;
        }
        else {
            return value.toString();
        }
    }
}
