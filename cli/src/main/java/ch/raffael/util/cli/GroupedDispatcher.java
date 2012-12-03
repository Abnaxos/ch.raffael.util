package ch.raffael.util.cli;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class GroupedDispatcher implements CommandDispatcher {

    private final Map<String, CommandDispatcher> groups = new HashMap<String, CommandDispatcher>();

    public void addGroup(String name, CommandDispatcher dispatcher) {
        if ( groups.containsKey(name) ) {
            throw new IllegalStateException("Duplicate group: " + name);
        }
        groups.put(name, dispatcher);
    }

    public void removeGroup(String name) {
        groups.remove(name);
    }

    public Map<String, CommandDispatcher> getGroups() {
        return Collections.unmodifiableMap(groups);
    }

    @Override
    public CommandDescriptor findCommand(String command) {
        int pos = command.indexOf(':');
        if ( pos >= 0 ) {
            CommandDispatcher group = groups.get(command.substring(0, pos));
            if ( group == null ) {
                return null;
            }
            else {
                return group.findCommand(command.substring(pos + 1));
            }
        }
        else {
            CommandDescriptor description = null;
            for ( CommandDispatcher group : groups.values() ) {
                CommandDescriptor found = group.findCommand(command);
                if ( found != null ) {
                    if ( description != null ) {
                        // ambiguous
                        return null;
                    }
                    else {
                        description = found;
                    }
                }
            }
            return description;
        }
    }

    @Override
    public Iterable<CommandDescriptor> listCommands() {
        return Iterables.concat(Maps.transformEntries(groups, new Maps.EntryTransformer<String, CommandDispatcher, Iterable<CommandDescriptor>>() {
            @Override
            public Iterable<CommandDescriptor> transformEntry(final String key, final CommandDispatcher value) {
                return Iterables.transform(value.listCommands(), new Function<CommandDescriptor, CommandDescriptor>() {
                    @Override
                    public CommandDescriptor apply(CommandDescriptor input) {
                        return new CommandDescriptor(key + ":" + input.getName(), input.getAliases(), input.getHandler());
                    }
                });
            }
        }).values());
    }
}
