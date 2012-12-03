package ch.raffael.util.cli;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CmdLineArgumentsException extends CmdLineException {

    public CmdLineArgumentsException() {
    }

    public CmdLineArgumentsException(String message) {
        super(message);
    }

    public CmdLineArgumentsException(Throwable cause) {
        super(cause);
    }

    public CmdLineArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
