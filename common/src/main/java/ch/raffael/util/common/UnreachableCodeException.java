package ch.raffael.util.common;

/**
 * Exception indicating that code has been reached which should under no circumstances
 * be reachable. Mainly used for silencing the compiler if it complains about some missing
 * return value. ;)
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UnreachableCodeException extends RuntimeException {

    public UnreachableCodeException() {
        super();
    }

    public UnreachableCodeException(String message) {
        super(message);
    }

    public UnreachableCodeException(Throwable cause) {
        super(cause);
    }

    public UnreachableCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
