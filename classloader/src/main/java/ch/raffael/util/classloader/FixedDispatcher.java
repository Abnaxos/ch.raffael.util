package ch.raffael.util.classloader;

/**
 * A simple dispatcher that dispatches everything to the specified class loader.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class FixedDispatcher implements Dispatcher {

    private final ClassLoader classLoader;

    public FixedDispatcher(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public ClassLoader getClassLoaderFor(String packageName, String name, Type type) {
        return classLoader;
    }

}
