package ch.raffael.util.cli;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class NoSuchCommandException extends CmdLineException {

    public NoSuchCommandException() {
    }

    public NoSuchCommandException(String message) {
        super(message);
    }

    public NoSuchCommandException(Throwable cause) {
        super(cause);
    }

    public NoSuchCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
