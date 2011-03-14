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

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.BeanUtils;
import ch.raffael.util.beans.PropertyChangeForwarder;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BeanPropertyBinding<T, B> extends AbstractChainedBinding<T, B> {

    private String propertyName;
    private PropertyChangeForwarder forwarder = new PropertyChangeForwarder(this, observableSupport);

    public BeanPropertyBinding() {
    }

    public BeanPropertyBinding(Binding<B> source) {
        setSource(source);
    }

    public BeanPropertyBinding(String propertyName) {
        setPropertyName(propertyName);
    }

    public BeanPropertyBinding(String propertyName, Binding<B> source) {
        setPropertyName(propertyName);
        setSource(source);
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        Object oldValue = getValue();
        forwarder.remove(this.propertyName);
        this.propertyName = propertyName;
        if ( propertyName != null ) {
            forwarder.map(propertyName, PROPERTY_VALUE);
        }
        observableSupport.firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
    }

    @Override
    protected void detachSource(@NotNull B source) {
        super.detachSource(source);
        if ( BeanUtils.getEventSetDescriptor(source, "propertyChange") != null) {
            BeanUtils.removePropertyChangeListener(source, forwarder);
        }
    }

    @Override
    protected void attachSource(@NotNull B source) {
        super.attachSource(source);
        if ( BeanUtils.getEventSetDescriptor(source, "propertyChange") != null) {
            BeanUtils.addPropertyChangeListener(source, forwarder);
        }
    }

    @SuppressWarnings( { "unchecked" })
    @Override
    protected T getValue(@NotNull B source) {
        if ( propertyName == null ) {
            return null;
        }
        return (T)BeanUtils.getProperty(source, propertyName);
    }

    @Override
    protected void setValue(@NotNull B src, T value) {
        if ( propertyName == null ) {
            return;
        }
        BeanUtils.setProperty(src, propertyName, value);
    }
}
