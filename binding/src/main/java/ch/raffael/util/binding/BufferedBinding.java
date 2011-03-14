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

import com.google.common.base.Objects;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BufferedBinding<T> extends AbstractBufferedBinding<T> {

    private Binding<T> binding;
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

    public BufferedBinding(Binding<T> binding) {
        setBinding(binding);
    }

    public Binding getBinding() {
        return binding;
    }

    public void setBinding(Binding<T> binding) {
        if ( this.binding != null ) {
            this.binding.removePropertyChangeListener(listener);
        }
        this.binding = binding;
        if ( this.binding != null ) {
            this.binding.addPropertyChangeListener(listener);
        }
        // FIXME: update or reset?
        reset(BindingUtils.getValue(binding));
        //setValue(BindingUtils.getValue(binding));
    }

    @Override
    public void flush() {
        reset(BindingUtils.getValue(binding));
    }

    @Override
    public void commit() {
        if ( binding == null ) {
            reset(null);
        }
        else {
            binding.setValue(getValue());
            reset(binding.getValue());
        }
    }
}
