package ch.raffael.util.classloader;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;


/**
 * A class loader that can be used to suppress parent delegation. Certain packages can
 * be excluded from the parent delegation as specified in the class loader contract.
 * <p/>
 * This class loader clearly violates the class loading contract, so use with care.
 * <p/>
 * To include/exclude exact packages specify package names without trailing slash
 * (<code>foo/bar</code>), end the package name with a slash to also include/exclude
 * sub-packages (<code>foo/bar/</code>).
 *
 * @see PackageMap
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class IsolatingClassLoader extends ClassLoader {

    private final PackageMap<Boolean> isolation = new PackageMap<Boolean>();

    public IsolatingClassLoader(ClassLoader parent) {
        super(parent);
    }

    public IsolatingClassLoader() {
    }

    @Override
    public String toString() {
        return "IsolatingClassLoader{"+ isolation + "}";
    }

    public void include(String name) {
        isolation.put(name.replace('.', '/'), true);
    }

    public void include(String... names) {
        include(Arrays.asList(names));
    }

    public void include(Iterable<String> names) {
        for ( String n : names ) {
            include(n);
        }
    }

    public void exclude(String name) {
        isolation.put(name.replace('.', '/'), false);
    }

    public void exclude(String... names) {
        exclude(Arrays.asList(names));
    }

    public void exclude(Iterable<String> names) {
        for ( String n : names ) {
            exclude(n);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if ( isolation.get(packageName(name.replace('.', '/')), true) ) {
            return super.loadClass(name, resolve);
        }
        else {
            return findClass(name);
        }
    }

    @Override
    public URL getResource(String name) {
        if ( isolation.get(packageName(name), true) ) {
            return super.getResource(name);
        }
        else {
            return findResource(name);
        }
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if ( isolation.get(packageName(name), true) ) {
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

}
