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

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 2011030401L;

    @SuppressWarnings( { "NonSerializableFieldInSerializableClass" })
    private final Object details;

    public ValidationException() {
        super(Message.ensureMessage(null));
        this.details = null;
    }

    public ValidationException(String message) {
        super(Message.ensureMessage(message));
        this.details = null;
    }

    public ValidationException(String message, Throwable cause) {
        super(Message.ensureMessage(message), cause);
        this.details = null;
    }

    public ValidationException(Throwable cause) {
        super(Message.ensureMessage(null), cause);
        this.details = null;
    }

    public ValidationException(Object details) {
        super(Message.ensureMessage(null));
        this.details = details;
    }

    public ValidationException(String message, Object details) {
        super(Message.ensureMessage(message));
        this.details = details;
    }

    public ValidationException(String message, Throwable cause, Object details) {
        super(Message.ensureMessage(message), cause);
        this.details = details;
    }

    public ValidationException(Throwable cause, Object details) {
        super(Message.ensureMessage(null), cause);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }
}
