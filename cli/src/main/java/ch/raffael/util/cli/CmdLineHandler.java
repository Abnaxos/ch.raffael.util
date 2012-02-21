package ch.raffael.util.cli;

import java.io.PrintWriter;

import ch.raffael.util.common.Token;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public interface CmdLineHandler {

    Mode command(PrintWriter output, Token token, String prefix, String cmd) throws Exception;
    Mode value(PrintWriter output, Token token, String name, String value) throws Exception;
    Mode value(PrintWriter output, Token token, String name, String[] value) throws Exception;
    void end(PrintWriter output, Token token, String endOfLine) throws Exception;
    void help(PrintWriter output);

    enum Mode {
        PARSE, END_OF_LINE
    }
}
