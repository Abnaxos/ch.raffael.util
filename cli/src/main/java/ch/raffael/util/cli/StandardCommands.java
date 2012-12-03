package ch.raffael.util.cli;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

import ch.raffael.util.common.UnexpectedException;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class StandardCommands {

    public static final String PREFIX = "std";

    private static final Joiner TEXT_JOINER = Joiner.on(' ');
    private static final String HELP_HELP = "Print help about the specified command";
    private static final CommandHandler HELP_HELP_HANDLER = new ReflectionCommandHandler(null, helpMethod(), HELP_HELP);

    private final CommandDispatcher dispatcher;

    private static Method helpMethod() {
        try {
            return StandardCommands.class.getMethod("help", PrintWriter.class, boolean.class, String.class);
        }
        catch ( NoSuchMethodException e ) {
            throw new UnexpectedException(e);
        }
    }

    public StandardCommands() {
        this(null);
    }


    public StandardCommands(CommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public static void register(String prefix, CommandDispatcher dispatcher) {
        if ( dispatcher instanceof GroupedDispatcher ) {
            if ( prefix == null ) {
                prefix = PREFIX;
            }
            GroupedDispatcher groups = (GroupedDispatcher)dispatcher;
            CommandDispatcher group = groups.getGroups().get(prefix);
            if ( group == null ) {
                groups.addGroup(prefix, new DefaultDispatcher(new StandardCommands(dispatcher)));
            }
            else {
                if ( group instanceof DefaultDispatcher ) {
                    ((DefaultDispatcher)group).register(new StandardCommands(dispatcher));
                }
                else {
                    throw new IllegalArgumentException("Existing dispatcher for group " + prefix + " must be a default command dispatcher");
                }
            }
        }
        else if ( dispatcher instanceof DefaultDispatcher ) {
            ((DefaultDispatcher)dispatcher).register(new StandardCommands(dispatcher));
        }
        else {
            throw new IllegalArgumentException("Dispatcher must be either a GroupedCommandDispatcher or DefaultCommandDispatcher");
        }
    }

    public static void register(CommandDispatcher dispatcher) {
        register(null, dispatcher);
    }

    @Command(name = "print", alias = "echo", doc = "Print the given String")
    public void print(PrintWriter out,
                      @Argument(usage = "The text to print", required = true) String[] text) {
        if ( text == null ) {
            out.println();
        }
        else {
            out.println(TEXT_JOINER.join(text));
        }
    }

    @Command(name = "help", doc = HELP_HELP)
    public void help(PrintWriter out,
                     @Option(name="-list", usage = "List commands") boolean list,
                     @Argument(usage = "The command or name of the group") final String command) throws CmdLineException {
        if ( command == null ) {
            list = true;
        }
        if ( list ) {
            final boolean grouped = dispatcher instanceof GroupedDispatcher;
            if ( !grouped && command != null ) {
                throw new CmdLineArgumentsException("No such group: " + command);
            }
            List<CommandDescriptor> descriptions =
                    Ordering.from(new Comparator<CommandDescriptor>() {
                        @Override
                        public int compare(CommandDescriptor a, CommandDescriptor b) {
                            if ( grouped ) {
                                return ComparisonChain.start()
                                        .compare(group(a), group(b))
                                        .compare(command(a), command(b))
                                        .result();
                            }
                            else {
                                return a.getName().compareTo(b.getName());
                            }
                        }
                    }).sortedCopy(Iterables.filter(dispatcher.listCommands(), new Predicate<CommandDescriptor>() {
                        @Override
                        public boolean apply(CommandDescriptor input) {
                            return command == null || input.getName().startsWith(command + ":");
                        }
                    }));
            if ( descriptions.isEmpty() ) {
                throw new CmdLineArgumentsException("No such group: " + command);
            }
            String prevGroup = null;
            final int maxCmdWidth = grouped ? 15 : 17;
            String overlengthIndent = "";
            int cmdWidth = -1;
            for ( int i = 0; i < descriptions.size(); i++ ) {
                String group = null;
                if ( grouped ) {
                    group = group(descriptions.get(i));
                    if ( !group.equals(prevGroup) ) {
                        if ( i > 0 ) {
                            out.println();
                        }
                        out.println("Commands in group " + group + ":");
                        cmdWidth = -1;
                        prevGroup = group;
                    }
                }
                if ( cmdWidth < 0 ) {
                    // determine the command field width
                    for ( int j = i; j < descriptions.size(); j++ ) {
                        if ( grouped && !group(descriptions.get(j)).equals(group) ) {
                            break;
                        }
                        String cmd = command(descriptions.get(j));
                        if ( cmd.length() <= maxCmdWidth ) {
                            cmdWidth = Math.max(cmdWidth, cmd.length());
                            if ( cmdWidth > maxCmdWidth ) {
                                cmdWidth = maxCmdWidth;
                                break;
                            }
                        }
                    }
                    if ( cmdWidth < 0 ) {
                        cmdWidth = maxCmdWidth;
                    }
                    overlengthIndent = Strings.repeat(" ", grouped ? cmdWidth + 2 : cmdWidth);
                }
                String cmd = command(descriptions.get(i));
                if ( grouped ) {
                    out.print("  ");
                }
                if ( cmd.length() > cmdWidth ) {
                    out.println(cmd);
                    out.print(overlengthIndent);
                }
                else {
                    out.print(Strings.padEnd(cmd, cmdWidth, ' '));
                }
                out.print(" : ");
                out.println(descriptions.get(i).getDescription());
            }
        }
        else {
            //if ( command == null ) {
            //    out.println(HELP_HELP_HANDLER.getHelp("help"));
            //}
            //else {
            CommandDescriptor description = dispatcher.findCommand(command);
            if ( description == null ) {
                throw new NoSuchCommandException("No such command: " + command);
            }
            else {
                out.println(description.getHelp());
            }
            //}
        }
    }

    private String group(CommandDescriptor description) {
        int pos = description.getName().indexOf(':');
        if ( pos >= 0 ) {
            return description.getName().substring(0, pos);
        }
        else {
            return "";
        }
    }

    private String command(CommandDescriptor description) {
        int pos = description.getName().indexOf(':');
        if ( pos >= 0 ) {
            return description.getName().substring(pos+1);
        }
        else {
            return description.getName();
        }
    }
}
