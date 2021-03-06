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

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
class ValidationResultBuffer extends AbstractValidationResult {

    private Set<Message> messages = null;
    private Message.Severity maxSeverity;

    @Override
    public void add(Message message) {
        if ( messages == null ) {
            messages = Sets.newLinkedHashSet();
        }
        messages.add(message);
        maxSeverity = Validators.max(maxSeverity, message.getSeverity());
    }

    @Override
    public Set<Message> getMessages() {
        return Collections.unmodifiableSet(messages);
    }

    @Override
    public Message.Severity getMaxSeverity() {
        return maxSeverity;
    }

    @Override
    public boolean containsSeverity(@NotNull Message.Severity severity) {
        return maxSeverity != null && maxSeverity.includes(severity);
    }

    public void reset() {
        maxSeverity = null;
    }

    public void flush(ValidationResult to) {
        flush(to, null);
    }

    public void flush(ValidationResult to, Message.Severity threshold) {
        if ( messages != null ) {
            for ( Message msg : messages ) {
                if ( threshold == null || threshold.compareTo(msg.getSeverity()) <= 0 ) {
                    to.add(msg);
                }
            }
        }
    }
}
