package ch.raffael.util.classloader

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class RecordingClassLoader extends ClassLoader {

    List<String> recorded = []

    RecordingClassLoader(ClassLoader classLoader) {
        super(classLoader)
    }

    RecordingClassLoader() {
        this(RecordingClassLoader.classLoader)
    }

    @Override
    String toString() {
        return recorded as String
    }

    @Override
    Class<?> loadClass(String name) {
        recorded << "c:$name"
        return super.loadClass(name)
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) {
        recorded << ("c:$name" as String)
        return super.loadClass(name, resolve)
    }

    @Override
    URL getResource(String name) {
        recorded << ("r:$name" as String)
        return super.getResource(name)
    }

    @Override
    Enumeration<URL> getResources(String name) {
        recorded << ("r:$name" as String)
        return super.getResources(name)
    }

    boolean isCase(Object obj) {
        return obj in recorded
    }

}
