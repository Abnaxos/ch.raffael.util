package ch.raffael.util.i18n;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class I18NException extends RuntimeException {

    public I18NException() {
        super();
    }

    public I18NException(String message) {
        super(message);
    }

    public I18NException(Throwable cause) {
        super(cause);
    }

    public I18NException(String message, Throwable cause) {
        super(message, cause);
    }
}
