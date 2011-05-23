package ch.raffael.util.beans;

import java.beans.PropertyChangeSupport;
import java.beans.VetoableChangeSupport;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ListProperty<T> extends Property<List<T>> {

    public ListProperty(String name) {
        super(name);
    }

    public ListProperty(String name, List<T> value) {
        super(name, value);
    }

    @Override
    protected List<T> processIncoming(List<T> value) {
        if ( value == null ) {
            return Collections.emptyList();
        }
        else {
            return ImmutableList.copyOf(value);
        }
    }

    @Override
    public ListProperty<T> notNull() {
        super.notNull();
        if ( doGet() == null ) {
            doSet(Collections.<T>emptyList());
        }
        return this;
    }

    @Override
    public ListProperty<T> bound(PropertyChangeSupport changeSupport) {
        super.bound(changeSupport);
        return this;
    }

    @Override
    public ListProperty<T> synchronize(Object synchronize) {
        super.synchronize(synchronize);
        return this;
    }

    @Override
    public ListProperty<T> vetoable(VetoableChangeSupport vetoSupport) {
        super.vetoable(vetoSupport);
        return this;
    }

    @Override
    public ListProperty<T> nullable() {
        super.nullable();
        return this;
    }

    @Override
    public ListProperty<T> value(List<T> value) {
        super.value(value);
        return this;
    }
}
