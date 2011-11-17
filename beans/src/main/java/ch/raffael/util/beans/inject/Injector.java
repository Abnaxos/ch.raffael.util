package ch.raffael.util.beans.inject;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Injector {

    boolean accept(Class<?> type, Place place);

    Object value(Class<?> type, Place place);

    enum Place {
        CONSTRUCTOR,
        PROPERTY
    }

}
