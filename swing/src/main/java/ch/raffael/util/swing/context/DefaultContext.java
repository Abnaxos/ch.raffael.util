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

package ch.raffael.util.swing.context;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultContext implements Context {

    public static final Object INJECTOR_CACHE_KEY = new Object();

    private final Map<Key, Object> objects = new HashMap<Key, Object>();

    private final Context parent;

    DefaultContext(boolean dummyForRoot) {
        this.parent = null;
    }

    public DefaultContext() {
        this(ContextManager.getInstance().getRoot());
    }

    public DefaultContext(@NotNull Context parent) {
        this.parent = parent;
    }

    public Context getParent() {
        return parent;
    }

    @Override
    public <T> T find(@NotNull Class<T> clazz) {
        return find(clazz, null);
    }

    @Override
    public <T> T find(@NotNull Class<T> clazz, @Nullable Object key) {
        ContextManager mgr = ContextManager.getInstance();
        Context context = this;
        T result;
        synchronized ( mgr ) {
            while ( context != null ) {
                result = context.get(clazz, key);
                if ( result != null ) {
                    return result;
                }
                else {
                    context = context.getParent();
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    public <T> T require(@NotNull Class<T> clazz) {
        return require(clazz, null);
    }

    @NotNull
    @Override
    public <T> T require(@NotNull Class<T> clazz, @Nullable Object key) {
        T result = find(clazz, key);
        if ( result == null ) {
            throw new ContextException("No object found for " + clazz + ":" + key);
        }
        return result;
    }

    @Override
    public <T> T get(@NotNull Class<T> clazz) {
        return get(clazz, null);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> T get(@NotNull Class<T> clazz, @Nullable Object key) {
        synchronized ( ContextManager.getInstance() ) {
            return (T)objects.get(new Key(clazz, key));
        }
    }

    @Override
    public <T> T remove(@NotNull Class<T> clazz) {
        return remove(clazz, null);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> T remove(@NotNull Class<T> clazz, @Nullable Object key) {
        synchronized ( ContextManager.getInstance() ) {
            return (T)objects.remove(new Key(clazz, key));
        }
    }

    @Override
    public <T> T put(@NotNull Class<T> clazz, @NotNull T value) {
        return put(clazz, null, value);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public synchronized <T> T put(@NotNull Class<T> clazz, @Nullable Object key, @NotNull T value) {
        return (T)objects.put(new Key(clazz, key), value);
    }

    @Override
    public void attach(@NotNull Component component) {
        ContextManager.getInstance().map(this, component);
    }

    @Override
    public boolean detach(@NotNull Component component) {
        Context context = ContextManager.getInstance().get(component);
        while ( true ) {
            if ( context == this ) {
                ContextManager.getInstance().unmap(component);
                return true;
            }
            if ( context != null ) {
                return false;
            }
            component = component.getParent();
            //noinspection ConstantConditions
            if ( component == null ) {
                return false;
            }
        }
    }

    @Override
    public Context create() {
        return new DefaultContext(this);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> T instantiate(Class<T> clazz) {
        Map cache = require(Map.class, INJECTOR_CACHE_KEY);
        Injector<T> injector;
        synchronized ( cache ) {
            injector = (Injector<T>)cache.get(clazz);
            if ( injector == null ) {
                injector = new Injector<T>(clazz);
                cache.put(clazz, injector);
            }
        }
        T object = injector.instantiate(this);
        injector.initialize(this, object);
        return object;
    }

    private final static class Key {
        private final Class<?> clazz;
        private final Object key;
        private Key(Class<?> clazz, Object key) {
            this.clazz = clazz;
            this.key = key;
        }
        @Override
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            Key that = (Key)o;
            if ( !clazz.equals(that.clazz) ) {
                return false;
            }
            return !(key != null ? !key.equals(that.key) : that.key != null);
        }
        @Override
        public int hashCode() {
            int result = clazz.hashCode();
            result = 31 * result + (key != null ? key.hashCode() : 0);
            return result;
        }
    }
    
}
