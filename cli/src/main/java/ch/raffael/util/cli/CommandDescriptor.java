package ch.raffael.util.cli;

import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class CommandDescriptor {

    private static final Joiner ALIAS_JOINER = Joiner.on(", ");

    private final String name;
    private final Set<String> aliases;
    private final CommandHandler handler;

    public CommandDescriptor(String name, String[] aliases, CommandHandler handler) {
        this(name, ImmutableSet.copyOf(aliases), handler);
    }

    public CommandDescriptor(String name, Iterable<String> aliases, CommandHandler handler) {
        this.name = name;
        this.aliases = ImmutableSet.copyOf(aliases);
        this.handler = handler;
    }

    @Override
    public String toString() {
        return "CommandDescriptor{" + name + ":" + handler + "}";
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        CommandDescriptor that = (CommandDescriptor)o;
        if ( !aliases.equals(that.aliases) ) {
            return false;
        }
        if ( !handler.equals(that.handler) ) {
            return false;
        }
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + aliases.hashCode();
        result = 31 * result + handler.hashCode();
        return result;
    }

    public String getName() {
        return name;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public CommandHandler getHandler() {
        return handler;
    }

    public String getDescription() {
        String help = handler.getHelp(name);
        int pos = help.indexOf('\n');
        if ( pos >= 0 ) {
            help = help.substring(0, pos);
        }
        return help;
    }

    public String getHelp() {
        StringBuilder buf = new StringBuilder(handler.getHelp(name));
        if ( !aliases.isEmpty() ) {
            buf.append("\n\nAliases: ");
            ALIAS_JOINER.appendTo(buf, aliases);
        }
        return buf.toString();
    }
}
