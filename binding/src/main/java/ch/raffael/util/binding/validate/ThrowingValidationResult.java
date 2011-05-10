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

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ThrowingValidationResult implements ValidationResult {

    private boolean throwOnWarning = false;
    private Message.Severity maxSeverity;

    public ThrowingValidationResult() {
    }

    public ThrowingValidationResult(boolean throwOnWarning) {
        this.throwOnWarning = throwOnWarning;
    }

    public boolean isThrowOnWarning() {
        return throwOnWarning;
    }

    public void setThrowOnWarning(boolean throwOnWarning) {
        this.throwOnWarning = throwOnWarning;
    }

    @Override
    public void add(Message message) {
        if ( throwOnWarning || message.getSeverity() == Message.Severity.ERROR ) {
            throw new ValidationException(message.getMessage(), message.getDetails());
        }
    }

    @Override
    public void addError(String message) {
        throw new ValidationException(message);
    }

    @Override
    public void addError(String message, Object details) {
        throw new ValidationException(message, details);
    }

    @Override
    public void addWarning(String message) {
        if ( throwOnWarning ) {
            throw new ValidationException(message);
        }
    }

    @Override
    public void addWarning(String message, Object details) {
        if ( throwOnWarning ) {
            throw new ValidationException(message, details);
        }
    }

    @Override
    public Message.Severity getMaxSeverity() {
        return maxSeverity;
    }

    @Override
    public boolean containsSeverity(@NotNull Message.Severity severity) {
        return maxSeverity != null && maxSeverity.includes(severity);
    }
}
