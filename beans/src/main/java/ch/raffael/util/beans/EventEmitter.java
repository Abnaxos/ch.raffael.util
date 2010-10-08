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

package ch.raffael.util.beans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class EventEmitter<T extends EventListener> implements Iterable<T> {

    private final List<T> listeners = new CopyOnWriteArrayList<T>();
    private final T emitter;

    @SuppressWarnings({ "unchecked" })
    public EventEmitter(Class<T> clazz, ClassLoader loader) {
        emitter = (T)Proxy.newProxyInstance(loader, new Class<?>[] { clazz }, createInvocationHandler());
    }


    public static <T extends EventListener> EventEmitter<T> newEmitter(Class<T> listenerClass) {
        return newEmitter(listenerClass, listenerClass.getClassLoader());
    }

    @SuppressWarnings({ "unchecked" })
    public static <T extends EventListener> EventEmitter<T> newEmitter(Class<T> listenerClass, ClassLoader loader) {
        return new EventEmitter(listenerClass, loader);
    }

    public void addListener(T listener) {
        listeners.add(listener);
    }

    public void removeListener(T listener) {
        listeners.remove(listener);
    }

    public int getListenerCount() {
        return listeners.size();
    }

    public boolean isEmpty() {
        return listeners.isEmpty();
    }

    public Iterator<T> iterator() {
        return listeners.iterator();
    }

    public T emitter() {
        return emitter;
    }

    protected InvocationHandler createInvocationHandler() {
        return new EventInvocationHandler();
    }

    protected class EventInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            for ( T l : EventEmitter.this ) {
                invokeListener(l, method, args);
            }
            return null;
        }

        protected void invokeListener(T l, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
            method.invoke(l, args);
        }
    }

}
