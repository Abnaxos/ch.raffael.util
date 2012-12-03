package ch.raffael.util.cli;

import java.io.PrintWriter;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface CommandHandler {

    void execute(PrintWriter out, String[] args) throws Exception;

    /**
     * Get a help string for the command. The first line should be a short summary that
     * can be used e.g. in a listing of available commands, after that, the command is
     * free to provide further help information. The recommended help format is:
     *
     * <pre>A short description of the command.
     *
     * Usage: command -arg ARG ...
     *
     *   -arg ARG  Description of arg.</pre>
     *
     * @param name
     * @return
     */
    String getHelp(String name);

}
