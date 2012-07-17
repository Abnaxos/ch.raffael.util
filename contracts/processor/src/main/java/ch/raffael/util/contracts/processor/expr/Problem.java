package ch.raffael.util.contracts.processor.expr;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Problem {

    private final int line;
    private final int charInLine;
    private final Token token;
    private final String message;
    private final RecognitionException exception;

    public Problem(int line, int charInLine, String message) {
        this.line = line;
        this.charInLine = charInLine;
        this.message = message;
        token = null;
        exception = null;
    }

    public Problem(String message) {
        this.message = message;
        line = -1;
        charInLine = -1;
        token = null;
        exception = null;
    }

    public Problem(Token token, String message) {
        this.token = token;
        this.message = message;
        line = token.getLine();
        charInLine = token.getCharPositionInLine();
        exception = null;
    }

    public Problem(RecognitionException exception, String message) {
        this.exception = exception;
        this.message = message;
        this.line = exception.line;
        this.charInLine = exception.charPositionInLine;
        this.token = exception.token;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("At ");
        if ( line > 0 ) {
            buf.append(line);
            if ( charInLine >= 0 ) {
                buf.append(':').append(charInLine);
            }
        }
        else {
            buf.append("unknown position");
        }
        return buf.append(": ").append(message).toString();
    }

    public int getLine() {
        return line;
    }

    public int getCharInLine() {
        return charInLine;
    }

    public Token getToken() {
        return token;
    }

    public String getMessage() {
        return message;
    }

    public RecognitionException getException() {
        return exception;
    }
}
