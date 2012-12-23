package ch.raffael.util.common;

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.common.annotations.Utility;

import static com.google.common.base.Preconditions.*;
import static java.util.Arrays.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Utility
public final class Classes {

    private static final GetCallerSecurityManager GET_CALLER = new GetCallerSecurityManager();

    private Classes() {
    }

    @NotNull
    public static Class<?> callerClass(@NotNull Class<?> base) {
        return GET_CALLER.getCallerClass(base);
    }

    @NotNull
    public static ClassLoader classLoader() {
        return classLoader(null, null);
    }

    @NotNull
    public static ClassLoader classLoader(@Nullable Class fallback) {
        return classLoader(null, fallback);
    }

    @NotNull
    public static ClassLoader classLoader(@Nullable ClassLoader explicit) {
        return classLoader(explicit, null);
    }

    @NotNull
    public static ClassLoader classLoader(@Nullable ClassLoader explicit, @Nullable Class<?> fallback) {
        if ( explicit != null ) {
            return explicit;
        }
        ClassLoader thread = Thread.currentThread().getContextClassLoader();
        if ( thread != null ) {
            return thread;
        }
        if ( fallback == null ) {
            fallback = callerClass(Classes.class);
        }
        return fallback.getClassLoader();
    }

    /**
     * Allows working with class names and their different notations styles.
     */
    public static enum NameStyle {

        /**
         * The natural style is the style used in source code. Primitives can be
         * referenced by name ('int'), arrays by appending '[]', inner classes using
         * 'Outer.Inner'.
         */
        NATURAL {
            @Override
            public Class<?> forName(ClassLoader loader, String name) throws ClassNotFoundException {
                if ( loader == null ) {
                    loader = callerClass(NameStyle.class).getClassLoader();
                }
                String search = name;
                int arrayDepth = 0;
                int pos = name.length() - 2;
                while ( pos > 0 && isArrayIndicator(search, pos) ) {
                    arrayDepth++;
                    pos -= 2;
                }
                if ( arrayDepth > 0 ) {
                    search = name.substring(0, name.length() - arrayDepth * 2);
                }
                Class clazz = primitivesByName.get(search);
                if ( clazz == null ) {
                    while ( true ) {
                        try {
                            clazz = Class.forName(search, false, loader);
                            break;
                        }
                        catch ( ClassNotFoundException e ) {
                            pos = search.lastIndexOf('.');
                            if ( pos < 0 ) {
                                throw new ClassNotFoundException("For natural name: " + name);
                            }
                            else {
                                search = search.substring(0, pos) + "$" + search.substring(pos + 1);
                            }
                        }
                    }
                }
                if ( arrayDepth > 0 ) {
                    try {
                        return Class.forName(BINARY.nameFor(clazz, arrayDepth), false, loader);
                    }
                    catch ( ClassNotFoundException e ) {
                        // can't happen, actually
                        throw new ClassNotFoundException(name, e);
                    }
                }
                else {
                    return clazz;
                }
            }

            @Override
            public String nameFor(Class<?> clazz, int arrayDepth) {
                checkArgument(arrayDepth >= 0, "arrayDepth>=0");
                checkArgument(clazz.getComponentType() == null, "clazz.getComponentType()==null");
                if ( arrayDepth == 0 ) {
                    if ( clazz.getEnclosingClass() == null ) {
                        return clazz.getName();
                    }
                    else {
                        return unwindInner(clazz, new StringBuilder()).toString();
                    }
                }
                else {
                    StringBuilder buf = new StringBuilder(clazz.getName().length() + arrayDepth * 2);
                    unwindInner(clazz, buf);
                    for ( int i = 0; i < arrayDepth; i++ ) {
                        buf.append("[]");
                    }
                    return buf.toString();
                }
            }
            private StringBuilder unwindInner(Class clazz, StringBuilder buf) {
                if ( clazz.getEnclosingClass() == null ) {
                    return buf.append(clazz.getName());
                }
                else {
                    return unwindInner(clazz.getEnclosingClass(), buf).append('.').append(clazz.getSimpleName());
                }
            }
        },

