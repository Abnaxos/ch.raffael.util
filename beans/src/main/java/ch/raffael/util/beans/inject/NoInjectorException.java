package ch.raffael.util.beans.inject;

import ch.raffael.util.beans.BeanException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class NoInjectorException extends BeanException {

    public NoInjectorException() {
    }

    public NoInjectorException(String message) {
        super(message);
    }

    public NoInjectorException(Throwable cause) {
        super(cause);
    }

    public NoInjectorException(String message, Throwable cause) {
        super(message, cause);
    }
}
