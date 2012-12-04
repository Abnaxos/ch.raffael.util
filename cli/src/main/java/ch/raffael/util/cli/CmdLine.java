package ch.raffael.util.cli;

import java.util.Arrays;
import java.util.Collection;

import static com.google.common.base.Objects.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CmdLine {

    private final static String[] EMPTY_ARGS = new String[0];

    private String command;
    private String[] arguments;

    public CmdLine(String command) {
        this(command, EMPTY_ARGS);
    }

    public CmdLine(String command, String[] arguments) {
        this.command = command;
        this.arguments = firstNonNull(arguments, EMPTY_ARGS);
    }

    public CmdLine(String command, Collection<String> arguments) {
        this(command, toArray(arguments));
    }

    public CmdLine(String[] command) {
        if ( command == null || command.length < 1 ) {
            throw new IllegalArgumentException("Command line must contain at least one element");
        }
        this.command = command[0];
        if ( command.length > 1 ) {
            arguments = new String[command.length - 1];
            System.arraycopy(command, 1, this.arguments, 0, command.length - 1);
        }
        else {
            arguments = EMPTY_ARGS;
        }
    }

    public CmdLine(Collection<String> command) {
        this(toArray(command));
    }


    private static String[] toArray(Collection<String> arguments) {
        if ( arguments == null || arguments.isEmpty() ) {
            return EMPTY_ARGS;
        }
        else {
            return arguments.toArray(new String[arguments.size()]);
        }
    }

    @Override
    public String toString() {
        return "CmdLine{" + command + ":" + Arrays.asList(arguments) + "}";
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        CmdLine that = (CmdLine)o;
        if ( !Arrays.equals(arguments, that.arguments) ) {
            return false;
        }
        return command.equals(that.command);
    }

    @Override
    public int hashCode() {
        int result = command.hashCode();
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }
}
