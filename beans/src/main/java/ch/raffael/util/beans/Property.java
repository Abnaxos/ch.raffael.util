/*
 * Copyright 2010 Raffael Herzog
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

package ch.raffael.util.beans;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeSupport;
import java.io.Serializable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings({ "SynchronizeOnNonFinalField" })
public class Property<T> implements Serializable {

    private final String name;
    private T value;
    private boolean nullable = true;
    private Object synchronize;
    private PropertyChangeSupport changeSupport;
    private VetoableChangeSupport vetoSupport;

    public Property(String name) {
        this(name, null);
    }

    public Property(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public static <T> Property<T> create(String name) {
        return new Property(name);
    }

    public static <T> Property<T> create(String name, T value) {
        return new Property(name, value);
    }

    public String getName() {
        return name;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Object getSynchronize() {
        return synchronize;
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public VetoableChangeSupport getVetoSupport() {
        return vetoSupport;
    }

    public Property<T> notNull() {
        nullable = true;
        return this;
    }

    public Property<T> nullable() {
        nullable = false;
        return this;
    }

    public Property<T> synchronize(Object synchronize) {
        this.synchronize = synchronize;
        return this;
    }

    public Property<T> bound(PropertyChangeSupport changeSupport) {
        this.changeSupport = changeSupport;
        return this;
    }

    public Property<T> vetoable(VetoableChangeSupport vetoSupport) {
        this.vetoSupport = vetoSupport;
        return this;
    }

    public Property<T> value(T value) {
        this.value = value;
        return this;
    }

    //public static Property<T, RuntimeException>

    public T get() {
        if ( synchronize != null ) {
            synchronized ( synchronize ) {
                return doGet();
            }
        }
        else {
            return doGet();
        }
    }
    
    protected T doGet() {
        return value;
    }

    public void set(T value) {
        if ( synchronize != null ) {
            synchronized ( synchronize ) {
                doSet(value);
            }
        }
        else {
            doSet(value);
        }
    }

    public void vetoableSet(T value) throws PropertyVetoException {
        if ( synchronize != null ) {
            synchronized ( synchronize ) {
                doVetoableSet(value);
            }
        }
        else {
            doVetoableSet(value);
        }
    }

    protected void doSet(T value) {
        if ( isChange(value) ) {
            T oldValue = this.value;
            this.value = value;
            didChange(oldValue, value);
            if ( changeSupport != null ) {
                changeSupport.firePropertyChange(name, oldValue, value);
            }
        }
    }

    protected void doVetoableSet(T value) throws PropertyVetoException {
        if ( vetoSupport == null ) {
            throw new IllegalStateException("No vetoable change support set");
        }
        if ( isChange(value) ) {
            vetoSupport.fireVetoableChange(name, this.value, value);
            T oldValue = this.value;
            this.value = value;
            didChange(oldValue, value);
            if ( changeSupport != null ) {
                changeSupport.firePropertyChange(name, oldValue, value);
            }
        }
    }

    protected void didChange(T oldValue, T newValue) {
    }

    public boolean isChange(T value) {
        if ( !nullable && value == null ) {
            throw new IllegalArgumentException("Property " + name + " is not nullable");
        }
        if ( value == this.value ) {
            return false;
        }
        else {
            return value == null || !value.equals(this.value);
        }
    }

}
