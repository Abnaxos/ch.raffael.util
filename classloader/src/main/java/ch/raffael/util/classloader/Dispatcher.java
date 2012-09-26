package ch.raffael.util.classloader;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Dispatcher {

    /**
     * Get a class loader to delegate the loading of the class or resource to.
     *
     * @param packageName The package name.
     * @param name        The name of the class or resource within the package.
     * @param type        <code>CLASS</code> or <code>RESOURCE</code>.
     *
     * @return The class loader to delegate to or <code>null</code>, to load the class or
     *         resource internally.
     */
    ClassLoader getClassLoaderFor(String packageName, String name, Type type);

    enum Type {
        CLASS, RESOURCE
    }

}
