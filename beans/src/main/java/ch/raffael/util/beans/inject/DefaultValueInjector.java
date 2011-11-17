package ch.raffael.util.beans.inject;

import com.google.common.base.Defaults;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultValueInjector implements Injector {

    private static final DefaultValueInjector INSTANCE = new DefaultValueInjector();

    public static DefaultValueInjector instance() {
        return INSTANCE;
    }

    @Override
    public boolean accept(Class<?> type, Place place) {
        return true;
    }

    @Override
    public Object value(Class<?> type, Place place) {
        return Defaults.defaultValue(type);
    }
}
