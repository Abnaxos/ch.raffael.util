package ch.raffael.util.cli;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Strings;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class StandardCommands {

    public static final String PREFIX = "std";

    private final CommandDispatcher dispatcher;

    public StandardCommands() {
        this(null);
    }


    public StandardCommands(CommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public static void register(String prefix, CommandDispatcher dispatcher) {
        dispatcher.add(prefix == null ? PREFIX : prefix, StandardCommands.class, new StandardCommands(dispatcher));
    }

    public static void register(CommandDispatcher dispatcher) {
        register(null, dispatcher);
    }

    @Command(name = "print", alias = "echo", doc = "Print the given String")
    public void print(PrintWriter out,
               @Argument(name="text", mode= Argument.Mode.END, doc="The text to print") String text) {
        if ( text != null ) {
            out.println(text);
        }
    }

    @Command(name = "help", doc = "Print help about the specified command")
    public void help(PrintWriter out,
               @Argument(name="command", alias="cmd", doc="The command") String command) {
        if ( dispatcher == null ) {
            out.println("No help available");
        }
        else if ( command == null ) {
            out.println("help");
            out.println("Print help on an command.");
            out.println();
            out.println("Arguments:");
            out.println("  command");
            out.println("    The command to print help on");
            List<CommandDispatcher.CmdName> commands = dispatcher.listCommands();
            if ( commands.isEmpty() ) {
                out.println();
                out.println("(no commands available)");
                return;
            }
            Collections.sort(commands, new Comparator<CommandDispatcher.CmdName>() {
                @Override
                public int compare(CommandDispatcher.CmdName a, CommandDispatcher.CmdName b) {
                    if ( a.getPrefix() == null ) {
                        if ( b.getPrefix() != null ) {
                            return 1;
                        }
                    }
                    else if ( b.getPrefix() == null ) {
                        return -1;
                    }
                    else {
                        int result = a.getPrefix().compareTo(b.getPrefix());
                        if ( result != 0 ) {
                            return result;
                        }
                    }
                    return a.getCmd().compareTo(b.getCmd());
                }
            });
            String prevPrefix = null;
            if ( commands.get(0).getPrefix() == null ) {
                out.println();
                out.println("No prefix:");
            }
            int count = 0;
            for ( CommandDispatcher.CmdName name : commands ) {
                if ( !(prevPrefix == null ? name.getPrefix() == null : prevPrefix.equals(name.getPrefix())) ) {
                    // new prefix
                    prevPrefix = name.getPrefix();
                    out.println();
                    out.println();
                    if ( name.getPrefix() == null ) {
                        out.println("No prefix:");
                    }
                    else {
                        out.print("Prefix ");
                        out.print(name.getPrefix());
                        out.println(':');
                    }
                    count = 0;
                }
                else if ( count >= 4 ) {
                    out.println();
                    count = 0;
                }
                out.print(' ');
                if ( name.getCmd().length() > 18 ) {
                    out.print(name.getCmd().substring(0, 15));
                    out.print("...");
                }
                else {
                    out.print(Strings.padEnd(name.getCmd(), 18, ' '));
                }
                out.print(' ');
                count++;
            }
        }
        else {
            int pos = command.indexOf(':');
            String prefix = null;
            if ( pos >= 0 ) {
                prefix = command.substring(0, pos);
                command = command.substring(pos + 1);
            }
            CmdLineHandler handler = dispatcher.get(prefix, command);
            if ( handler == null ) {
                out.println("No such command: " + command);
            }
            else {
                handler.help(out);
            }
        }
    }
}
