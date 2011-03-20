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
public abstract class AbstractChainedBinding<T, S> implements ChainedBinding<T,S> {

    protected final ObservableSupport observableSupport = new ObservableSupport(this);
    private final PropertyChangeListener sourceObserver = new PropertyChangeListener() {
        @SuppressWarnings( { "unchecked" })
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( evt.getPropertyName().equals(PROPERTY_VALUE) ) {
                T oldValue = null;
                if ( evt.getOldValue() != null ) {
                    oldValue = getValue((S)evt.getOldValue());
                    detachSource((S)evt.getOldValue());
                }
                T newValue = null;
                if ( evt.getNewValue() != null ) {
                    newValue = getValue((S)evt.getNewValue());
                    attachSource((S)evt.getNewValue());
                }
                observableSupport.firePropertyChange(PROPERTY_VALUE, oldValue, newValue);
            }
        }
    };
    private Binding<S> source;

    protected AbstractChainedBinding() {
    }

    // better to forbid this: note that the sub-class may not be initialized at this
    // point which may lead to hard-to-find bugs
    //protected AbstractChainedBinding(Binding<S> source) {
    //    setSource(source);
    //}

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

    @Override
    public Binding<S> getSource() {
        return source;
    }

    @Override
    public void setSource(Binding<S> source) {
        if ( !Bindings.equal(this.source, source) ) {
            if ( this.source != null ) {
                this.source.removePropertyChangeListener(sourceObserver);
            }
            T oldValue = getValue();
            detachSource();
            Binding<S> oldSource = this.source;
            this.source = source;
            attachSource();
            if ( this.source != null ) {
                this.source.addPropertyChangeListener(sourceObserver);
            }
            observableSupport.firePropertyChange(PROPERTY_SOURCE, oldSource, source);
            observableSupport.firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
        }
    }

    private void detachSource() {
        if ( source != null ) {
            S src = source.getValue();
            if ( src != null ) {
                detachSource(src);
            }
        }
    }

    private void attachSource() {
        if ( source != null ) {
            S src = source.getValue();
            if ( src != null ) {
                attachSource(src);
            }
        }
    }

    protected void detachSource(@NotNull S source) {
    }

    protected void attachSource(@NotNull S source) {
    }

    @Override
    public T getValue() {
        if ( source == null ) {
            return null;
        }
        S src = source.getValue();
        if ( src == null ) {
            return null;
        }
        else {
            return getValue(src);
        }
    }

    protected abstract T getValue(@NotNull S source);

    @Override
    public void setValue(T value) {
        if ( source == null ) {
            return;
        }
        S src = source.getValue();
        if ( src == null ) {
            return;
        }
        setValue(src, value);
    }

    protected abstract void setValue(@NotNull S src, T value);
}
