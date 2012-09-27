package ch.raffael.util.classloader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Maps packages or package hierarchies. A key of '<code>foo/bar</code>' denotes an exact
 * match, i.e. the package <code>foo/bar/foobar</code> doesn't match. To include
 * sub-packages add a trailing slash ('<code>foo/bar/</code>').
 * <p/>
 * This class is not thread-safe.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PackageMap<T> {

    private final Map<String, T> map = new HashMap<String, T>();
    private final Map<String, T> cache = new HashMap<String, T>();

    public PackageMap() {
    }

    public PackageMap(Map<String, T> entries) {
        map.putAll(entries);
    }

    @Override
    public String toString() {
        return "PackageMap{" + map + "}";
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( (o instanceof PackageMap) ) {
            return false;
        }
        return map.equals(((PackageMap)o).map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    public Map<String, T> asMap() {
        return new HashMap<String, T>(map);
    }

    public T put(String name, T value) {
        cache.clear();
        return map.put(name, value);
    }

    public void putAll(Map<String, T> map) {
        cache.clear();
        this.map.putAll(map);
    }

    public T remove(String name) {
        cache.clear();
        return map.remove(name);
    }

    public void removeAll(Iterable<String> names) {
        cache.clear();
        for ( String n : names ) {
            map.remove(n);
        }
    }

    public void removeAll(Iterator<String> names) {
        cache.clear();
        while ( names.hasNext() ) {
            map.remove(names.next());
        }
    }

    public void clear() {
        cache.clear();
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public T get(String name) {
        // try the exact match first
        T value = map.get(name);
        if ( value != null ) {
            return value;
        }
        // try the cache
        value = cache.get(name);
        if ( value != null ) {
            return value;
        }
        // try sub-package matches
        String search = name + "/";
        while ( true ) {
            value = map.get(search);
            if ( value != null ) {
                cache.put(name, value);
                return value;
            }
            int pos = search.lastIndexOf('/', search.length() - 2);
            if ( pos < 0 ) {
                return null;
            }
            search = search.substring(0, pos + 1);
        }
    }

    public T get(String name, T fallback) {
        T value = get(name);
        if ( value == null ) {
            return fallback;
        }
        else {
            return value;
        }
    }

}
