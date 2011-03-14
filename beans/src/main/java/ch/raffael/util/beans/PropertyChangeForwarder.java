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

package ch.raffael.util.beans;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PropertyChangeForwarder implements PropertyChangeListener {

    private final Object source;
    private final ObservableSupport emitter;
    private final HashMap<String, String> propertyMappings = new HashMap<String, String>();

    public PropertyChangeForwarder(Object source, ObservableSupport emitter) {
        this.source = source;
        this.emitter = emitter;
    }

    public PropertyChangeForwarder(Object source, ObservableSupport emitter, String... propertyNames) {
        this.source = source;
        this.emitter = emitter;
        if ( propertyNames != null && propertyNames.length > 0 ) {
            for ( String p : propertyNames ) {
                add(p);
            }
        }
    }

    public PropertyChangeForwarder add(String name) {
        propertyMappings.put(name, name);
        return this;
    }

    public PropertyChangeForwarder map(String sourceName, String newName) {
        propertyMappings.put(sourceName, newName);
        return this;
    }

    public void remove(String name) {
        propertyMappings.remove(name);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String newName = propertyMappings.get(evt.getPropertyName());
        if ( newName != null ) {
            emitter.firePropertyChange(newName, evt.getOldValue(), evt.getNewValue());
        }
    }
}
