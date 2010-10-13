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

package ch.raffael.util.swing.beans;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.EventListener;

import org.slf4j.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.beans.BeanException;
import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class WeakListeners {

    private final static Logger logger = LogUtil.getLogger();

    private WeakListeners() {
    }

    public static <T extends EventListener> T weakListener(@NotNull Class<?> iface, @NotNull T listener) {
        return weakListener(null, iface, listener);
    }

    @SuppressWarnings({ "unchecked" })
    public static <T extends EventListener> T weakListener(@Nullable ClassLoader loader, Class<?> iface, final T listener) {
        if ( loader == null ) {
            loader = iface.getClassLoader(); // FIXME: use listener.getClass().getClassLoader()?
        }
        return (T)Proxy.newProxyInstance(loader, new Class[] { iface }, new InvocationHandler() {
            private final WeakReference<T> ref = new WeakReference<T>(listener);
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                T listener = ref.get();
                if ( listener != null ) {
                    return method.invoke(listener, args);
                }
                else {
                    return null;
                }
            }
        });
    }

    public static Object addWeakListener(@NotNull ClassLoader loader, @NotNull final Object bean, @NotNull String eventSet, @NotNull Object listener) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            for ( EventSetDescriptor evt : beanInfo.getEventSetDescriptors() ) {
                if ( evt.getName().equals(eventSet) ) {
                    if ( !evt.getListenerType().isInstance(listener) ) {
                        throw new BeanException(listener + " does not implement " + evt.getListenerType().getName());
                    }
                    final Method removeMethod = evt.getRemoveListenerMethod();
                    Object weakListener = Proxy.newProxyInstance(loader, new Class<?>[]{evt.getListenerType()},
                                                                 new WeakInvocationHandler(bean, removeMethod));
                    try {
                        evt.getAddListenerMethod().invoke(bean, weakListener);
                    }
                    catch ( InvocationTargetException e ) {
                        throw new BeanException("Error adding listener for " + eventSet + " to " + bean, e);
                    }
                    catch ( IllegalAccessException e ) {
                        throw new BeanException("Error adding listener for " + eventSet + " to " + bean, e);
                    }
                    return weakListener;
                }
            }
            throw new BeanException("No event set " + eventSet + " in class " + bean.getClass().getName());
        }
        catch ( IntrospectionException e ) {
            throw new BeanException("Error introspecting " + bean.getClass(), e);
        }
    }

    private static class WeakInvocationHandler implements InvocationHandler {

        private final WeakReference<Object> ref;
        private final Object bean;
        private final Method removeMethod;

        public WeakInvocationHandler(Object bean, Method removeMethod) {
            this.bean = bean;
            this.removeMethod = removeMethod;
            ref = new WeakReference<Object>(bean);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object listener = ref.get();
            if ( listener == null ) {
                try {
                    removeMethod.invoke(bean, this);
                }
                catch ( Exception e ) {
                    if ( logger.isWarnEnabled() ) {
                        logger.warn("Error unregistering listener (method {}; object {}",
                                    new Object[] { removeMethod, bean, e });
                    }
                }
                return null;
            }
            else {
                return method.invoke(listener, args);
            }
        }
    }
}
