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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.sun.xml.internal.stream.util.ReadOnlyIterator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractCompoundValidator<T> implements Validator<T>, Iterable<Validator<T>> {

    private final Set<Validator<T>> validators = new LinkedHashSet<Validator<T>>();

    protected AbstractCompoundValidator() {
    }

    protected AbstractCompoundValidator(Validator<T> validator) {
        add(validator);
    }

    protected AbstractCompoundValidator(Validator<T>... validator) {
        add(validators);
    }

    protected AbstractCompoundValidator(Collection<Validator<T>> validator) {
        add(validators);
    }

    public void add(Validator<T> validator) {
        validators.add(validator);
    }

    public void add(Validator<T>... validators) {
        this.validators.addAll(Arrays.asList(validators));
    }

    public void add(Collection<Validator<T>> validators) {
        this.validators.addAll(validators);
    }

    @Override
    public Iterator<Validator<T>> iterator() {
        return new ReadOnlyIterator(validators.iterator());
    }
}
