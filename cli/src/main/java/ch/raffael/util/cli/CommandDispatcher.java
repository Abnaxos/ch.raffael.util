package ch.raffael.util.cli;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import ch.raffael.util.common.collections.TokenMap;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CommandDispatcher {

    private final Map<CmdName, CmdEntry> commands = new HashMap<CmdName, CmdEntry>();

    public <T> void add(String prefix, Class<T> clazz, T target) {
        add(prefix, clazz, Suppliers.ofInstance(target));
    }

    public <T> void add(String prefix, Class<T> clazz, Supplier<T> supplier) {
        for ( Method method : clazz.getMethods() ) {
            Command cmd = method.getAnnotation(Command.class);
            if ( cmd == null ) {
                continue;
            }
            ReflectionHandler handler = new ReflectionHandler(supplier, method);
            add(prefix, cmd.name(), cmd.alias(), handler);
        }
    }

    public void add(String prefix, String cmd, CmdLineHandler handler) {
        add(prefix, cmd, null, handler);
    }

    public void add(String prefix, String cmd, String[] aliases, CmdLineHandler handler) {
        add(new CmdName(prefix, cmd), handler, false);
        if ( aliases != null && aliases.length > 0 ) {
            for ( String alias : aliases ) {
                add(new CmdName(prefix, alias), handler, true);
            }
        }
    }

    private void add(CmdName name, CmdLineHandler handler, boolean alias) {
        CmdEntry entry = commands.get(name);
        if ( entry == null ) {
            entry = new CmdEntry(handler, alias);
            commands.put(name, entry);
        }
        else {
            if ( !entry.override(handler, alias) ) {
                if ( !alias && name.prefix != null ) {
                    throw new IllegalArgumentException("Duplicate command: " + name);
                }
            }
        }
        if ( name.prefix != null ) {
            add(name.stripPrefix(), handler, alias);
        }
    }

    public CmdLineHandler get(String prefix, String command) {
        CmdEntry entry = commands.get(new CmdName(prefix, command));
        if ( entry == null ) {
            return null;
        }
        else {
            return entry.handler;
        }
    }

    public List<CmdName> listCommands() {
        return new ArrayList<CmdName>(commands.keySet());
    }

    public CmdLineHandler handler() {
        return new CmdLineHandler() {
            private CmdLineHandler delegate;
            @Override
            public Mode command(PrintWriter output, TokenMap tokenMap, String prefix, String cmd) throws Exception {
                CmdName cmdName = new CmdName(prefix, cmd);
                CmdEntry entry = commands.get(cmdName);
                if ( entry == null || entry.handler == null ) {
                    throw new CmdLineSyntaxException("No such command: " + cmdName);
                }
                delegate = entry.handler;
                return delegate.command(output, tokenMap, prefix, cmd);
            }
            @Override
            public Mode value(PrintWriter output, TokenMap tokenMap, String name, String value) throws Exception {
                return delegate.value(output, tokenMap, name, value);
            }
            @Override
            public Mode value(PrintWriter output, TokenMap tokenMap, String name, String[] value) throws Exception {
                return delegate.value(output, tokenMap, name, value);
            }
            @Override
            public void end(PrintWriter output, TokenMap tokenMap, String endOfLine) throws Exception {
                delegate.end(output, tokenMap, endOfLine);
            }
            @Override
            public void help(PrintWriter output) {
                delegate.help(output);
            }
        };
    }

    public static final class CmdName {
        private final String prefix;
        private final String cmd;
        public CmdName(String prefix, String cmd) {
            this.prefix = prefix;
            this.cmd = cmd;
        }
        @Override
        public String toString() {
            if ( prefix == null ) {
                return cmd;
            }
            else {
                return prefix + ":" + cmd;
            }
        }
        @Override
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            CmdName that = (CmdName)o;
            if ( !cmd.equals(that.cmd) ) {
                return false;
            }
            return !(prefix != null ? !prefix.equals(that.prefix) : that.prefix != null);
        }
        @Override
        public int hashCode() {
            int result = prefix != null ? prefix.hashCode() : 0;
            result = 31 * result + cmd.hashCode();
            return result;
        }
        private CmdName stripPrefix() {
            if ( prefix == null ) {
                return this;
            }
            else {
                return new CmdName(null, cmd);
            }
        }
        public String getPrefix() {
            return prefix;
        }
        public String getCmd() {
            return cmd;
        }
    }

    private final static class CmdEntry {
        private CmdLineHandler handler;
        private boolean alias;
        private CmdEntry(CmdLineHandler handler, boolean alias) {
            this.handler = handler;
            this.alias = alias;
        }
        private boolean override(CmdLineHandler handler, boolean alias) {
            if ( this.handler == null ) {
                if ( this.alias && !alias ) {
                    this.alias = false;
                    this.handler = handler;
                    return true;
                }
                else {
                    return false;
                }
            }
            else if ( this.alias ) {
                if ( !alias ) {
                    this.alias = false;
                    this.handler = handler;
                    return true;
                }
                else {
                    this.handler = null;
                    return false;
                }
            }
            else {
                if ( alias ) {
                    return false;
                }
                else {
                    this.handler = null;
                    return false;
                }
            }
        }
    }

}
