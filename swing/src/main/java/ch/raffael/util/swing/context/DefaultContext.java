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

import ch.raffael.util.beans.EventEmitter;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultContext implements Context {

    public static final Object INJECTOR_CACHE_KEY = new Object();

    private final Map<Key, Object> objects = new HashMap<Key, Object>();

    private final EventEmitter<ContextListener> contextEvents = EventEmitter.newEmitter(ContextListener.class);
    private final Context parent;

    DefaultContext(boolean dummyForRoot) {
        this.parent = null;
    }

    public DefaultContext() {
        this(ContextManager.getInstance().getRoot());
    }

    public DefaultContext(@NotNull Context parent) {
        this.parent = parent;
        parent.addContextListener(new WeakContextListener(new ParentContextListener()));
    }

    @Override
    public void addContextListener(ContextListener listener) {
        contextEvents.addListener(listener);
    }

    @Override
    public void removeContextListener(ContextListener listener) {
        contextEvents.removeListener(listener);
    }

    @Override
    public ContextListener[] getContextListeners() {
        return contextEvents.getListeners();
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
        return (T)objects.get(new Key(clazz, key));
    }

    @Override
    public <T> T remove(@NotNull Class<T> clazz) {
        return remove(clazz, null);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> T remove(@NotNull Class<T> clazz, @Nullable Object key) {
        T oldValue = (T)objects.remove(new Key(clazz, key));
        if ( oldValue != null ) {
            Object newValue = find(clazz, key);
            if ( !oldValue.equals(newValue) ) {
                contextEvents.emitter().contextChanged(new ContextEvent(DefaultContext.this, clazz, key, oldValue, newValue));
            }
        }
        return oldValue;
    }

    @Override
    public <T> T put(@NotNull Class<T> clazz, @NotNull T value) {
        return put(clazz, null, value);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public synchronized <T> T put(@NotNull Class<T> clazz, @Nullable Object key, @NotNull T value) {
        T oldValue = (T)objects.put(new Key(clazz, key), value);
        if ( oldValue == null ) {
            // we didn't override, check whether our parent provided something
            if ( parent != null ) {
                oldValue = parent.find(clazz, key);
            }
        }
        if ( oldValue == null || !oldValue.equals(value) ) {
            contextEvents.emitter().contextChanged(new ContextEvent(DefaultContext.this, clazz, key, oldValue, value));
        }
        return oldValue;
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

    @Override
    public Context create(@NotNull Component attachTo) {
        Context ctx = create();
        ContextManager.getInstance().map(ctx, attachTo);
        return ctx;
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

    private class ParentContextListener implements ContextListener {
        @Override
        public void contextChanged(ContextEvent event) {
            Object mine = get(event.getType(), event.getKey());
            if ( mine == null ) {
                contextEvents.emitter().contextChanged(new ContextEvent(DefaultContext.this, event.getType(), event.getKey(), event.getOldValue(), event.getNewValue()));
            }
        }
    }
    
}
