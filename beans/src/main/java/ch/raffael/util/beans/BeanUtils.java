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

package ch.raffael.util.beans;

import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.WeakHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BeanUtils {

    private static final Map<CacheKey, EventSetDescriptor> EVENT_SET_CACHE = new WeakHashMap<CacheKey, EventSetDescriptor>();
    private static final Map<CacheKey, PropertyDescriptor> PROPERTY_CACHE = new WeakHashMap<CacheKey, PropertyDescriptor>();

    private BeanUtils() {
    }

    public static void addPropertyChangeListener(Object bean, PropertyChangeListener listener) {
        if ( bean == null || listener == null ) {
            return;
        }
        if ( bean instanceof Observable ) {
            ((Observable)bean).addPropertyChangeListener(listener);
        }
        else {
            try {
                EventSetDescriptor propertyChange = getEventSetDescriptor(bean, "propertyChange");
                if ( propertyChange == null ) {
                    throw new BeanException(bean.getClass() + " does not seem to support propertyChange events");
                }
                propertyChange.getAddListenerMethod().invoke(bean, listener);
            }
            catch ( InvocationTargetException e ) {
                throw new BeanException("Error adding listener", e);
            }
            catch ( IllegalAccessException e ) {
                throw new BeanException("Error adding listener", e);
            }
        }
    }

    public static void removePropertyChangeListener(Object bean, PropertyChangeListener listener) {
        if ( bean == null || listener == null ) {
            return;
        }
        if ( bean instanceof Observable ) {
            ((Observable)bean).removePropertyChangeListener(listener);
        }
        else {
            try {
                EventSetDescriptor propertyChange = getEventSetDescriptor(bean, "propertyChange");
                if ( propertyChange == null ) {
                    throw new BeanException(bean.getClass() + " does not seem to support propertyChange events");
                }
                propertyChange.getRemoveListenerMethod().invoke(bean, listener);
            }
            catch ( InvocationTargetException e ) {
                throw new BeanException("Error removing listener", e);
            }
            catch ( IllegalAccessException e ) {
                throw new BeanException("Error removing listener", e);
            }
        }
    }

    @Nullable
    public static EventSetDescriptor getEventSetDescriptor(Object bean, String name) {
        EventSetDescriptor eventSet = null;
        CacheKey key = new CacheKey(bean.getClass(), name);
        synchronized ( EVENT_SET_CACHE ) {
            if ( !EVENT_SET_CACHE.containsKey(key) ) {
                BeanInfo info;
                try {
                    info = Introspector.getBeanInfo(bean.getClass());
                }
                catch ( IntrospectionException e ) {
                    throw new BeanException("Error introspecting " + bean.getClass(), e);
                }
                for ( EventSetDescriptor evt : info.getEventSetDescriptors() ) {
                    if ( evt.getName().equals(name) ) {
                        eventSet = evt;
                        break;
                    }
                }
                EVENT_SET_CACHE.put(key, eventSet);
            }
            else {
                eventSet = EVENT_SET_CACHE.get(key);
            }
        }
        return eventSet;
    }

    @NotNull
    public static EventSetDescriptor requireEventSetDescriptor(@NotNull Object bean, @NotNull String name) {
        EventSetDescriptor result = getEventSetDescriptor(bean, name);
        if ( result == null ) {
            throw new BeanException(bean.getClass() + " has no event set " + name);
        }
        return result;
    }

    @Nullable
    public static PropertyDescriptor getPropertyDescriptor(@NotNull Object bean, @NotNull String name) {
        PropertyDescriptor property = null;
        CacheKey key = new CacheKey(bean.getClass(), name);
        synchronized ( PROPERTY_CACHE ) {
            if ( !PROPERTY_CACHE.containsKey(key) ) {
                BeanInfo info;
                try {
                    info = Introspector.getBeanInfo(bean.getClass());
                }
                catch ( IntrospectionException e ) {
                    throw new BeanException("Error introspecting " + bean.getClass(), e);
                }
                for ( PropertyDescriptor prop : info.getPropertyDescriptors() ) {
                    if ( prop.getName().equals(name) ) {
                        property = prop;
                        break;
                    }
                }
                PROPERTY_CACHE.put(key, property);
            }
            else {
                property = PROPERTY_CACHE.get(key);
            }
        }
        return property;
    }

    @NotNull
    public static PropertyDescriptor requirePropertyDescriptor(@NotNull Object bean, @NotNull String name) {
        PropertyDescriptor result = getPropertyDescriptor(bean, name);
        if ( result == null ) {
            throw new BeanException(bean.getClass() + " has no property " + name);
        }
        return result;
    }

    public static void checkReadable(Object bean, PropertyDescriptor descriptor) {
        if ( descriptor.getReadMethod() == null ) {
            throw new BeanException("Property " + descriptor.getName()+ " of " + bean.getClass() + " is not readable");
        }
    }

    public static void checkWritable(Object bean, PropertyDescriptor descriptor) {
        if ( descriptor.getWriteMethod() == null ) {
            throw new BeanException("Property " + descriptor.getName()+ " of " + bean.getClass() + " is not writable");
        }
    }

    public static Object getProperty(Object bean, String name) {
        return getProperty(bean, requirePropertyDescriptor(bean, name));
    }

    public static Object getProperty(Object bean, PropertyDescriptor descriptor) {
        checkReadable(bean, descriptor);
        try {
            return descriptor.getReadMethod().invoke(bean);
        }
        catch ( IllegalAccessException e ) {
            throw new BeanException("Error reading property " + descriptor.getName() + " from " + bean, e);
        }
        catch ( InvocationTargetException e ) {
            throw new BeanException("Error reading property " + descriptor.getName() + " from " + bean, e.getTargetException());
        }
    }

    public static void setProperty(Object bean, String name, Object value) {
        setProperty(bean, requirePropertyDescriptor(bean, name), value);
    }

    public static void setProperty(Object bean, PropertyDescriptor descriptor, Object value) {
        checkWritable(bean, descriptor);
        try {
            descriptor.getWriteMethod().invoke(bean, value);
        }
        catch ( IllegalAccessException e ) {
            throw new BeanException("Error writing property " + descriptor.getName() + " from " + bean, e);
        }
        catch ( InvocationTargetException e ) {
            throw new BeanException("Error writing property " + descriptor.getName() + " from " + bean, e.getTargetException());
        }
    }

    private final static class CacheKey {
        private final Class<?> clazz;
        private final String name;
        private CacheKey(Class<?> clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }
        @Override
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            CacheKey that = (CacheKey)o;
            if ( !clazz.equals(that.clazz) ) {
                return false;
            }
            return name.equals(that.name);
        }
        @Override
        public int hashCode() {
            int result = clazz.hashCode();
            result = 31 * result + name.hashCode();
            return result;
        }
    }

}
