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

import org.testng.annotations.*;

import static org.easymock.EasyMock.*;
import static org.testng.Assert.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestSimpleBinding {

    @Test
    public void testGetSet() throws Exception {
        SimpleBinding<String> binding = new SimpleBinding<String>();
        assertNull(binding.getValue(), "1: null");
        binding.setValue("Testing");
        assertEquals(binding.getValue(), "Testing", "2: Testing");
    }

    @Test
    public void testPropertyChange() throws Exception {
        SimpleBinding<String> binding = new SimpleBinding<String>();
        PropertyChangeListener listener = createStrictMock(PropertyChangeListener.class);
        binding.addPropertyChangeListener(listener);
        ExpectPropertyChange.expect(listener, binding, "value", null, "String A");
        ExpectPropertyChange.expect(listener, binding, "value", "String A", "String B");
        replay(listener);
        binding.setValue("String A");
        binding.setValue("String B");
        verify(listener);
    }

    @Test
    public void testNoPropertyChange() throws Exception {
        SimpleBinding<String> binding = new SimpleBinding<String>("MyValue");
        PropertyChangeListener listener = createStrictMock(PropertyChangeListener.class);
        binding.addPropertyChangeListener(listener);
        replay(listener);
        binding.setValue("MyValue");
        verify(listener);
    }

    @Test
    public void testNoPropertyChangeNull() throws Exception {
        SimpleBinding<String> binding = new SimpleBinding<String>();
        PropertyChangeListener listener = createStrictMock(PropertyChangeListener.class);
        binding.addPropertyChangeListener(listener);
        replay(listener);
        binding.setValue(null);
        verify(listener);
    }

}
