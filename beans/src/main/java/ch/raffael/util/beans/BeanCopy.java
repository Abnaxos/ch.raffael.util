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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BeanCopy {

    private static final Map<Class<?>, Map<String, PropertyDescriptor>> propertyCache = new HashMap<Class<?>, Map<String, PropertyDescriptor>>();

    private BeanCopy() {
    }

    public static void copy(@NotNull Object source, @NotNull Object target) {
        Map<String, PropertyDescriptor> sourceProps = getCopyableProperties(source.getClass());
        Map<String, PropertyDescriptor> targetProps = getCopyableProperties(target.getClass());
        for ( PropertyDescriptor sourceProp : sourceProps.values() ) {
            if ( sourceProp.getReadMethod() != null ) {
                PropertyDescriptor targetProp = targetProps.get(sourceProp.getName());
                if ( targetProp != null && targetProp.getWriteMethod() != null ) {
                    try {
                        targetProp.getWriteMethod().invoke(target, sourceProp.getReadMethod().invoke(source));
                    }
                    catch ( IllegalAccessException e ) {
                        throw new IllegalArgumentException("Error copying bean " + source + " to " + target, e);
                    }
                    catch ( InvocationTargetException e ) {
                        throw new IllegalArgumentException("Error copying bean " + source + " to " + target, e);
                    }
                }
            }
        }
    }

    public static Map<String, PropertyDescriptor> getCopyableProperties(@NotNull Class<?> clazz) {
        synchronized ( propertyCache ) {
            Map<String, PropertyDescriptor> props = propertyCache.get(clazz);
            if ( props == null ) {
                props = new HashMap<String, PropertyDescriptor>();
                Set<String> excluded = new HashSet<String>();
                excluded.add("class");
                Class<?> currentClass = clazz;
                while ( currentClass != Object.class ) {
                    Exclude annotation = currentClass.getAnnotation(Exclude.class);
                    if ( annotation != null ) {
                        excluded.addAll(Arrays.asList(annotation.value()));
                    }
                    currentClass = currentClass.getSuperclass();
                }
                try {
                    for ( PropertyDescriptor p : Introspector.getBeanInfo(clazz).getPropertyDescriptors() ) {
                        if ( !excluded.contains(p.getName()) ) {
                            props.put(p.getName(), p);
                        }
                    }
                }
                catch ( IntrospectionException e ) {
                    throw new IllegalArgumentException("Error introspecting class " + clazz);
                }
                propertyCache.put(clazz, Collections.unmodifiableMap(props));
            }
            return props;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    public static @interface Exclude {
        String[] value();
    }

    // FIXME: implement this
    //@Retention(RetentionPolicy.RUNTIME)
    //@Target(ElementType.TYPE)
    //@Documented
    //public static @interface DeepCopy {
    //    String[] value();
    //}

}
