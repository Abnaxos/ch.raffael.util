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

import java.util.Collection;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class OrValidator<T> extends AbstractCompoundValidator<T> {

    private boolean keepWarnings = false;

    public OrValidator() {
    }

    public OrValidator(Validator<T> tValidator) {
        super(tValidator);
    }

    public OrValidator(Collection<Validator<T>> validator) {
        super(validator);
    }

    public OrValidator(boolean keepWarnings) {
        this.keepWarnings = keepWarnings;
    }

    public OrValidator(Validator<T> validator, boolean keepWarnings) {
        super(validator);
        this.keepWarnings = keepWarnings;
    }

    public OrValidator(boolean keepWarnings, Collection<Validator<T>> validator) {
        super(validator);
        this.keepWarnings = keepWarnings;
    }

    @Override
    public void validate(T value, ValidationResult result) {
        ValidationResultBuffer buffer = new ValidationResultBuffer();
        boolean hadValid = false;
        for ( Validator<T> validator : this ) {
            buffer.reset();
            validator.validate(value, result);
            if ( buffer.getMaxSeverity() == null || buffer.getMaxSeverity() == Message.Severity.WARNING ) {
                hadValid = true;
                if ( !keepWarnings ) {
                    return;
                }
            }
        }
        // flush the buffer
        if ( hadValid ) {
            // flush warnings only
            buffer.flush(result, Message.Severity.WARNING);
        }
        else {
            // each validator had at least one error => flush everything
            buffer.flush(result);
        }
    }

    @Override
    public OrValidator<T> add(Validator<T> tValidator) {
        super.add(tValidator);
        return this;
    }

    @Override
    public OrValidator<T> add(Collection<Validator<T>> validators) {
        super.add(validators);
        return this;
    }

    public OrValidator<T> or(Validator<T> tValidator) {
        return add(tValidator);
    }


}
