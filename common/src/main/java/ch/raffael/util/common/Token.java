package ch.raffael.util.common;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Token {

    private final Map<Key, Object> map = new HashMap<Key, Object>();

    @Override
    public String toString() {
        return "Token" + map;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || !(o instanceof Token) ) {
            return false;
        }
        Token that = (Token)o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Nullable
    public <T> T put(@NotNull Class<T> type, @Nullable T object) {
        return put(type, null, object);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T put(@NotNull Class<T> type, @Nullable Object key, @Nullable T object) {
        if ( object == null ) {
            return (T)map.remove(new Key(type, key));
        }
        else {
            return (T)map.put(new Key(type, key), object);
        }
    }

    @Nullable
    public <T> T get(@NotNull Class<T> type) {
        return get(type, null);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T get(@NotNull Class<T> type, @Nullable Object key) {
        return (T)map.get(new Key(type, key));
    }

    @NotNull
    public <T> T require(@NotNull Class<T> type) {
        return require(type, null);
    }

    @NotNull
    public <T> T require(@NotNull Class<T> type, @Nullable Object key) {
        T value = get(type, key);
        if ( value == null ) {
            throw new IllegalStateException("No such value: " + new Key(type, key));
        }
        return value;
    }

    @NotNull
    public Token synchronize() {
        return new SynchronizedToken();
    }

    private final static class Key {
        private final Class<?> clazz;
        private final Object key;
        private Key(Class<?> clazz, Object key) {
            this.clazz = clazz;
            this.key = key;
        }
        @Override
        public String toString() {
            return "Key{" +
                    "class=" + clazz +
                    ",key=" + key +
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
            Key that = (Key)o;
            if ( !clazz.equals(that.clazz) ) {
                return false;
            }
            return !(key != null ? !key.equals(that.key) : that.key != null);
        }
        @Override
        public int hashCode() {
            int result = clazz.hashCode();
            result = 31 * result + (key != null ? key.hashCode() : 0);
            return result;
        }
    }

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
