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

import org.testng.annotations.*;
import org.easymock.EasyMock;

import static org.testng.Assert.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TestBufferedBinding {

    private SimpleBinding<String> value;
    private BufferedBinding<String> buffer;

    @BeforeMethod
    public void setupBindings() {
        value = new SimpleBinding<String>();
        buffer = new BufferedBinding<String>(value);
    }

    @Test
    public void testFlushBottomUp() throws Exception {
        PropertyChangeListener bufListener = EasyMock.createStrictMock(PropertyChangeListener.class);
        buffer.addPropertyChangeListener(bufListener);
        ExpectPropertyChange.expect(bufListener, buffer, "buffering", false, true);
        ExpectPropertyChange.expect(bufListener, buffer, "value", null, "Test");
        ExpectPropertyChange.expect(bufListener, buffer, "buffering", true, false);
        EasyMock.replay(bufListener);
        assertFalse(buffer.isBuffering(), "not buffering");
        value.setValue("Test");
        assertNull(buffer.getValue(), "still null");
        assertTrue(buffer.isBuffering(), "buffering after change");
        buffer.flush();
        assertEquals(buffer.getValue(), "Test");
        assertFalse(buffer.isBuffering(), "not buffering after flush");
        EasyMock.verify(bufListener);
    }

    @Test
    public void testFlushTopDown() throws Exception {
        PropertyChangeListener bufListener = EasyMock.createStrictMock(PropertyChangeListener.class);
        buffer.addPropertyChangeListener(bufListener);
        ExpectPropertyChange.expect(bufListener, buffer, "value", null, "Test");
        ExpectPropertyChange.expect(bufListener, buffer, "buffering", false, true);
        ExpectPropertyChange.expect(bufListener, buffer, "value", "Test", null);
        ExpectPropertyChange.expect(bufListener, buffer, "buffering", true, false);
        EasyMock.replay(bufListener);
        assertFalse(buffer.isBuffering(), "not buffering");
        buffer.setValue("Test");
        assertNull(value.getValue(), "value still null");
        assertEquals(buffer.getValue(), "Test", "buffered value");
        assertTrue(buffer.isBuffering(), "buffering after change");
        buffer.flush();
        assertNull(buffer.getValue(), "null after flush");
        assertFalse(buffer.isBuffering(), "not buffering after flush");
        EasyMock.verify(bufListener);
    }

    @Test
    public void testCommit() throws Exception {
        PropertyChangeListener valListener = EasyMock.createStrictMock(PropertyChangeListener.class);
        value.addPropertyChangeListener(valListener);
        PropertyChangeListener bufListener = EasyMock.createStrictMock(PropertyChangeListener.class);
        buffer.addPropertyChangeListener(bufListener);
        ExpectPropertyChange.expect(bufListener, buffer, "value", null, "Test");
        ExpectPropertyChange.expect(bufListener, buffer, "buffering", false, true);
        ExpectPropertyChange.expect(valListener, value, "value", null, "Test");
        ExpectPropertyChange.expect(bufListener, buffer, "buffering", true, false);
        EasyMock.replay(valListener, bufListener);
        buffer.setValue("Test");
        assertNull(value.getValue(), "value: null");
        assertEquals(buffer.getValue(), "Test", "buffer: Test");
        assertTrue(buffer.isBuffering(), "Buffering after change");
        buffer.commit();
        assertEquals(value.getValue(), "Test", "value: Test");
        assertFalse(buffer.isBuffering(), "Not buffering after change");
        EasyMock.verify(valListener, bufListener);
    }

    @Test
    public void testChangeValueNoBuffer() {
        value.setValue("Old Binding");
        buffer.flush();
        PropertyChangeListener bufListener = EasyMock.createStrictMock(PropertyChangeListener.class);
        buffer.addPropertyChangeListener(bufListener);
        ExpectPropertyChange.expect(bufListener, buffer, "value", "Old Binding", "New Binding");
        EasyMock.replay(bufListener);
        SimpleBinding<String> newBinding = new SimpleBinding<String>("New Binding");
        assertEquals(buffer.getValue(), "Old Binding", "old binding's value");
        assertFalse(buffer.isBuffering(), "not buffering");
        buffer.setBinding(newBinding);
        assertEquals(buffer.getValue(), "New Binding", "new binding's value");
        assertFalse(buffer.isBuffering(), "not buffering");
        EasyMock.verify(bufListener);
    }

    @Test
    public void testChangeValueBuffered() {
        value.setValue("Old Binding");
        buffer.flush();
        PropertyChangeListener bufListener = EasyMock.createStrictMock(PropertyChangeListener.class);
        buffer.addPropertyChangeListener(bufListener);
        ExpectPropertyChange.expect(bufListener, buffer, "value", "Old Binding", "Buffered");
        ExpectPropertyChange.expect(bufListener, buffer, "buffering", false, true);
        ExpectPropertyChange.expect(bufListener, buffer, "value", "Buffered", "New Binding");
        ExpectPropertyChange.expect(bufListener, buffer, "buffering", true, false);
        EasyMock.replay(bufListener);
        SimpleBinding<String> newBinding = new SimpleBinding<String>("New Binding");
        assertEquals(buffer.getValue(), "Old Binding", "old binding's value");
        assertFalse(buffer.isBuffering(), "not buffering");
        buffer.setValue("Buffered");
        assertTrue(buffer.isBuffering(), "buffering after change");
        buffer.setBinding(newBinding);
        assertEquals(buffer.getValue(), "New Binding", "new binding's value");
        assertFalse(buffer.isBuffering(), "not buffering after change");
        EasyMock.verify(bufListener);
    }

}
