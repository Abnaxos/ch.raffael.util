package ch.raffael.util.cli;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;

import ch.raffael.util.common.collections.TokenMap;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ReflectionHandler implements CmdLineHandler {

    private static final Map<Class<?>, Converter> converters;
    static {
        Map<Class<?>, Converter> map = new HashMap<Class<?>, Converter>();
        map.put(String.class, new Converter() {
            @Override
            public Object convert(String string) throws CmdLineSyntaxException {
                return string;
            }
        });
        map.put(Integer.class, new AbstractNumberConverter("integer") {
            @Override
            protected Number convertImpl(String string) {
                return Integer.decode(string);
            }
        });
        map.put(int.class, map.get(Integer.class));
        map.put(Long.class, new AbstractNumberConverter("long") {
            @Override
            protected Number convertImpl(String string) {
                return Long.decode(string);
            }
        });
        map.put(long.class, map.get(Long.class));
        map.put(Short.class, new AbstractNumberConverter("short") {
            @Override
            protected Number convertImpl(String string) {
                return Short.decode(string);
            }
        });
        map.put(short.class, map.get(Short.class));
        map.put(Byte.class, new AbstractNumberConverter("byte") {
            @Override
            protected Number convertImpl(String string) {
                return Byte.decode(string);
            }
        });
        map.put(byte.class, map.get(Byte.class));
        map.put(Double.class, new AbstractNumberConverter("double") {
            @Override
            protected Number convertImpl(String string) {
                return Double.valueOf(string);
            }
        });
        map.put(double.class, map.get(Double.class));
        map.put(Float.class, new AbstractNumberConverter("float") {
            @Override
            protected Number convertImpl(String string) {
                return Float.valueOf(string);
            }
        });
        map.put(float.class, map.get(Float.class));
        map.put(BigDecimal.class, new AbstractNumberConverter("decimal") {
            @Override
            protected Number convertImpl(String string) {
                return new BigDecimal(string);
            }
        });
        map.put(BigInteger.class, new AbstractNumberConverter("big integer") {
            @Override
            protected Number convertImpl(String string) {
                return new BigInteger(string);
            }
        });
        map.put(Boolean.class, new Converter() {
            public Boolean convert(String string) throws CmdLineSyntaxException {
                Boolean bool = toBool(string);
                if ( bool == null ) {
                    throw new CmdLineSyntaxException("Invalid boolean value: " + string);
                }
                else {
                    return bool;
                }
            }
        });
        map.put(boolean.class, map.get(Boolean.class));
        converters = Collections.unmodifiableMap(map);
    }
    
    private static Map<Class<?>, Object> nullValues = new HashMap<Class<?>, Object>();
    static {
        nullValues.put(int.class, 0);
        nullValues.put(long.class, 0L);
        nullValues.put(short.class, (short)0);
        nullValues.put(byte.class, (byte)0);
        nullValues.put(float.class, 0.0f);
        nullValues.put(double.class, 0.0);
        nullValues.put(boolean.class, false);
    }

    private final Supplier<?> target;
    private final Method method;
    private final Method annotatedMethod;
    private final String name;
    private final String[] aliases;

    private final Class<?>[] types;
    private final Argument[] args;
    private final boolean hasPreferredLast;
    private final int outputIndex;


    public ReflectionHandler(Supplier<?> targetSupplier, Method method) {
        this(targetSupplier, method, method);
    }

    public ReflectionHandler(Supplier<?> targetSupplier, Method method, Method annotatedMethod) {
        this.target = targetSupplier;
        this.method = method;
        this.annotatedMethod = annotatedMethod;
        if ( (method.getModifiers() & Modifier.ABSTRACT) != 0 ) {
            throw new IllegalArgumentException("Method " + method + " is abstract");
        }
        if ( (method.getModifiers() & Modifier.PUBLIC) == 0 ) {
            throw new IllegalArgumentException("Method " + method + " is not public");
        }
        Command cmd = annotatedMethod.getAnnotation(Command.class);
        if ( cmd == null ) {
            throw new IllegalArgumentException("Method " + annotatedMethod + " not annotated with @Command");
        }
        name = cmd.name();
        aliases = cmd.alias();
        types = method.getParameterTypes();
        args = new Argument[types.length];
        int outputIndex = -1;
        Annotation[][] annotations = method.getParameterAnnotations();
        for ( int i = 0; i < annotations.length; i++ ) {
            Argument arg = null;
            for ( int j = 0; j < annotations[i].length; j++ ) {
                if ( annotations[i][j] instanceof Argument ) {
                    arg = (Argument)annotations[i][j];
                    break;
                }
            }
            if ( arg == null ) {
                if ( types[i].isAssignableFrom(PrintWriter.class) ) {
                    if ( outputIndex >= 0 ) {
                        throw new IllegalArgumentException("Duplicate output parameter in method " + annotatedMethod);
                    }
                    outputIndex = i;
                }
                else {
                    throw new IllegalArgumentException("Parameter " + i + " of method " + annotatedMethod + " is missing @Argument annotation");
                }
            }
            args[i] = arg;
            // FIXME: check annotation here
        }
        this.outputIndex = outputIndex;
        if ( args.length == 0 ) {
            hasPreferredLast = false;
        }
        else if ( args.length == 1 ) {
            if ( args[0] == null ) {
                hasPreferredLast = false;
            }
            else {
                hasPreferredLast = args[0].mode() == Argument.Mode.END_PREFER;
            }
        }
        else {
            int index = args.length - 1;
            if ( args[index] == null ) {
                index--;
            }
            hasPreferredLast = args[index].mode() == Argument.Mode.END_PREFER;
        }
        for ( int i = 0; i < args.length; i++ ) {
            if ( args[i] == null ) {
                continue;
            }
            if ( args[i].mode() != Argument.Mode.DEFAULT ) {
                if ( i != args.length - 1 ) {
                    throw new IllegalArgumentException("Mode " + args[i].mode() + " only supported for the last argument");
                }
                if ( types[i] != String.class ) {
                    throw new IllegalArgumentException("Mode " + args[i].mode() + " not supported for type " + types[i]);
                }
            }
            if ( types[i].isArray() ) {
                if ( types[i].getComponentType() == boolean.class || types[i].getComponentType() == Boolean.class ) {
                    throw new IllegalArgumentException("Argument " + i + ": Boolean arrays not supported");
                }
                if ( !converters.containsKey(types[i].getComponentType()) ) {
                    throw new IllegalArgumentException("Argument " + i + ": Unsupported class " + types[i].getComponentType());
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return Arrays.copyOf(aliases, aliases.length);
    }

    public Method getMethod() {
        return method;
    }

    public Class<?>[] getTypes() {
        return Arrays.copyOf(types, types.length);
    }    

    public Argument[] getArgs() {
        return args;
    }

    @Override
    public Mode command(PrintWriter output, TokenMap tokenMap, String prefix, String cmd) throws CmdLineSyntaxException {
        tokenMap.put(Object[].class, this, new Object[args.length]);
        return Mode.PARSE;
    }

    @Override
    public Mode value(PrintWriter output, TokenMap tokenMap, String name, String value) throws CmdLineSyntaxException {
        Object[] values = tokenMap.require(Object[].class, this);
        if ( name != null ) {
            int index = indexOfArg(name);
            if ( index < 0 ) {
                throw new CmdLineSyntaxException("No such argument: " + name);
            }
            if ( args[index].mode() == Argument.Mode.END || args[index].mode() == Argument.Mode.END_PREFER ) {
                return Mode.END_OF_LINE;
            }
            value(values, index, value);
            return Mode.PARSE;
        }
        else {
            // check for flag
            for ( int i = 0; i < args.length; i++ ) {
                Argument arg = args[i];
                if ( types[i] == Boolean.class || types[i] == boolean.class && !arg.requireName() ) {
                    if ( arg.name().equals(value) ) {
                        if ( values[i] != null ) {
                            throw new CmdLineSyntaxException("Duplicate value for argument " + arg.name());
                        }
                        values[i] = true;
                        return Mode.PARSE;
                    }
                    for ( String n : arg.alias() ) {
                        if ( n.equals(value) ) {
                            if ( values[i] != null ) {
                                throw new CmdLineSyntaxException("Duplicate value for argument " + arg.name());
                            }
                            values[i] = true;
                            return Mode.PARSE;
                        }
                    }
                }
            }
            if ( hasPreferredLast ) {
                // preferred last ...
                return Mode.END_OF_LINE;
            }
            for ( int i = 0; i < values.length; i++ ) {
                if ( args[i] == null ) {
                    continue;
                }
                Object val = values[i];
                if ( val != null ) {
                    if ( i == values.length - 1 && types[i].isArray() ) {
                        value(values, i, value);
                        return Mode.PARSE;
                    }
                    continue;
                }
                if ( args[i].requireName() ) {
                    continue;
                }
                //if ( types[i] == Boolean.class || types[i] == boolean.class ) {
                //    continue;
                //}
                if ( args[i].mode() == Argument.Mode.END || args[i].mode() == Argument.Mode.END_PREFER ) {
                    return Mode.END_OF_LINE;
                }
                else {
                    value(values, i, value);
                    return Mode.PARSE;
                }
            }
        }
        throw new CmdLineSyntaxException("Cannot match argument");
    }

    private void value(Object[] values, int index, String value) throws CmdLineSyntaxException {
        if ( values[index] != null ) {
            if ( types[index].isArray() ) {
                values[index] = append((Object[])values[index], converters.get(types[index].getComponentType()).convert(value));
            }
            else {
                throw new CmdLineSyntaxException("Too many values for argument " + name);
            }
        }
        else {
            values[index] = toValue(types[index], value);
        }
    }

    @Override
    public Mode value(PrintWriter output, TokenMap tokenMap, String name, String[] value) throws CmdLineSyntaxException {
        Object[] values = tokenMap.require(Object[].class, this);
        if ( value.length == 0 ) {
            return Mode.PARSE;
        }
        if ( value.length == 1 ) {
            return value(output, tokenMap, name, value[0]);
        }
        if ( name != null ) {
            int index = indexOfArg(name);
            if ( index < 0 ) {
                throw new CmdLineSyntaxException("No such argument: " + name);
            }
            assert args[index].mode() != Argument.Mode.END && args[index].mode() != Argument.Mode.END_PREFER;
            if ( !types[index].isArray() ) {
                throw new CmdLineSyntaxException("Too many values for argument " + name);
            }
            else {
                value(values, index, value);
            }
            return Mode.PARSE;
        }
        else {
            for ( int i = 0; i < values.length; i++ ) {
                Object val = values[i];
                if ( val != null ) {
                    if ( i == values.length - 1 && types[i].isArray() ) {
                        value(values, i, value);
                        return Mode.PARSE;
                    }
                    continue;
                }
                if ( args[i].requireName() ) {
                    continue;
                }
                if ( !types[i].isArray() ) {
                    continue;
                }
                value(values, i, value);
                return Mode.PARSE;
            }
        }
        throw new CmdLineSyntaxException("Cannot match argument");
    }

    private void value(Object[] values, int index, String[] value) throws CmdLineSyntaxException {
        Object[] list = toValue(types[index].getComponentType(), value);
        if ( values[index] != null ) {
            values[index] = join((Object[])values[index], list);
        }
        else {
            values[index] = list;
        }
    }

    @Override
    public void end(PrintWriter output, TokenMap tokenMap, String endOfLine) throws Exception {
        Object[] values = tokenMap.require(Object[].class, this);
        try {
            if ( endOfLine != null ) {
                assert args.length > 0 && (args[args.length - 1].mode() == Argument.Mode.END || args[args.length - 1].mode() == Argument.Mode.END_PREFER) :
                        "Unexpected end argument";
                assert values[args.length - 1] == null : "Duplicate end argument";
                values[args.length - 1] = toValue(types[args.length - 1], endOfLine);
            }
            for ( int i = 0; i < args.length; i++ ) {
                if ( args[i] == null ) {
                    values[i] = output;
                }
                else {
                    if ( args[i].required() && values[i] == null ) {
                        throw new CmdLineSyntaxException("Argument " + args[i].name() + " is required");
                    }
                    if ( types[i].isPrimitive() && values[i] == null ) {
                        values[i] = nullValues.get(types[i]);
                        assert values[i] != null : "No null value for primitive type " + types[i];
                    }
                }

            }
            if ( outputIndex >= 0 ) {
                values[outputIndex] = output;
            }
            method.invoke(target.get(), values);
        }
        catch ( InvocationTargetException e ) {
            Throwables.propagateIfPossible(e.getTargetException(), Exception.class);
            throw new CmdLineException("Undeclared exception invoking " + method, e.getTargetException());
        }
        catch ( IllegalAccessException e ) {
            throw new CmdLineException("Unexpected reflection error invoking " + method, e);
        }
    }

    private Object toValue(Class<?> type, String string) throws CmdLineSyntaxException {
        if ( type.isArray() ) {
            Object[] value = (Object[])Array.newInstance(type.getComponentType(), 1);
            value[0] = converters.get(type.getComponentType()).convert(string);
            return value;
        }
        else {
            return converters.get(type).convert(string);
        }
    }

    private Object[] toValue(Class<?> type, String[] strings) throws CmdLineSyntaxException {
        Object[] value = (Object[])Array.newInstance(type, strings.length);
        Converter conv = converters.get(type);
        for ( int i = 0; i < strings.length; i++ ) {
            value[i] = conv.convert(strings[i]);
        }
        return value;
    }

    private Object[] join(Object[] one, Object[] two) {
        Object[] join = (Object[])Array.newInstance(one.getClass().getComponentType(), one.length + two.length);
        System.arraycopy(one, 0, join, 0, one.length);
        System.arraycopy(two, 0, join, one.length, two.length);
        return join;
    }

    private Object[] append(Object[] array, Object elem) {
        Object[] join = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length + 1);
        System.arraycopy(array, 0, join, 0, array.length);
        join[join.length - 1] = elem;
        return join;
    }

    private int indexOfArg(String argName) {
        return indexOfArg(0, argName);
    }

    private int indexOfArg(int start, String argName) {
        int index = start;
        for ( Argument arg : args ) {
            if ( arg == null ) {
                index++;
                continue;
            }
            if ( arg.name().equals(argName) ) {
                return index;
            }
            else if ( arg.alias() != null && arg.alias().length > 0 ) {
                for ( String alias : arg.alias() ) {
                    if ( alias.equals(argName) ) {
                        return index;
                    }
                }
            }
            index++;
        }
        return -1;
    }

    @Override
    public void help(PrintWriter output) {
        Command command = annotatedMethod.getAnnotation(Command.class);
        if ( command == null ) {
            output.println("No help available.");
            return;
        }
        output.print(command.name().toLowerCase());
        String[] aliases = getAliases();
        for ( String alias : aliases ) {
            output.print(", ");
            output.print(alias.toLowerCase());
        }
        output.println();
        printDescription(output, "", command.doc());
        if ( args.length > 1 || (args.length == 1 && args[0] != null) ) {
            output.println();
            output.println("Arguments:");
            for ( Argument arg : args ) {
                if ( arg == null ) {
                    continue;
                }
                output.print("  ");
                output.print(arg.name().toLowerCase());
                for ( String alias : arg.alias() ) {
                    output.print(", ");
                    output.print(alias.toLowerCase());
                }
                StringBuilder flags = new StringBuilder();
                if ( arg.required() ) {
                    appendFlag(flags, "required");
                }
                if ( arg.requireName() ) {
                    appendFlag(flags, "name required");
                }
                if ( arg.mode() == Argument.Mode.END || arg.mode() == Argument.Mode.END_PREFER ) {
                    appendFlag(flags, "final");
                }
                if ( flags.length() > 0 ) {
                    output.print(" (");
                    output.print(flags);
                    output.println(")");
                }
                printDescription(output, "    ", arg.doc());
            }
        }
    }

    private void printAliases(PrintWriter output, String[] aliases) {
        if ( aliases.length > 0 ) {
            Arrays.sort(aliases, String.CASE_INSENSITIVE_ORDER);
            output.print(" ");
            for ( int i = 0; i < aliases.length; i++ ) {
                if ( i > 0 ) {
                    output.print(", ");
                }
                output.print(aliases[i].toLowerCase());
            }
        }
    }

    private static void appendFlag(StringBuilder buf, String flag) {
        if ( buf.length() > 0 ) {
            buf.append(", ");
        }
        buf.append(flag);
    }

    private static void printDescription(PrintWriter output, String prefix, String description) {
        description = description.trim();
        if ( description.isEmpty() ) {
            output.print(prefix);
            output.println("No description available");
            return;
        }
        for ( String l : Splitter.on(CharMatcher.is('\n')).split(description) ) {
            output.print(prefix);
            output.println(l);
        }
    }

    private void printAliases(String[] aliases) {
        
    }

    private static Boolean toBool(String string) {
        String s = string.toLowerCase(Locale.US).trim();
        if ( s.equals("true") || s.equals("yes") || s.equals("on") ) {
            return true;
        }
        if ( s.equals("false") || s.equals("no") || s.equals("off") ) {
            return false;
        }
        else {
            return null;
        }
    }

    private static interface Converter {
        Object convert(String string) throws CmdLineSyntaxException;
    }

    private static abstract class AbstractNumberConverter implements Converter {
        private final String typeName;
        protected AbstractNumberConverter(String typeName) {
            this.typeName = typeName;
        }
        @Override
        public Object convert(String string) throws CmdLineSyntaxException {
            try {
                return convertImpl(string.trim());
            }
            catch ( NumberFormatException e ) {
                throw new CmdLineSyntaxException("Invalid " + typeName + " value: " + string);
            }
        }
        protected abstract Number convertImpl(String string);
    }

    private static enum Tokens {
        VALUES, 
    }

}
