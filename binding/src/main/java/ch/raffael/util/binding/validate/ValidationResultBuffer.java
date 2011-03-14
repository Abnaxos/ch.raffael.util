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

import java.util.LinkedHashSet;
import java.util.Set;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
class ValidationResultBuffer extends AbstractValidationResult {

    private Set<Message> messages = new LinkedHashSet<Message>();
    private Message.Severity didAdd = null;

    @Override
    public void add(Message message) {
        messages.add(message);
        if ( didAdd == null || didAdd.compareTo(message.getSeverity()) > 1 ) {
            didAdd = message.getSeverity();
        }
    }

    public Message.Severity didAdd() {
        return didAdd;
    }

    public void reset() {
        didAdd = null;
    }

    public void flush(ValidationResult to) {
        flush(to, null);
    }

    public void flush(ValidationResult to, Message.Severity threshold) {
        for ( Message msg : messages ) {
            if ( threshold == null || threshold.compareTo(msg.getSeverity()) <= 0 ) {
                to.add(msg);
            }
        }
    }
}
