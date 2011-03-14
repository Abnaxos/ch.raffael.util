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

package ch.raffael.util.binding.validate;

import com.google.common.base.Objects;
import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.Event;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ValidationMessageEvent extends Event<Object> {

    private final Object subject;
    private final Message message;

    public ValidationMessageEvent(@NotNull Object source, Object subject, @NotNull Message message) {
        super(source);
        this.subject = subject;
        this.message = message;
    }

    @Override
    @NotNull 
    public String toString() {
        return Objects.toStringHelper(this)
                .add("source", getSource())
                .add("subject", subject)
                .add("message", message)
                .toString();
    }

    public Object getSubject() {
        return subject;
    }

    @NotNull
    public Message getMessage() {
        return message;
    }
}
