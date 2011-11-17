package ch.raffael.util.beans.inject;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class LoggerInjector implements Injector {

    private static final LoggerInjector INSTANCE = new LoggerInjector();

    public static LoggerInjector instance() {
        return INSTANCE;
    }

    @Override
    public boolean accept(Class<?> type, Place place) {
        return type == java.util.logging.Logger.class
                || type == org.slf4j.Logger.class;
    }

    @Override
    public Object value(Class<?> type, Place place) {
        if ( type == java.util.logging.Logger.class ) {
            return java.util.logging.Logger.getLogger(type.getName());
        }
        else {
            return org.slf4j.LoggerFactory.getLogger(type);
        }
    }
}