        /**
         * The binary style is the style used by <code>Class#forName()</code>. Generally,
         * <code>nameFor()</code> always returns what Class#getName() returns, and
         * <code>forName()</code> works exactly as <code>Class#forName()</code>. There's
         * one small exception: <code>forName()</code> will still recognise primitives by
         * their natural name.
         */
        BINARY {
            @Override
            public Class<?> forName(ClassLoader loader, String name) throws ClassNotFoundException {
                if ( name.length() == 0 ) {
                    throw new ClassNotFoundException("For binary name: " + name);
                }
                if ( name.startsWith("[") ) {
                    return Class.forName(name, false, loader);
                }
                else {
                    Class<?> clazz = primitivesByName.get(name);
                    if ( clazz == null ) {
                        clazz = Class.forName(name, false, loader);
                    }
                    return clazz;
                }
            }

            @Override
            public String nameFor(Class<?> clazz, int arrayDepth) {
                checkArgument(arrayDepth >= 0, "arrayDepth < 0");
                Preconditions.checkArgument(clazz.getComponentType() == null, "clazz.getComponentType() == null");
                if ( arrayDepth == 0 ) {
                    return clazz.getName();
                }
                else {
                    StringBuilder buf = new StringBuilder();
                    for ( int i = 0; i < arrayDepth; i++ ) {
                        buf.append('[');
                    }
                    if ( clazz.isPrimitive() ) {
                        buf.append(binaryPrimitives.get(clazz));
                    }
                    else {
                        buf.append('L').append(clazz.getName()).append(';');
                    }
                    return buf.toString();
                }
            }
        },

        /**
         * The JVM name style is the style used by the JVM internally.
         */
        JVM {
            @Override
            public Class<?> forName(ClassLoader loader, String name) throws ClassNotFoundException {
                int arrayDepth = 0;
                while ( arrayDepth < name.length() && name.charAt(arrayDepth) == '[' ) {
                    arrayDepth++;
                }
                if ( arrayDepth >= name.length() - 1 ) {
                    throw new ClassNotFoundException("By JVM name: " + name);
                }
                if ( name.endsWith(";") ) {
                    if ( name.charAt(arrayDepth) != 'L' ) {
                        throw new ClassNotFoundException("By JVM name: " + name);
                    }
                    String dotted = name.substring(arrayDepth + 1, name.length() - 1).replace('/', '.');
                    if ( arrayDepth == 0 ) {
                        try {
                            return Class.forName(dotted, false, loader);
                        }
                        catch ( ClassNotFoundException e ) {
                            throw new ClassNotFoundException("By JVM name: " + name);
                        }
                    }
                    else {
                        StringBuilder buf = new StringBuilder(dotted.length() + arrayDepth + 2);
                        for ( int i = 0; i < arrayDepth; i++ ) {
                            buf.append('[');
                        }
                        buf.append('L').append(dotted).append(';');
                        try {
                            return Class.forName(buf.toString(), false, loader);
                        }
                        catch ( ClassNotFoundException e ) {
                            throw new ClassNotFoundException("By JVM name: " + name);
                        }
                    }
                }
                else {
                    return Class.forName(name, false, loader);
                }
            }

            @Override
            public String nameFor(Class<?> clazz, int arrayDepth) {
                if ( clazz.isPrimitive() ) {
                    if ( arrayDepth == 0 ) {
                        return binaryPrimitives.get(clazz);
                    }
                    else {
                        StringBuilder buf = new StringBuilder();
                        for ( int i = 0; i < arrayDepth; i++ ) {
                            buf.append('[');
                        }
                        buf.append(binaryPrimitives.get(clazz));
                        return buf.toString();
                    }
                }
                else {
                    if ( arrayDepth == 0 ) {
                        return "L" + clazz.getName().replace('.', '/') + ";";
                    }
                    else {
                        StringBuilder buf = new StringBuilder(clazz.getName().length() + 2 * arrayDepth + 2);
                        for ( int i = 0; i < arrayDepth; i++ ) {
                            buf.append('[');
                        }
                        buf.append('L').append(clazz.getName().replace('.', '/')).append(';');
                        return buf.toString();
                    }
                }
            }

            @Override
            protected void sigNextParam(StringBuilder buf) {
            }

            @Override
            protected void sigReturnType(StringBuilder buf, Class<?> returnType, boolean leading, boolean hasMethodName) {
                buf.append(nameFor(returnType));
            }
        };

