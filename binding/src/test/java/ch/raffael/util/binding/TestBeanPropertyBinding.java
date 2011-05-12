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
import java.beans.PropertyChangeSupport;

import org.testng.annotations.*;
import org.easymock.EasyMock;

import static org.testng.Assert.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestBeanPropertyBinding {

    @Test
    public void testBeanPropertyBinding() {
        MyBean bean = new MyBean();
        bean.setTheProperty(42);
        BeanPropertyBinding binding = new BeanPropertyBinding("theProperty", new SimpleBinding<Object>(bean));
        PropertyChangeListener listener = EasyMock.createStrictMock(PropertyChangeListener.class);
        binding.addPropertyChangeListener(listener);
        ExpectPropertyChange.expect(listener, binding, "value", 42, 23);
        EasyMock.replay(listener);
        assertEquals(binding.getValue(), 42, "old value");
        bean.setTheProperty(23);
        assertEquals(binding.getValue(), 23, "new value");
        EasyMock.verify(listener);
    }

    @Test
    public void testChangeBean() {
        MyBean bean = new MyBean();
        bean.setTheProperty(42);
        MyBean anotherBean = new MyBean();
        anotherBean.setTheProperty(23);
        SimpleBinding<Object> beanBinding = new SimpleBinding<Object>(bean);
        BeanPropertyBinding binding = new BeanPropertyBinding("theProperty", beanBinding);
        PropertyChangeListener listener = EasyMock.createStrictMock(PropertyChangeListener.class);
        binding.addPropertyChangeListener(listener);
        ExpectPropertyChange.expect(listener, binding, "value", 42, 23);
        EasyMock.replay(listener);
        assertEquals(binding.getValue(), 42, "old value");
        beanBinding.setValue(anotherBean);
        assertEquals(binding.getValue(), 23, "new value");
        EasyMock.verify(listener);
    }

    @Test
    public void testChangePropertyName() throws Exception {
        MyBean bean = new MyBean();
        bean.setTheProperty(42);
        bean.setAnotherProperty("Hello");
        BeanPropertyBinding binding = new BeanPropertyBinding("theProperty", new SimpleBinding<Object>(bean));
        PropertyChangeListener listener = EasyMock.createStrictMock(PropertyChangeListener.class);
        binding.addPropertyChangeListener(listener);
        ExpectPropertyChange.expect(listener, binding, "value", 42, "Hello");
        EasyMock.replay(listener);
        assertEquals(binding.getValue(), 42, "old value");
        binding.setPropertyName("anotherProperty");
        assertEquals(binding.getValue(), "Hello", "new value");
        EasyMock.verify(listener);
    }

    public static class MyBean {

        private final PropertyChangeSupport propertyChange = new PropertyChangeSupport(this);
        private int theProperty;
        private String anotherProperty;

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propertyChange.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propertyChange.removePropertyChangeListener(listener);
        }

        public int getTheProperty() {
            return theProperty;
        }

        public void setTheProperty(int theProperty) {
            int oldValue = this.theProperty;
            this.theProperty = theProperty;
            propertyChange.firePropertyChange("theProperty", oldValue, theProperty);
        }

        public String getAnotherProperty() {
            return anotherProperty;
        }

        public void setAnotherProperty(String anotherProperty) {
            String oldValue = this.anotherProperty;
            this.anotherProperty = anotherProperty;
            propertyChange.firePropertyChange("anotherProperty", oldValue, anotherProperty);
        }
    }

}
