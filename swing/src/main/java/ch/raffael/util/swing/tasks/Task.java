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

package ch.raffael.util.swing.tasks;

import javax.swing.SwingWorker;

import ch.raffael.util.beans.Property;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class Task extends SwingWorker {

    private final Type type;
    private final Property<String> name = new Property<String>("name").bound(getPropertyChangeSupport()).synchronize(this);
    private final Property<String> description = new Property<String>("description").bound(getPropertyChangeSupport()).synchronize(this);
    private final Property<String> phase = new Property<String>("phase").bound(getPropertyChangeSupport()).synchronize(this);

    protected Task(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }
    
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
    
    public String getPhase() {
        return phase.get();
    }

    public void setPhase(String phase) {
        this.phase.set(phase);
    }
    
    public static enum Type {
        BACKGROUND(false), SHORT_BLOCKING(true), LONG_BLOCKING(true);
        private final boolean blocking;
        Type(boolean blocking) {
            this.blocking = blocking;
        }
        public boolean isBlocking() {
            return blocking;
        }
    }

}
