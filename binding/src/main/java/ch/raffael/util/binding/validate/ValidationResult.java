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

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.binding.InvalidValueException;

import static ch.raffael.util.binding.validate.Message.Severity.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ValidationResult extends ValidationStatus {

    ValidationResult EMPTY = new ValidationResult() {
        @Override
        public void add(Message message) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void addError(String message) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void addError(String message, Object details) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void addWarning(String message) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void addWarning(String message, Object details) {
            throw new UnsupportedOperationException();
        }
        @Override
        public Set<Message> getMessages() {
            return Collections.emptySet();
        }
        @Override
        public Message.Severity getMaxSeverity() {
            return null;
        }
        @Override
        public boolean containsSeverity(@NotNull Message.Severity severity) {
            return false;
        }
    };

    ValidationResult THROW = new ValidationResult() {
        @Override
        public void add(Message message) {
            if ( message.getSeverity() == ERROR ) {
                throw new InvalidValueException(message.getMessage(), message.getDetails());
            }
        }
        @Override
        public void addError(String message) {
            throw new InvalidValueException(message);
        }
        @Override
        public void addError(String message, Object details) {
            throw new InvalidValueException(message, details);
        }
        @Override
        public void addWarning(String message) {
        }
        @Override
        public void addWarning(String message, Object details) {
        }
        @Override
        public Set<Message> getMessages() {
            return Collections.emptySet();
        }
        @Override
        public Message.Severity getMaxSeverity() {
            return null;
        }
        @Override
        public boolean containsSeverity(@NotNull Message.Severity severity) {
            return false;
        }
    };

    void add(Message message);

    void addError(String message);

    void addError(String message, Object details);

    void addWarning(String message);

    void addWarning(String message, Object details);

    Message.Severity getMaxSeverity();

    boolean containsSeverity(@NotNull Message.Severity severity);

}
