/*
 * Copyright 2010 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
