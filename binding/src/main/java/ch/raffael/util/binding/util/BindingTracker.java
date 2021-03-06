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

package ch.raffael.util.binding.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.raffael.util.binding.Binding;
import ch.raffael.util.binding.Bindings;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class BindingTracker<T> implements PropertyChangeListener {

    private Binding<T> binding;
    private boolean updating = false;

    public BindingTracker() {
    }

    public BindingTracker(Binding<T> binding) {
        setBinding(binding);
    }

    public Binding<T> getBinding() {
        return binding;
    }

    public void setBinding(Binding<T> binding) {
        if ( this.binding != null ) {
            this.binding.removePropertyChangeListener(this);
        }
        this.binding = binding;
        update();
        if ( this.binding != null ) {
            this.binding.addPropertyChangeListener(this);
        }
    }

    public void set(T value) {
        if ( binding == null ) {
            return;
        }
        try {
            updating = true;
            binding.setValue(value);
        }
        finally {
            updating = false;
        }
    }

    public void update() {
        update(Bindings.getValue(binding));
    }
    
    public abstract void update(T newValue);

    @SuppressWarnings( { "unchecked" })
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() == binding && evt.getPropertyName().equals(Binding.PROPERTY_VALUE) ) {
            if ( !updating ) {
                update(binding.getValue());
            }
        }
    }
}
