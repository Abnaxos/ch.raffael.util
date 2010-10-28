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

package ch.raffael.util.swing.context;

import ch.raffael.util.beans.Event;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings({ "NonSerializableFieldInSerializableClass" })
public class ContextEvent extends Event<Context> {
    private static final long serialVersionUID = 1487053147370729764L;

    private final Class<?> type;
    private final Object key;
    private final Object oldValue;
    private final Object newValue;

    public ContextEvent(Context source, Class<?> type, Object key, Object oldValue, Object newValue) {
        super(source);
        this.type = type;
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String toString() {
        return "ContextEvent{" +
                "type=" + type +
                ", key=" + key +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }

    public Class<?> getType() {
        return type;
    }

    public Object getKey() {
        return key;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
