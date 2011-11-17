package ch.raffael.util.beans.inject;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.BeanException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class InstanceInjector implements Injector {

    private final Class<?> type;
    private final Object instance;

    @SuppressWarnings({ "unchecked" })
    public InstanceInjector(@NotNull Object instance) {
        this((Class<Object>)instance.getClass(), instance);
    }

    public <T> InstanceInjector(Class<T> type, T instance) {
        this.type = type;
        this.instance = instance;
    }

    @Override
    public boolean accept(Class<?> type, Place place) {
        return type == this.type;
    }

    @Override
    public Object value(Class<?> type, Place place) {
        if ( !accept(type, place) ) {
            throw new BeanException("Cannot inject type " + type);
        }
        return instance;
    }
}
