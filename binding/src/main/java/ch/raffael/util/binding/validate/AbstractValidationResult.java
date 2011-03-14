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
public abstract class AbstractValidationResult implements ValidationResult {

    protected AbstractValidationResult() {
    }

    @Override
    public void addError(String message) {
        add(Message.error(message));
    }

    @Override
    public void addError(String message, Object details) {
        add(Message.error(message, details));
    }

    @Override
    public void addWarning(String message) {
        add(Message.warning(message));
    }

    @Override
    public void addWarning(String message, Object details) {
        add(Message.warning(message, details));
    }
}
