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

package ch.raffael.util.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.raffael.util.beans.ObservableSupport;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class AbstractGuard<T> extends AbstractBinding<T> {

    private final ObservableSupport observableSupport = new ObservableSupport(this);
    private final PropertyChangeListener guardedListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( evt.getPropertyName().equals(PROPERTY_VALUE) ) {
                observableSupport.firePropertyChange(PROPERTY_VALUE, evt.getOldValue(), evt.getNewValue());
            }
        }
    };
    private Binding<T> guarded;

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

    public Binding<T> getGuarded() {
        return guarded;
    }

    public void setGuarded(Binding<T> guarded) {
        if ( !Bindings.equal(this.guarded, guarded) ) {
            T oldValue = null;
            if ( this.guarded != null ) {
                this.guarded.removePropertyChangeListener(guardedListener);
                oldValue = this.guarded.getValue();
            }
            this.guarded = guarded;
            T newValue = null;
            if ( guarded != null ) {
                newValue = guarded.getValue();
                guarded.addPropertyChangeListener(guardedListener);
            }
            observableSupport.firePropertyChange(PROPERTY_VALUE, oldValue, newValue);
        }
    }

    @Override
    public T getValue() {
        return Bindings.getValue(guarded);
    }

    @Override
    public void setValue(T value) {
        Bindings.setValue(guarded, value);
    }
}
