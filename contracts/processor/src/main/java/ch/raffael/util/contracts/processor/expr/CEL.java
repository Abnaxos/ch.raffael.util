package ch.raffael.util.contracts.processor.expr;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CEL {

    public static CELParser newParser(String source, final ProblemReporter problemReporter) {
        ANTLRStringStream stream = new ANTLRStringStream(source);
        CELLexer lexer = new CELLexer(stream);
        return newParser(lexer, problemReporter);
    }

    public static CELParser newParser(final CELLexer lexer, final ProblemReporter problemReporter) {
        CELParser parser = new CELParser(new CommonTokenStream(lexer)) {
            @Override
            public void displayRecognitionError(String[] tokenNames, RecognitionException e) {
                String msg = getErrorMessage(e, tokenNames);
                if ( !msg.isEmpty() && !Character.isUpperCase(msg.charAt(1)) )
                    msg = Character.toUpperCase(msg.charAt(0))+msg.substring(1);
                problemReporter.report(new Problem(e, msg));
            }
        };
        parser.setTreeAdaptor(new CELTreeAdaptor());
        return parser;
    }

}
