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
import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

import ch.raffael.util.beans.ObservableSupport;
import ch.raffael.util.common.collections.ReadOnlyIterator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BufferGroup implements Buffer, Iterable<Buffer> {

    private final ObservableSupport observableSupport = new ObservableSupport(this);
    private Set<Buffer> members = Sets.newIdentityHashSet();
    private Set<Buffer> buffering = Sets.newIdentityHashSet();
    private final PropertyChangeListener memberListener = new PropertyChangeListener() {
        @SuppressWarnings( { "SuspiciousMethodCalls" })
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( members.contains(evt.getSource()) && evt.getPropertyName().equals(PROPERTY_BUFFERING) ) {
                updateBuffering((Buffer)evt.getSource());
            }
        }
    };
    private boolean skipNonBuffering = false;

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

    public void add(Buffer buffer) {
        members.add(buffer);
        updateBuffering(buffer);
        buffer.addPropertyChangeListener(memberListener);
    }

    public void remove(Buffer buffer) {
        buffer.removePropertyChangeListener(memberListener);
        members.remove(buffer);
        boolean oldBuffering = isBuffering();
        buffering.remove(buffer);
        observableSupport.firePropertyChange(PROPERTY_BUFFERING, oldBuffering, isBuffering());
    }

    public void removeAll() {
        for ( Buffer buf : members ) {
            buf.removePropertyChangeListener(memberListener);
        }
        boolean oldBuffering = isBuffering();
        members.clear();
        buffering.clear();
        observableSupport.firePropertyChange(PROPERTY_BUFFERING, oldBuffering, isBuffering());
    }

    public boolean contains(Buffer buffer) {
        return members.contains(buffer);
    }

    @Override
    public Iterator<Buffer> iterator() {
        return new ReadOnlyIterator<Buffer>(members.iterator());
    }

    public boolean isSkipNonBuffering() {
        return skipNonBuffering;
    }

    public void setSkipNonBuffering(boolean skipNonBuffering) {
        this.skipNonBuffering = skipNonBuffering;
    }

    @Override
    public void flush() {
        for ( Buffer buf : members ) {
            if ( !skipNonBuffering || buf.isBuffering() ) {
                buf.flush();
            }
        }
    }

    @Override
    public void commit() {
        for ( Buffer buf : members ) {
            if ( !skipNonBuffering || buf.isBuffering() ) {
                buf.commit();
            }
        }
    }

    @Override
    public boolean isBuffering() {
        return !buffering.isEmpty();
    }

    private void updateBuffering(Buffer buffer) {
        assert members.contains(buffer);
        boolean oldBuffering = isBuffering();
        if ( buffer.isBuffering() ) {
            buffering.add(buffer);
        }
        else {
            buffering.remove(buffer);
        }
        observableSupport.firePropertyChange(PROPERTY_BUFFERING, oldBuffering, isBuffering());
    }

}
