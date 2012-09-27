package ch.raffael.util.classloader;


import spock.lang.Specification

import static ch.raffael.util.classloader.IsolatingClassLoader.*

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class IsolatingClassLoaderSpec extends Specification {

    private RecordingClassLoader recording = new RecordingClassLoader()
    private IsolatingClassLoader isolating

    def "Isolates class loading"() {
      given:
        isolating = builder().exclude("foo").build(recording)

      when:
        tryLoad("bar.Foo")
        tryLoad("foo.Bar")

      then:
        "c:bar.Foo" in recording
        !("c:foo.Bar" in recording)
    }

    def "Deepest matching isolation is used"() {
      given:
        isolating = builder().exclude("foo/").include("foo/bar").build(recording)

      when:
        tryLoad("foo.Bar")
        tryLoad("foo.bar.FooBar")

      then:
        !("c:foo.Bar" in recording)
        "c:foo.bar.FooBar" in recording
    }

    def "Also works for getResource()"() {
      given:
        isolating = builder().exclude("foo/").build(recording)

      when:
        isolating.getResource("foo/bar/foobar")
        isolating.getResource("bar/foo")

      then:
        !("r:foo/bar/foobar" in recording)
        "r:bar/foo" in recording
    }

    def "Also works for getResources()"() {
      given:
        isolating = builder().exclude("foo/").build(recording)

      when:
        isolating.getResources("foo/bar/foobar")
        isolating.getResources("bar/foo")

      then:
        !("r:foo/bar/foobar" in recording)
        "r:bar/foo" in recording
    }

    def "excludeByDefault turns things the other way round"() {
      given:
        isolating = builder().include("foo/").excludeByDefault().build(recording)

      when:
        tryLoad("foo.Bar")
        tryLoad("bar.Foo")

      then:
        "c:foo.Bar" in recording
        !("c:bar.Foo" in recording)
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
