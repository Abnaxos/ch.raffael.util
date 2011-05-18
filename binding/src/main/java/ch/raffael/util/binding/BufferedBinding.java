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

import ch.raffael.util.binding.validate.ValidationResult;
import ch.raffael.util.binding.validate.Validator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BufferedBinding<T> extends AbstractBufferedBinding<T> implements ChainedBinding<T, T> {

    private Binding<T> source;
    private final PropertyChangeListener listener = new PropertyChangeListener() {
        @SuppressWarnings( { "unchecked" })
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( evt.getPropertyName().equals(PROPERTY_VALUE) ) {
                update((T)evt.getNewValue());
            }
        }
    };

    public BufferedBinding() {
    }

    public BufferedBinding(Binding<T> source) {
        setSource(source);
    }

    public Binding<T> getSource() {
        return source;
    }

    public void setSource(Binding<T> source) {
        if ( !Bindings.equal(this.source, source) ) {
            Binding<T> oldValue = this.source;
            if ( oldValue != null ) {
                oldValue.removePropertyChangeListener(listener);
            }
            this.source = source;
            if ( source != null ) {
                source.addPropertyChangeListener(listener);
            }
            observableSupport.firePropertyChange(PROPERTY_SOURCE, oldValue, source);
            // FIXME: update or reset?
            reset(Bindings.getValue(source));
        }
    }

    @Override
    public void validate(T value, ValidationResult result) {
        super.validate(value, result);
        if ( getSource() instanceof ValidatingBinding ) {
            ((ValidatingBinding<T>)getSource()).validate(value, result);
        }
    }

    @Override
    public void flush() {
        reset(Bindings.getValue(source));
    }

    @Override
    public void commit() {
        if ( source == null ) {
            reset(null);
        }
        else {
            source.setValue(getValue());
            reset(source.getValue());
        }
    }

    @NotNull
    @Override
    public BufferedBinding<T> validator(@NotNull Validator<T> tValidator) {
        super.validator(tValidator);
        return this;
    }
}
