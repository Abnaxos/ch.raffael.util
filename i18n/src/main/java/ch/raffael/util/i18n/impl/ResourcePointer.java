/*
 * Copyright 2010 Raffael Herzog
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

package ch.raffael.util.i18n.impl;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.i18n.LocaleSearch;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ResourcePointer {

    private final MethodSignature signature;
    private final Object[] selectors;
    private final LocaleSearch localeSearch;

    public ResourcePointer(@NotNull MethodSignature signature, @Nullable Object[] selectors, @NotNull LocaleSearch localeSearch) {
        this.signature = signature;
        if ( selectors != null && selectors.length == 0 ) {
            this.selectors = null;
        }
        else {
            this.selectors = selectors;
        }
        this.localeSearch = localeSearch;
    }

    @Override
    public String toString() {
        return "ResourcePointer{" +
                "signature=" + signature +
                ", selectors=" + (selectors == null ? null : Arrays.asList(selectors)) +
                ", localeSearch=" + localeSearch +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        ResourcePointer that = (ResourcePointer)o;
        if ( !localeSearch.equals(that.localeSearch) ) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if ( !Arrays.equals(selectors, that.selectors) ) {
            return false;
        }
        return signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        int result = signature.hashCode();
        result = 31 * result + (selectors != null ? Arrays.hashCode(selectors) : 0);
        result = 31 * result + localeSearch.hashCode();
        return result;
    }

    public MethodSignature getSignature() {
        return signature;
    }

    public Object[] getSelectors() {
        if ( selectors == null ) {
            return null;
        }
        else {
            return Arrays.copyOf(selectors, selectors.length);
        }
    }

    public LocaleSearch getLocaleSearch() {
        return localeSearch;
    }

    public String getKeyString() {
        String key = signature.getIndicator().toString();
        if ( selectors != null && selectors.length > 0 ) {
            StringBuilder buf = new StringBuilder(key);
            for ( Object sel : selectors ) {
                buf.append('#').append(sel);
            }
            key = buf.toString();
        }
        return key;
    }


}
