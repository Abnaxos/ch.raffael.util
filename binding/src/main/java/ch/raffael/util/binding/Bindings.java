/*
 * Copyright 2011 Raffael Herzog
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

package ch.raffael.util.binding;

import java.lang.reflect.Array;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.common.UnreachableCodeException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Bindings {

    private Bindings() {
    }

    @Nullable
    public static <T> T getValue(@Nullable Binding<T> binding) {
        if ( binding == null ) {
            return null;
        }
        else {
            return binding.getValue();
        }
    }

    public static <T> void setValue(@Nullable Binding<T> binding, T value) {
        if ( binding != null ) {
            binding.setValue(value);
        }
    }

    // commented out for now, hopefully unused ...
    //@SuppressWarnings( { "unchecked" })
    //public static void setValueUnsafe(@Nullable Binding<?> binding, Object value) {
    //    if ( binding != null ) {
    //        ((Binding<Object>)binding).setValue(value);
    //    }
    //}

    public static boolean equal(@Nullable Object a, @Nullable Object b) {
        return equal(a, b, false);
    }

    public static boolean equal(@Nullable Object a, @Nullable Object b, boolean deepArrays) {
        if ( a == null || b == null ) {
            return a == b;
        }
        if ( deepArrays ) {
            if ( a.getClass().isArray() ) {
                // if one is an array and the other isn't, we can stop right here
                if ( !b.getClass().isArray() ) {
                    return false;
                }
                // if the two arrays have different lengths, we can stop right here
                if ( Array.getLength(a) != Array.getLength(b) ) {
                    return false;
                }
                // check primitive arrays
                if ( a.getClass().getComponentType().isPrimitive() ) {
                    if ( a.getClass().getComponentType() != b.getClass().getComponentType() ) {
                        return false;
                    }
                    // tried to order by frequency -- no idea, whether it's accurate ;)
                    if ( a.getClass().getComponentType() == int.class ) {
                        return Arrays.equals((int[])a, (int[])b);
                    }
                    else if ( a.getClass().getComponentType() == char.class ) {
                        return Arrays.equals((char[])a, (char[])b);
                    }
                    else if ( a.getClass().getComponentType() == long.class ) {
                        return Arrays.equals((long[])a, (long[])b);
                    }
                    else if ( a.getClass().getComponentType() == byte.class ) {
                        return Arrays.equals((byte[])a, (byte[])b);
                    }
                    else if ( a.getClass().getComponentType() == boolean.class ) {
                        return Arrays.equals((boolean[])a, (boolean[])b);
                    }
                    else if ( a.getClass().getComponentType() == double.class ) {
                        return Arrays.equals((double[])a, (double[])b);
                    }
                    else if ( a.getClass().getComponentType() == short.class ) {
                        return Arrays.equals((short[])a, (short[])b);
                    }
                    else if ( a.getClass().getComponentType() == float.class ) {
                        return Arrays.equals((float[])a, (float[])b);
                    }
                    else {
                        throw new UnreachableCodeException();
                    }
                }
                // some kind of Object[]
                for ( int i = 0; i < Array.getLength(a); i++ ) {
                    // we're recursively calling equal(): a) null-safe also for arrays, b) handles multi-dimensional arrays
                    if ( !equal(Array.get(a, i), Array.get(b, i)) ) {
                        return false;
                    }
                }
                return true;
            }
            if ( b.getClass().isArray() ) {
                // if b is an array but a wasn't, they're not equal; any other case has been handled above
                return false;
            }
        }
        // the standard case, just use equals()
        return a.equals(b);
    }

}