        private final static Map<String, Class<?>> primitivesByName;
        static {
            Map<String, Class<?>> map = new HashMap<String, Class<?>>();
            map.put("int", int.class);
            map.put("long", long.class);
            map.put("short", short.class);
            map.put("byte", byte.class);
            map.put("char", char.class);
            map.put("double", double.class);
            map.put("float", float.class);
            map.put("boolean", boolean.class);
            map.put("void", void.class);
            primitivesByName = map;
        }
        private final static Map<Class<?>, String> binaryPrimitives;
        static {
            Map<Class<?>, String> map = new HashMap<Class<?>, String>();
            map.put(int.class, "I");
            map.put(long.class, "J");
            map.put(short.class, "S");
            map.put(byte.class, "B");
            map.put(char.class, "C");
            map.put(double.class, "D");
            map.put(float.class, "F");
            map.put(boolean.class, "Z");
            map.put(void.class, "V");
            binaryPrimitives= map;
        }
        private final static Map<String, Class<?>> primitivesByBinary;
        static {
            Map<String, Class<?>> map = new HashMap<String, Class<?>>();
            map.put("I", int.class);
            map.put("J", long.class);
            map.put("S", short.class);
            map.put("B", byte.class);
            map.put("C", char.class);
            map.put("D", double.class);
            map.put("F", float.class);
            map.put("Z", boolean.class);
            map.put("V", void.class);
            primitivesByBinary = map;
        }

        private static boolean isArrayIndicator(String name, int pos) {
            return name.charAt(pos) == '[' && name.charAt(pos + 1) == ']';
        }

        public abstract Class<?> forName(ClassLoader loader, String name) throws ClassNotFoundException;

        public abstract String nameFor(Class<?> clazz, int arrayDepth);

        public final Class<?> forName(String name) throws ClassNotFoundException {
            return forName(callerClass(NameStyle.class).getClassLoader(), name);
        }

        public String nameFor(Class<?> clazz) {
            int arrayDepth = 0;
            while ( clazz.getComponentType() != null ) {
                clazz = clazz.getComponentType();
                arrayDepth++;
            }
            return nameFor(clazz, arrayDepth);
        }

        public final String convertTo(String name, NameStyle target) throws ClassNotFoundException {
            return convertTo(callerClass(NameStyle.class).getClassLoader(), name, target);
        }

        public String convertTo(ClassLoader loader, String name, NameStyle target) throws ClassNotFoundException {
            Class<?> clazz = forName(loader, name);
            if ( target == this ) {
                return name;
            }
            else {
                return target.nameFor(clazz);
            }
        }

        public String formatMethodSignature(String methodName, Class<?> returnType, Class<?>... paramTypes) {
            return formatMethodSignature(methodName, returnType, false, paramTypes);
        }

        public String formatMethodSignature(String methodName, Class<?> returnType, boolean leadingReturnType, Class<?>... paramTypes) {
            return formatMethodSignature(methodName, returnType, leadingReturnType, paramTypes != null && paramTypes.length > 0 ? asList(paramTypes) : null);
        }

        public String formatMethodSignature(String methodName, Class<?> returnType, Iterable<Class<?>> paramTypes) {
            return formatMethodSignature(methodName, returnType, false, paramTypes);
        }

        public String formatMethodSignature(String methodName, Class<?> returnType, boolean leadingReturnType, Iterable<Class<?>> paramTypes) {
            StringBuilder buf = new StringBuilder();
            if ( methodName != null ) {
                buf.append(methodName);
            }
            buf.append('(');
            if ( paramTypes != null ) {
                boolean first = true;
                for ( Class<?> paramType : paramTypes ) {
                    if ( first ) {
                        first = false;
                    }
                    else {
                        sigNextParam(buf);
                    }
                    buf.append(nameFor(paramType));
                }
            }
            buf.append(')');
            if ( returnType != null ) {
                sigReturnType(buf, returnType, leadingReturnType, methodName != null);
            }
            return buf.toString();
        }

        protected void sigNextParam(StringBuilder buf) {
            buf.append(',');
        }

        protected void sigReturnType(StringBuilder buf, Class<?> returnType, boolean leading, boolean hasMethodName) {
            if ( leading ) {
                if ( hasMethodName ) {
                    buf.insert(0, ' ');
                }
                buf.insert(0, nameFor(returnType));
            }
            else {
                buf.append(':').append(nameFor(returnType));
            }
        }

    }

    private final static class GetCallerSecurityManager extends SecurityManager {
        @SuppressWarnings({ "ForLoopReplaceableByForEach" })
        private Class<?> getCallerClass(Class<?> base) {
            Class<?>[] context = getClassContext();
            for ( int i = 0; i < context.length; i++ ) {
                if ( !context[i].equals(Classes.class) && !context[i].equals(GetCallerSecurityManager.class) && !context[i].equals(base) ) {
                    return context[i];
                }
            }
            throw new IllegalStateException("Cannot determine caller from context " + asList(context));
        }
    }

}
