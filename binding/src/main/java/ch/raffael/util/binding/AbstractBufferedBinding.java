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

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.ObservableSupport;
import ch.raffael.util.binding.util.ValidatorHolder;
import ch.raffael.util.binding.validate.ValidationResult;
import ch.raffael.util.binding.validate.Validator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractBufferedBinding<T> extends AbstractBinding<T> implements Buffer, ValidatingBinding<T> {

    protected final ObservableSupport observableSupport = new ObservableSupport(this);
    private final ValidatorHolder<T> validatorHolder = new ValidatorHolder<T>();

    private T value = null;
    private T original = null;
    private boolean buffering = false;

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

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
            validatorHolder.checkValue(value);
            Object oldValue = this.value;
            this.value = value;
            observableSupport.firePropertyChange(PROPERTY_VALUE, oldValue, value);
            updateBuffering();
        }
    }

    protected void updateBuffering() {
        boolean oldBuffering = buffering;
        buffering = !Bindings.equal(this.value, original, true);
        observableSupport.firePropertyChange(PROPERTY_BUFFERING, oldBuffering, buffering);
    }

    protected T getOriginal() {
        return original;
    }

    protected boolean update(T original) {
        if ( !Bindings.equal(this.original, original, true) ) {
            this.original = original;
            updateBuffering();
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean isBuffering() {
        return buffering;
    }

    protected void reset(T value) {
        this.original = value;
        if ( this.value != null && this.value.equals(value) ) {
            // force a property change event by first setting the value to null
            // see PropertyChangeSupport: an event will ALWAYS be fired if the old value
            // is null
            this.value = null;
        }
        setValue(value);
        // propertyChange for buffering has been checked in setValue()
        //if ( buffering ) {
        //    buffering = false;
        //    observableSupport.firePropertyChange(PROPERTY_BUFFERING, true, false);
        //}
    }

    @Override
    public void validate(T value, ValidationResult result) {
        validatorHolder.validate(value, result);
    }

    public Validator<T> getValidator() {
        return validatorHolder.getValidator();
    }

    public void setValidator(Validator<T> validator) {
        validatorHolder.setValidator(validator);
    }

    @NotNull
    public AbstractBufferedBinding<T> validator(@NotNull Validator<T> validator) {
        validatorHolder.append(validator);
        return this;
    }

}
