package ch.raffael.util.i18n.impl.handlers;

import java.net.MalformedURLException;
import java.net.URL;

import ch.raffael.util.common.UnexpectedException;
import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.i18n.impl.ResourcePointer;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UrlHandler extends NoParametersHandler {

    @Override
    public Object resolve(Class<? extends ResourceBundle> bundleClass, ResourcePointer ptr, URL baseUrl, String value) throws Exception {
        return new URL(baseUrl, value);
    }

    @Override
    public Object notFound(Class<? extends ResourceBundle> bundleClass, ResourcePointer ptr, URL baseUrl) {
        try {
            return new URL(baseUrl, "not-found");
        }
        catch ( MalformedURLException e ) {
            throw new UnexpectedException("Unexpected URL syntax exception", e);
        }
    }

}
