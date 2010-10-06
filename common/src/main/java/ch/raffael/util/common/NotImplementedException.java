package ch.raffael.util.common;

/**
 * Exception used for indicating that some feature has not been implemented yet. Useful
 * during development, especially because it enables searching specifically for
 * unimplemented features using the IDE.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class NotImplementedException extends RuntimeException {

    public NotImplementedException() {
        super();
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }
}
