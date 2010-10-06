package ch.raffael.util.common;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.common.annotations.Utility;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Utility
public class GetCaller {

    private static final GetCallerSecurityManager GET_CALLER = new GetCallerSecurityManager();

    private GetCaller() {
    }

    @NotNull
    public static Class<?> getCallerClass(Class<?> base) {
        return GET_CALLER.getCallerClass(base);
    }


    private final static class GetCallerSecurityManager extends SecurityManager {
        @SuppressWarnings({ "ForLoopReplaceableByForEach" })
        private Class<?> getCallerClass(Class<?> base) {
            Class<?>[] context = getClassContext();
            for ( int i = 0; i < context.length; i++ ) {
                if ( !context[i].equals(GetCaller.class) && !context[i].equals(GetCallerSecurityManager.class) && !context[i].equals(base) ) {
                    return context[i];
                }
            }
            throw new IllegalStateException("Cannot determine caller from context " + Arrays.asList(context));
        }
    }


}
