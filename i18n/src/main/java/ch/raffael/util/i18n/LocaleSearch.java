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

package ch.raffael.util.i18n;

import java.util.Arrays;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class LocaleSearch {

    public static final char DEFAULT_SEPARATOR = '_';
    
    private final String locale;
    private final String[] search;

    public LocaleSearch(String locale) {
        if ( locale == null ) {
            locale = Locale.getDefault().toString();
        }
        this.locale = locale;
        int count = 1;
        for ( int i = 0; i < locale.length(); i++ ) {
            if ( locale.charAt(i) == DEFAULT_SEPARATOR ) {
                count++;
            }
        }
        search = new String[count];
        search[0] = locale;
        int index = 1;
        for ( int i = locale.length() - 1; i >= 0; i-- ) {
            if ( locale.charAt(i) == DEFAULT_SEPARATOR ) {
                search[index++] = locale.substring(0, i);
            }
        }
    }

    public LocaleSearch(Locale locale) {
        this((locale == null ? Locale.getDefault() : locale).toString());
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder("LocaleSearch{");
        for ( int i = 0; i < search.length; i++ ) {
            if ( i > 0 ) {
                buf.append(',');
            }
            buf.append(search[i]);
        }
        return buf.append('}').toString();
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        return Arrays.equals(search, ((LocaleSearch)o).search);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(search);
    }

    @NotNull
    public Locale getLocale() {
        return new Locale(locale);
    }

    @NotNull
    public String[] getSearch() {
        return Arrays.copyOf(search, search.length);
    }

    @NotNull
    public String[] getSearch(@NotNull String prefix, @Nullable String postfix) {
        return getSearch(prefix, postfix, DEFAULT_SEPARATOR);
    }

    @NotNull
    public String[] getSearch(@NotNull String prefix, @Nullable String postfix, char separator) {
        if ( postfix == null ) {
            postfix = "";
        }
        String[] result = new String[search.length + 1];
        for ( int i = 0; i < search.length; i++ ) {
            result[i] = prefix + separator + search[i] + postfix;
        }
        result[result.length - 1] = prefix + postfix;
        return result;
    }

    @NotNull
    public <T> T find(@NotNull Finder<T> finder) {
        return find(finder, null);
    }

    @NotNull
    public <T> T find(@NotNull Finder<T> finder, @Nullable String notFoundMessage) {
        T result = tryFind(finder);
        if ( result == null ) {
            throw new NotFoundException(notFoundMessage != null ? notFoundMessage : finder.toString());
        }
        return result;
    }

    @SuppressWarnings({ "ForLoopReplaceableByForEach" })
    @Nullable
    public <T> T tryFind(@NotNull Finder<T> finder) {
        T result;
        for ( int i = 0; i < search.length; i++ ) {
            result = finder.find(search[i]);
            if ( result != null ) {
                return result;
            }
        }
        return finder.find(null);
    }

    public static interface Finder<T> {
        T find(@Nullable String locale);
    }

}
