package ch.raffael.util.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.common.collections.TokenMap;


/**
 * @deprecated Use {@link ch.raffael.util.common.collections.TokenMap} instead.
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Deprecated
public class Token extends TokenMap {

    @NotNull
    @Override
    public Token synchronize() {
        return new SynchronizedToken();
    }

    @SuppressWarnings("deprecation")
    protected class SynchronizedToken extends Token {
        @Override
        public synchronized String toString() {
            return super.toString();
        }
        @Override
        public synchronized boolean equals(Object o) {
            return super.equals(o);
        }
        @Override
        public synchronized int hashCode() {
            return super.hashCode();
        }
        @Override
        public synchronized <T> T put(@NotNull Class<T> type, @Nullable Object key, @Nullable T object) {
            return super.put(type, key, object);
        }
        @Override
        public synchronized <T> T get(@NotNull Class<T> type, @Nullable Object key) {
            return super.get(type, key);
        }
        @NotNull
        @Override
        public Token synchronize() {
            return this;
        }
    }

}
