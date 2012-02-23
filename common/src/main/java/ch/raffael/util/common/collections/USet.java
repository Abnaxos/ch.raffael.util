/*
 * Copyright 2012 Raffael Herzog
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

package ch.raffael.util.common.collections;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ForwardingSet;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class USet<T> extends ForwardingSet<T> {

    private final Set<T> delegate;
    private final Set<T> unmodifiable;

    public USet() {
        this(new HashSet<T>());
    }

    public USet(Set<T> delegate) {
        this.delegate = delegate;
        this.unmodifiable = Collections.unmodifiableSet(delegate);
    }

    @Override
    protected Set<T> delegate() {
        return delegate;
    }

    public Set<T> unmodifiable() {
        return unmodifiable;
    }

}
