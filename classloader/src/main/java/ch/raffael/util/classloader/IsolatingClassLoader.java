package ch.raffael.util.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * A class loader that can be used to suppress parent delegation. Certain packages can be
 * excluded from the parent delegation as specified in the class loader contract.
 * <p/>
 * This class loader blatantly violates the class loading contract, so use with care.
 * <p/>
 * To include/exclude exact packages specify package names without trailing slash
 * (<code>foo/bar</code>), end the package name with a slash to also include/exclude
 * sub-packages (<code>foo/bar/</code>).
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 * @see PackageMap
 */
public class IsolatingClassLoader extends ClassLoader {

    private final PackageMap<Boolean> isolation = new PackageMap<Boolean>();
    private final boolean excludeByDefault;

    public IsolatingClassLoader(ClassLoader parent, Map<String, Boolean> isolation) {
        this(parent, isolation, false);
    }

    public IsolatingClassLoader(ClassLoader parent, Map<String, Boolean> isolation, boolean excludeByDefault) {
        super(parent);
        this.isolation.putAll(isolation);
        this.excludeByDefault = excludeByDefault;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "IsolatingClassLoader{" + isolation + "}";
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if ( isolation.get(packageName(name.replace('.', '/')), !excludeByDefault) ) {
            return super.loadClass(name, resolve);
        }
        else {
            return findClass(name);
        }
    }

    @Override
    public URL getResource(String name) {
        if ( isolation.get(packageName(name), !excludeByDefault) ) {
            return super.getResource(name);
        }
        else {
            return findResource(name);
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if ( isolation.get(packageName(name), !excludeByDefault) ) {
            return super.getResources(name);
        }
        else {
            return findResources(name);
        }
    }

    private String packageName(String name) {
        int pos = name.lastIndexOf('/');
        if ( pos < 0 ) {
            return null;
        }
        return name.substring(0, pos);
    }

    public static class Builder {

        private final Map<String, Boolean> isolation = new HashMap<String, Boolean>();
        private boolean excludeByDefault = false;

        protected Builder() {
        }

        public Builder include(String name) {
            isolation.put(name.replace('.', '/'), true);
            return this;
        }

        public Builder include(String... names) {
            include(Arrays.asList(names));
            return this;
        }

        public Builder include(Iterable<String> names) {
            for ( String n : names ) {
                include(n);
            }
            return this;
        }

        public Builder exclude(String name) {
            isolation.put(name.replace('.', '/'), false);
            return this;
        }

        public Builder exclude(String... names) {
            exclude(Arrays.asList(names));
            return this;
        }

        public Builder exclude(Iterable<String> names) {
            for ( String n : names ) {
                exclude(n);
            }
            return this;
        }

        public Builder excludeByDefault() {
            return excludeByDefault(true);
        }

        public Builder excludeByDefault(boolean excludeByDefault) {
            this.excludeByDefault = excludeByDefault;
            return this;
        }

        public IsolatingClassLoader build(ClassLoader parent) {
            return new IsolatingClassLoader(parent, isolation, excludeByDefault);
        }

    }

}
