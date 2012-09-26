package ch.raffael.util.classloader;


import spock.lang.Specification

import static ch.raffael.util.classloader.Dispatcher.Type.*

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class DelegatingClassLoaderSpec extends Specification {

    RecordingClassLoader delegate = new RecordingClassLoader()
    RecordingClassLoader parent = new RecordingClassLoader()
    Dispatcher dispatcher = Mock(Dispatcher)
    DelegatingClassLoader loader = DelegatingClassLoader.builder().dispatch("foo/", dispatcher).build(parent)

    def "Delegates to dispatcher"() {
      when:
        tryLoad("foo.bar.FooBar")
        tryLoad("bar.foo.BarFoo")

      then:
        "c:foo.bar.FooBar" in delegate
        "c:foo.bar.FooBar"  in parent
        !("c:bar.foo.BarFoo" in delegate)
        "c:bar.foo.BarFoo" in parent
        1 * dispatcher.getClassLoaderFor("foo/bar", "FooBar", CLASS) >> delegate
        0 * _._()
    }

    def "Also uses dispatcher for getResource()"() {
      when:
        loader.getResource("foo/bar/foo-bar")
        loader.getResource("bar/foo/bar-foo")

      then:
        "r:foo/bar/foo-bar" in delegate
        "r:foo/bar/foo-bar" in parent
        !("r:bar/foo/bar-foo" in delegate)
        "r:bar/foo/bar-foo" in parent
        1 * dispatcher.getClassLoaderFor("foo/bar", "foo-bar", RESOURCE) >> delegate
        0 * _._()
    }

    def "Also uses dispatcher for getResources()"() {
      when:
        loader.getResources("foo/bar/foo-bar")
        loader.getResources("bar/foo/bar-foo")

      then:
        "r:foo/bar/foo-bar" in delegate
        "r:foo/bar/foo-bar" in parent
        !("r:bar/foo/bar-foo" in delegate)
        "r:bar/foo/bar-foo" in parent
        1 * dispatcher.getClassLoaderFor("foo/bar", "foo-bar", RESOURCE) >> delegate
        0 * _._()
    }

    @SuppressWarnings("GroovyUnusedCatchParameter")
    private Class tryLoad(String name) {
        try {
            return Class.forName(name, false, loader)
        }
        catch ( ClassNotFoundException e ) {
            return null
        }
    }

}
