package ch.raffael.util.common.collections;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TokenMap implements Serializable {
    private static final long serialVersionUID = 12022401L;

    private final Map<Key, Object> map = new HashMap<Key, Object>();

    @Override
    public String toString() {
        return "TokenMap" + map;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || !(o instanceof TokenMap) ) {
            return false;
        }
        TokenMap that = (TokenMap)o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    public void putAll(TokenMap that) {
        putAll(that.getAll());
    }

    public void putAllAbsent(TokenMap that) {
        putAllAbsent(that.getAll());
    }

    protected void putAll(Map<Key, Object> map) {
        this.map.putAll(map);
    }

    protected void putAllAbsent(Map<Key, Object> map) {
        for ( Map.Entry<Key, Object> entry : map.entrySet() ) {
            if ( !this.map.containsKey(entry.getKey()) ) {
                this.map.put(entry.getKey(), entry.getValue());
            }
        }
    }

    protected Map<Key, Object> getAll() {
        return map;
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
    public TokenMap synchronize() {
        return synchronize(this);
    }

    @NotNull
    public TokenMap synchronize(Object sync) {
        return new SynchronizedTokenMap(sync);
    }

    protected final static class Key {
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

    protected static class SynchronizedTokenMap extends TokenMap {
        private static final long serialVersionUID = 12022401L;
        private final Object sync;
        protected SynchronizedTokenMap(Object sync) {
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
        public TokenMap synchronize(Object sync) {
            throw new IllegalStateException("Already synchronized");
        }
    }

}
