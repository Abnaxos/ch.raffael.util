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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import ch.raffael.util.binding.PresentationModelMember;

import static com.google.common.collect.Iterators.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractCompoundValidator<T> extends PresentationModelMember implements Validator<T>, Iterable<Validator<T>> {

    private final Set<Validator<T>> validators = new LinkedHashSet<Validator<T>>();

    protected AbstractCompoundValidator() {
    }

    protected AbstractCompoundValidator(Validator<T> validator) {
        add(validator);
    }

    protected AbstractCompoundValidator(Collection<Validator<T>> validator) {
        add(validators);
    }

    public AbstractCompoundValidator<T> add(Validator<T> validator) {
        validators.add(validator);
        return this;
    }

    public AbstractCompoundValidator<T> add(Collection<Validator<T>> validators) {
        this.validators.addAll(validators);
        return this;
    }

    @Override
    public Iterator<Validator<T>> iterator() {
        return unmodifiableIterator(validators.iterator());
    }
}
