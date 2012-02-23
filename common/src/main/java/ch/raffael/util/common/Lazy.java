/*
 * Copyright 2012 Raffael Herzog
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.google.common.base.Function;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class Lazy<T> implements Serializable {
    private static final long serialVersionUID = 12022301L;

    private static final Object UNINITIALIZED = Uninitialized.UNINITIALIZED;

    private final boolean serializeValue;
    private transient volatile Object instance = UNINITIALIZED;

    protected Lazy() {
        this(false);
    }

    protected Lazy(boolean serializeValue) {
        this.serializeValue = serializeValue;
    }

    @SuppressWarnings({ "unchecked", "AccessToStaticFieldLockedOnInstance" })
    public T get() {
        if ( instance == UNINITIALIZED ) {
            synchronized ( this ) {
                if ( instance == UNINITIALIZED ) {
                    instance = createInstance();
                }
            }
        }
        return (T)instance;
    }

    protected abstract T createInstance();

    @SuppressWarnings("unchecked")
    public static <T> Function<Lazy<T>, T> function() {
        return new Function<Lazy<T>, T>() {
            @Override
            public T apply(Lazy<T> input) {
                if ( input == null ) {
                    return null;
                }
                else {
                    return input.get();
                }
            }
        };
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if ( serializeValue ) {
            out.writeObject(instance);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if ( serializeValue ) {
            instance = in.readObject();
        }
        else {
            instance = UNINITIALIZED;
        }
    }

    private static enum Uninitialized {
        UNINITIALIZED
    }

}
