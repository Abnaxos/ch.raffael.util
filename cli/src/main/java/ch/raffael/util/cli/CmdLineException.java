package ch.raffael.util.cli;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CmdLineException extends Exception {

    public CmdLineException() {
        super();
    }

    public CmdLineException(String message) {
        super(message);
    }

    public CmdLineException(Throwable cause) {
        super(cause);
    }

    public CmdLineException(String message, Throwable cause) {
        super(message, cause);
    }
}
