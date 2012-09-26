package ch.raffael.util.classloader;


import spock.lang.Specification

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class IsolatingClassLoaderSpec extends Specification {

    private RecordingClassLoader recording = new RecordingClassLoader()
    private IsolatingClassLoader isolating = new IsolatingClassLoader(recording)

    def "Isolates class loading"() {
      given:
        isolating.exclude("foo")

      when:
        tryLoad("bar.Foo")
        tryLoad("foo.Bar")

      then:
        "c:bar.Foo" in recording
        !("c:foo.Bar" in recording)
    }

    def "Deepest matching isolation is used"() {
      given:
        isolating.exclude("foo/")
        isolating.include("foo/bar")

      when:
        tryLoad("foo.Bar")
        tryLoad("foo.bar.FooBar")

      then:
        !("c:foo.Bar" in recording)
        "c:foo.bar.FooBar" in recording
    }

    def "Also works for getResource()"() {
      given:
        isolating.exclude("foo/")

      when:
        isolating.getResource("foo/bar/foobar")
        isolating.getResource("bar/foo")

      then:
        !("r:foo/bar/foobar" in recording)
        "r:bar/foo" in recording
    }

    def "Also works for getResources()"() {
      given:
        isolating.exclude("foo/")

      when:
        isolating.getResources("foo/bar/foobar")
        isolating.getResources("bar/foo")

      then:
        !("r:foo/bar/foobar" in recording)
        "r:bar/foo" in recording
    }

    @SuppressWarnings("GroovyUnusedCatchParameter")
    private Class tryLoad(String name) {
        try {
            return Class.forName(name, false, isolating)
        }
        catch ( ClassNotFoundException e ) {
            return null
        }
    }
}
