package ch.raffael.util.binding.convert;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UrlToStringConverter implements Converter<URL, String> {

    @Override
    public String sourceToTarget(URL value) {
        if ( value == null ) {
            return null;
        }
        else {
            return value.toString();
        }
    }

    @Override
    public URL targetToSource(String value) {
        if ( value == null || value.isEmpty() ) {
            return null;
        }
        try {
            return new URL(value);
        }
        catch ( MalformedURLException e ) {
            return null;
        }
    }
}
