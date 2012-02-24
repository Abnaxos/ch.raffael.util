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

import java.io.Serializable;

import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UMultimap<K, V> extends ForwardingMultimap<K, V> implements Serializable {
    private static final long serialVersionUID = 12022401L;

    private final Multimap<K, V> delegate;
    private final Multimap<K, V> unmodifiable;

    public UMultimap() {
        this(HashMultimap.<K, V>create());
    }

    public UMultimap(Multimap<K, V> delegate) {
        this.delegate = delegate;
        this.unmodifiable = Multimaps.unmodifiableMultimap(delegate);
    }

    @Override
    protected Multimap<K, V> delegate() {
        return delegate;
    }

    public Multimap<K, V> unmodifiable() {
        return unmodifiable;
    }

}
