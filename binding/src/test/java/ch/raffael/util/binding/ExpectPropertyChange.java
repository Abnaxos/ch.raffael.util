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

import org.easymock.EasyMock;
import org.easymock.IExpectationSetters;

import static org.testng.Assert.*;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class ExpectPropertyChange implements PropertyChangeListener {

    private final Object source;
    private final String propertyName;
    private final Object oldValue;
    private final Object newValue;

    ExpectPropertyChange(Object source, String propertyName, Object oldValue, Object newValue) {
        this.source = source;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public static <T> IExpectationSetters<T> expect(PropertyChangeListener listener, Object source, String propertyName, Object oldValue, Object newValue) {
        listener.propertyChange(EasyMock.anyObject(PropertyChangeEvent.class));
        return EasyMock.<T>expectLastCall().andDelegateTo(new ExpectPropertyChange(source, propertyName, oldValue, newValue));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //System.out.println("Property Change: " + evt.getSource() + ": " + evt.getPropertyName() + ": " + evt.getOldValue() + " => " + evt.getNewValue());
        assertSame(evt.getSource(), source, "source");
        assertEquals(evt.getPropertyName(), propertyName, "propertyName");
        assertEquals(evt.getOldValue(), oldValue, "oldValue");
        assertEquals(evt.getNewValue(), newValue, "newValue");
    }
}
