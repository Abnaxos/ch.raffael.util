package ch.raffael.util.beans.inject;

import ch.raffael.util.beans.BeanException;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class FilterByPlace implements Injector {

    private final Injector delegate;
    private final Place place;

    public FilterByPlace(Injector delegate, Place place) {
        this.delegate = delegate;
        this.place = place;
    }

    @Override
    public boolean accept(Class<?> type, Place place) {
        if ( place == this.place ) {
            return delegate.accept(type, place);
        }
        else {
            return false;
        }
    }

    @Override
    public Object value(Class<?> type, Place place) {
        if ( place == this.place ) {
            return delegate.accept(type, place);
        }
        else {
            throw new BeanException("Inacceptable place for injection: " + place);
        }
    }
}
