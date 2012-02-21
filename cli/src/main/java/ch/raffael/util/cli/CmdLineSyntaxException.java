package ch.raffael.util.cli;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CmdLineSyntaxException extends CmdLineException {

    public CmdLineSyntaxException() {
        super();
    }

    public CmdLineSyntaxException(String message) {
        super(message);
    }

    public CmdLineSyntaxException(Throwable cause) {
        super(cause);
    }

    public CmdLineSyntaxException(String message, Throwable cause) {
        super(message, cause);
    }
}
