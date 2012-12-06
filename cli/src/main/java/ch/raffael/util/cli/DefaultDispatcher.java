package ch.raffael.util.cli;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.SetMultimap;

import static java.lang.reflect.Modifier.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultDispatcher implements CommandDispatcher {

    private final Map<String, CommandDescriptor> commands = new HashMap<String, CommandDescriptor>();
    private final SetMultimap<String, CommandDescriptor> aliases = HashMultimap.create();

    public DefaultDispatcher() {
    }

    public DefaultDispatcher(Object... targets) {
        for ( Object t : targets ) {
            if ( t instanceof Class ) {
                registerStatic((Class)t);
            }
            else {
                register(t);
            }
        }
    }

    void register(Object target) {
        for ( Method method : target.getClass().getMethods() ) {
            if ( method.getAnnotation(Command.class) != null ) {
                register(target, method);
            }
        }
    }

    void registerStatic(Class clazz) {
        for ( Method method : clazz.getMethods() ) {
            if ( method.getAnnotation(Command.class) != null && isStatic(method.getModifiers()) ) {
                register(null, method);
            }
        }
    }

    void register(Object target, Method method) {
        if ( target == null && !isStatic(method.getModifiers()) ) {
            throw ReflectionCommandHandler.illegalMethod(method, "Method must be static if target is null");
        }
        if ( !isPublic(method.getModifiers()) ) {
            throw ReflectionCommandHandler.illegalMethod(method, "Method is not public");
        }
        Command cmd = method.getAnnotation(Command.class);
        if ( cmd == null ) {
            throw ReflectionCommandHandler.illegalMethod(method, "No @Command annotation found");
        }
        register(cmd.name(), Arrays.asList(cmd.alias()), new ReflectionCommandHandler(target, method, cmd.doc()));
    }

    void register(String name, Iterable<String> aliases, CommandHandler handler) {
        if ( commands.containsKey(name) ) {
            throw new IllegalStateException("Duplicate command: " + name);
        }
        CommandDescriptor description = new CommandDescriptor(name, aliases, handler);
        commands.put(name, description);
        for ( String alias : aliases ) {
            this.aliases.put(alias, description);
        }
    }

    void unregister(String name) {
        CommandDescriptor description = commands.remove(name);
        if ( description != null ) {
            for ( String alias : description.getAliases() ) {
                aliases.remove(alias, description);
            }
        }
    }

    @Override
    public CommandDescriptor findCommand(String command) {
        CommandDescriptor descriptor = commands.get(command);
        if ( descriptor == null ) {
            Set<CommandDescriptor> alias = aliases.get(command);
            if ( aliases.size() != 1 ) {
                return null;
            }
            descriptor = Iterables.getFirst(alias, null);
        }
        if ( descriptor != null ) {
            descriptor = aliasFilteredDescriptor(descriptor);
        }
        return descriptor;
    }

    @Override
    public Iterable<CommandDescriptor> listCommands() {
        return Iterables.transform(commands.values(), new Function<CommandDescriptor, CommandDescriptor>() {
            @Override
            public CommandDescriptor apply(CommandDescriptor input) {
                return aliasFilteredDescriptor(input);
            }
        });
    }

    private CommandDescriptor aliasFilteredDescriptor(CommandDescriptor input) {
        return new CommandDescriptor(input.getName(), Iterables.filter(input.getAliases(), new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return aliases.get(input).size() == 1;
            }
        }), input.getHandler());
    }
}
