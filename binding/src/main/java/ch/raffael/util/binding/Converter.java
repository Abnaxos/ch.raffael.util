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

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.ObservableSupport;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class Converter<T, S> extends SimpleBinding<T> {

    private final ObservableSupport observableSupport = new ObservableSupport(this);
    private Binding<S> source;

    private final PropertyChangeListener sourceObserver = new PropertyChangeListener() {
        @SuppressWarnings( { "unchecked" })
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( evt.getSource() == source && evt.getPropertyName().equals(PROPERTY_VALUE) ) {
                setValue(fromSource((S)evt.getNewValue()));
            }
        }
    };

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

    public Binding<S> getSource() {
        return source;
    }

    public void setSource(Binding<S> source) {
        if ( this.source != null ) {
            this.source.removePropertyChangeListener(sourceObserver);
        }
        this.source = source;
        if ( source != null ) {
            setValue(fromSource(source.getValue()));
            this.source.addPropertyChangeListener(sourceObserver);
        }
        else {
            setValue(null);
        }
    }

    @Override
    protected void valueChanged(T oldValue, T newValue) {
        if ( source != null ) {
            source.setValue(toSource(newValue));
        }
    }

    protected abstract S toSource(T value);

    protected abstract T fromSource(S value);

}
