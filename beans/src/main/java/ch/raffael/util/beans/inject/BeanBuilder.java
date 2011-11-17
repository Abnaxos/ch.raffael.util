package ch.raffael.util.beans.inject;

import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import org.slf4j.Logger;

import ch.raffael.util.beans.BeanException;
import ch.raffael.util.common.logging.LogUtil;

import static ch.raffael.util.beans.inject.Injector.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BeanBuilder {

    private static final Comparator<Constructor<?>> CONSTRUCTOR_COMPARATOR = new Comparator<Constructor<?>>() {
            @Override
            public int compare(Constructor<?> a, Constructor<?> b) {
                int aCount = a.getParameterTypes().length;
                int bCount = b.getParameterTypes().length;
                if ( aCount < bCount ) {
                    return 1;
                }
                else if ( aCount > bCount ) {
                    return -1;
                }
                else {
                    return 0;
                }
            }
        };

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private final LinkedList<Injector> injectors = new LinkedList<Injector>();
    private boolean injectProperties = true;

    public BeanBuilder inject(Injector injector) {
        injectors.add(injector);
        return this;
    }

    public BeanBuilder logger() {
        return inject(LoggerInjector.instance());
    }

    public BeanBuilder instance(Object instance) {
        return inject(new InstanceInjector(instance));
    }

    public <T> BeanBuilder instance(Class<T> type, T instance) {
        return inject(new InstanceInjector(type, instance));
    }

    public BeanBuilder filter(Place place) {
        injectors.add(new FilterByPlace(injectors.removeLast(), place));
        return this;
    }

    public BeanBuilder noProperties() {
        injectProperties = false;
        return this;
    }

    public <T> T newInstance(Class<T> beanClass) {
        Constructor<?>[] constructors = beanClass.getConstructors();
        // order by parameter count, we're going to use the most greedy constructor
        Arrays.sort(constructors, CONSTRUCTOR_COMPARATOR);
        Injector[] injectors = null;
        nextCtor:
        for ( Constructor<?> ctor : constructors ) {
            if ( (ctor.getModifiers() & Modifier.PUBLIC) == 0 ) {
                //noinspection UnnecessaryLabelOnContinueStatement
                continue nextCtor;
            }
            if ( (ctor.getAnnotation(NoInjection.class)) != null ) {
                log.trace("Skipping constructor {} because @NoInspection", ctor);
                continue nextCtor;
            }
            log.trace("Looking at constructor: {}", ctor);
            Class<?>[] types = ctor.getParameterTypes();
            Annotation[][] annotations = null;
            if ( injectors == null ) {
                // because of ordering by greediness, we can reuse this array
                injectors = new Injector[types.length];
            }
            for ( int i = 0; i < types.length; i++ ) {
                Class<?> type = types[i];
                injectors[i] = findInjector(type, Place.CONSTRUCTOR);
                if ( injectors[i] == null ) {
                    if ( annotations == null ) {
                        annotations = ctor.getParameterAnnotations();
                    }
                    for ( Annotation annotation : annotations[i] ) {
                        if ( annotation.annotationType() == Optional.class ) {
                            if ( log.isTraceEnabled() ) {
                                log.trace("Using default value for argument {}", i);
                            }
                            injectors[i] = DefaultValueInjector.instance();
                            break;
                        }
                    }
                }
                if ( injectors[i] == null ) {
                    if ( log.isTraceEnabled() ) {
                        log.trace("No injector for argument " + i);
                    }
                    continue nextCtor;
                }
            }
            // we've got our constructor
            Object[] arguments = new Object[types.length];
            for ( int i = 0; i < arguments.length; i++ ) {
                arguments[i] = injectors[i].value(types[i], Place.CONSTRUCTOR);
            }
            if ( log.isDebugEnabled() ) {
                log.debug("Invoking constructor " + ctor + " with arguments " + Arrays.asList(arguments));
            }
            try {
                T instance = newInstance(ctor, arguments);
                if ( injectProperties ) {
                    injectProperties(instance);
                }
                return instance;
            }
            catch ( Exception e ) {
                throw new BeanException("Error invoking constructor " + ctor, e);
            }
        }
        throw new NoInjectorException("No suitable constructor in class " + beanClass);
    }

    @SuppressWarnings({ "unchecked" })
    private <T> T newInstance(Constructor<?> ctor, Object[] arguments) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        return (T)ctor.newInstance(arguments);
    }

    public void injectProperties(Object bean) {
        try {
            for ( PropertyDescriptor property : Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors() ) {
                if ( property instanceof IndexedPropertyDescriptor ) {
                    continue;
                }
                Method setter = property.getWriteMethod();
                if ( setter == null ) {
                    continue;
                }
                if ( setter.getAnnotation(NoInjection.class) != null ) {
                    continue;
                }
                Injector injector = findInjector(property.getPropertyType(), Place.PROPERTY);
                if ( injector == null ) {
                    if ( setter.getAnnotation(Required.class) != null ) {
                        Annotation[][] annotations = setter.getParameterAnnotations();
                        for ( int i = 0; i < annotations[0].length; i++ ) {
                            if ( annotations[0][i].annotationType() == Optional.class ) {
                                injector = DefaultValueInjector.instance();
                                break;
                            }
                        }
                        if ( injector == null ) {
                            continue;
                        }
                    }
                }
                try {
                    setter.invoke(bean, injector.value(property.getPropertyType(), Place.PROPERTY));
                }
                catch ( BeanException e ) {
                    throw e;
                }
                catch ( Exception e ) {
                    throw new BeanException("Error invoking setter for property " + property.getName() + " on " + bean, e);
                }
            }
        }
        catch ( IntrospectionException e ) {
            throw new BeanException("Error introspecting " + bean.getClass());
        }
    }

    private Injector findInjector(Class<?> type, Place place) {
        for ( Injector injector : injectors ) {
            if ( injector.accept(type, place) ) {
                return injector;
            }
        }
        return null;
    }

}
