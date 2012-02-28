package ch.raffael.util.common;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.common.collections.TokenMap;


/**
 * @deprecated Use {@link ch.raffael.util.common.collections.TokenMap} instead.
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Deprecated
public class Token extends TokenMap {
    private static final long serialVersionUID = 12022401L;

    @NotNull
    @Override
    public Token synchronize() {
        return synchronize(this);
    }

    @NotNull
    @Override
    public Token synchronize(Object sync) {
        return new SynchronizedToken(sync);
    }

    @SuppressWarnings("deprecation")
    protected static class SynchronizedToken extends Token {
        private static final long serialVersionUID = 12022401L;
        private final Object sync;
        protected SynchronizedToken(Object sync) {
            this.sync = sync;
        }
        @Override
        public String toString() {
            synchronized ( sync ) {
                return super.toString();
            }
        }
        @Override
        public boolean equals(Object o) {
            synchronized ( sync ) {
                return super.equals(o);
            }
        }
        @Override
        public int hashCode() {
            synchronized ( sync ) {
                return super.hashCode();
            }
        }
        @Override
        protected void putAll(Map<Key, Object> map) {
            synchronized ( sync ) {
                super.putAll(map);
            }
        }
        @Override
        protected void putAllAbsent(Map<Key, Object> map) {
            synchronized ( sync ) {
                super.putAll(map);
            }
        }
        @Override
        protected Map<Key, Object> getAll() {
            synchronized ( sync ) {
                return new HashMap<Key, Object>(super.getAll());
            }
        }
        @Override
        public <T> T put(@NotNull Class<T> type, @Nullable Object key, @Nullable T object) {
            synchronized ( sync ) {
                return super.put(type, key, object);
            }
        }
        @Override
        public <T> T get(@NotNull Class<T> type, @Nullable Object key) {
            synchronized ( sync ) {
                return super.get(type, key);
            }
        }
        @NotNull
        @Override
        public Token synchronize() {
            return synchronize(this);
        }
        @NotNull
        @Override
        public Token synchronize(Object sync) {
            throw new IllegalStateException("Already synchronized");
        }
    }

}
