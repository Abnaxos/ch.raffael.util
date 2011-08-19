package ch.raffael.util.common;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.common.annotations.Utility;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Utility
public class Classes {

    private static final GetCallerSecurityManager GET_CALLER = new GetCallerSecurityManager();

    private Classes() {
    }

    @NotNull
    public static Class<?> callerClass(@NotNull Class<?> base) {
        return GET_CALLER.getCallerClass(base);
    }

    @NotNull
    public static ClassLoader classLoader(@Nullable ClassLoader explicit, @NotNull Class<?> fallback) {
        if ( explicit != null ) {
            return explicit;
        }
        ClassLoader thread = Thread.currentThread().getContextClassLoader();
        if ( thread != null ) {
            return thread;
        }
        return fallback.getClassLoader();
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
            throw new IllegalStateException("Cannot determine caller from context " + Arrays.asList(context));
        }
    }


}
