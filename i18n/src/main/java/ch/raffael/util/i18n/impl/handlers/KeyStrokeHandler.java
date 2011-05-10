package ch.raffael.util.i18n.impl.handlers;

import java.net.URL;

import javax.swing.KeyStroke;

import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.i18n.impl.ResourcePointer;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class KeyStrokeHandler extends NoParametersHandler {

    @Override
    public Object resolve(Class<? extends ResourceBundle> bundleClass, ResourcePointer ptr, URL baseUrl, String value) throws Exception {
        return KeyStroke.getKeyStroke(value);
    }

    @Override
    public Object notFound(Class<? extends ResourceBundle> bundleClass, ResourcePointer ptr, URL baseUrl) {
        return null;
    }
}
