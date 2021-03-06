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

package ch.raffael.util.binding;

import ch.raffael.util.binding.Binding;
import ch.raffael.util.binding.PresentationModel;
import ch.raffael.util.binding.validate.ValidationListener;
import ch.raffael.util.binding.validate.ValidationResult;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Adapter<B, T> {

    Binding<B> getBinding();

    T getTarget();

    void addValidationListener(ValidationListener listener);
    void removeValidationListener(ValidationListener listener);

    void validate();

    ValidationResult getValidationStatus();

}
