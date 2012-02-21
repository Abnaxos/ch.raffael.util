package ch.raffael.util.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.ClosedByInterruptException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.raffael.util.common.Classes;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CliShell implements Runnable {

    protected final BufferedReader input;
    protected final PrintWriter output;
    protected final ExecutorService commandExecutor;

    private String newline = System.getProperty("line.separator");
    private CmdLineHandler handler;
    private Thread shellThread = null;

    public CliShell(Reader input, Writer output, ExecutorService commandExecutor) {
        if ( input instanceof BufferedReader ) {
            this.input = (BufferedReader)input;
        }
        else {
            this.input = new BufferedReader(input);
        }
        if ( output instanceof PrintWriter ) {
            this.output = (PrintWriter)output;
        }
        else {
            this.output = new PrintWriter(output);
        }
        this.commandExecutor = commandExecutor;
    }

    public String getNewline() {
        return newline;
    }

    public void setNewline(String newline) {
        this.newline = newline;
    }

    public CmdLineHandler getHandler() {
        return handler;
    }

    public void setHandler(CmdLineHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        synchronized ( this ) {
            if ( shellThread != null ) {
                throw new IllegalStateException("Shell already running");
            }
            shellThread = Thread.currentThread();
        }
        try {
            while ( !Thread.interrupted() ) {
                prompt(output);
                String command = null;
                command = input.readLine();
                if ( command == null ) {
                    break;
                }
                if ( command.trim().isEmpty() ) {
                    continue;
                }
                PipedWriter pipeOut = new PipedWriter();
                BufferedReader cmdIn = new BufferedReader(new PipedReader(pipeOut)); // IOException: fatal
                final PrintWriter cmdOut = new PrintWriter(pipeOut);
                final CmdLineParser parser = new CmdLineParser(command, cmdOut, handler());
                commandExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            parser.parse();
                        }
                        catch ( Exception e ) {
                            exception(output, e);
                        }
                        finally {
                            cmdOut.close();
                        }
                    }
                });
                while ( true ) {
                    if ( Thread.interrupted() ) {
                        throw new InterruptedException();
                    }
                    String line;
                    try {
                        line = cmdIn.readLine();
                    }
                    catch ( InterruptedIOException e ) {
                        throw e;
                    }
                    catch ( IOException e ) {
                        printLine(output, "Error reading output of command: " + e.toString());
                        cmdOut.close();
                        break;
                    }
                    if ( line == null ) {
                        break;
                    }
                    printLine(output, line);
                }
            }
        }
        catch ( InterruptedException e ) {
            // normal exit => clear interrupted flag to allow onExit() to run normally
            Thread.interrupted();
        }
        catch ( ClosedByInterruptException e ) {
            // normal exit => clear interrupted flag to allow onExit() to run normally
            Thread.interrupted();
        }
        catch ( InterruptedIOException e ) {
            // normal exit => clear interrupted flag to allow onExit() to run normally
            Thread.interrupted();
        }
        catch ( IOException e ) {
            fatal(e);
        }
        finally {
            synchronized ( this ) {
                shellThread = null;
            }
            onExit();
        }
    }

    protected CmdLineHandler handler() {
        return handler;
    }

    protected void prompt(PrintWriter out) throws IOException {
        out.write("> ");
        out.flush();
    }

    protected void printLine(PrintWriter out, String line) throws IOException {
        out.write(line);
        out.write(newline);
        out.flush();
    }

    protected void exception(PrintWriter out, Throwable exception) {
        if ( exception instanceof CmdLineException ) {
            out.println(exception.getLocalizedMessage());
        }
        else {
            exception.printStackTrace(out);
        }
    }

    @Command(name = "exit", alias = { "quit", "bye" }, doc = "Quit the current shell.")
    public void exit() throws IOException {
        synchronized ( shellThread ) {
            if ( shellThread != null ) {
                shellThread.interrupt();
            }
        }
    }

    protected void onExit() {
    }

    protected void fatal(Throwable e) {
        PrintWriter exceptionOut = new PrintWriter(output);
        exceptionOut.print("Fatal error in CLI shell: ");
        e.printStackTrace(exceptionOut);
        exceptionOut.flush();
    }

    public static void main(String[] args) throws Exception {
        TestShell shell = new TestShell();
        CommandDispatcher dispatcher = new CommandDispatcher();
        StandardCommands.register(dispatcher);
        dispatcher.add("test", TestCommands.class, new TestCommands());
        dispatcher.add("shell", TestShell.class, shell);
        shell.setHandler(dispatcher.handler());
        new Thread(shell).start();
    }

    public static class TestCommands {

        @Command(name = "throw", doc = "Throw an exception.")
        public void throwCmd(@Argument(name = "exception", required = true, doc = "The exception to throw") String excClass) throws Throwable {
            throw (Throwable)Class.forName(excClass, false, Classes.classLoader()).newInstance();
        }

        @Command(name = "a-Command-with-a-very-long-name-that-doesnt-even-do-anyting", doc = "Does nothing (testing help output)")
        public void longCommand() {
        }

    }

    private static class TestShell extends CliShell {

        private volatile boolean prefix = false;

        public TestShell() {
            super(new InputStreamReader(System.in), new OutputStreamWriter(System.out), Executors.newSingleThreadExecutor());
        }

        @Override
        protected void prompt(PrintWriter out) throws IOException {
            output.write("Test Shell");
            super.prompt(out);
        }

        @Override
        protected void printLine(PrintWriter out, String line) throws IOException {
            if ( prefix ) {
                output.write("< ");
            }
            super.printLine(out, line);
        }

        @Command(name="prefix")
        public void prefix(@Argument(name = "enable", required = true) boolean prefix) {
            this.prefix = prefix;
        }

    }
}
