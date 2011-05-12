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

import java.beans.PropertyChangeListener;

import ch.raffael.util.beans.ObservableSupport;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class SimpleBinding<T> extends AbstractBinding<T> {

    protected final ObservableSupport observableSupport = new ObservableSupport(this);
    private T value;

    public SimpleBinding() {
    }

    public SimpleBinding(T value) {
        this.value = value;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        if ( !Bindings.equal(this.value, value, true) ) {
            T oldValue = this.value;
            this.value = value;
            valueChanged(oldValue, value);
            observableSupport.firePropertyChange(PROPERTY_VALUE, oldValue, value);
        }
    }

    protected void valueChanged(T oldValue, T newValue) {
    }

    public <P> BeanPropertyBinding<P, T> property(String name) {
        return add(new BeanPropertyBinding<P, T>(name, this));
    }

}
