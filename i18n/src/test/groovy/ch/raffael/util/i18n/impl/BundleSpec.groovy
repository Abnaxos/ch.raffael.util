package ch.raffael.util.i18n.impl;


import ch.raffael.util.i18n.I18N
import ch.raffael.util.i18n.I18NException
import spock.lang.FailsWith
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
class BundleSpec extends Specification {

    @Shared
    private TestRes res = I18N.getBundle(TestRes)

    def setup() {
        I18N.setLocale(new Locale("en"))
    }

    def "My own resource"() {
      expect:
        res.mine() == "This is my own thing"
    }

    def "Inherited resources"() {
      expect:
        res.inherited() == "Inherited from ResA"
        res.alsoInherited() == "Inherited from ResB"
    }

    @FailsWith(StackOverflowError)
    def "Ambiguous with explicit forward succeeds"() {
      expect:
        res.ambiguousWithForward() == "Ambiguous with forward from ResB"
    }

    def "Ambiguous with own 'implementation' succeeds"() {
      expect:
        res.ambiguousWithImpl() == "My own implementation"
    }

    def "Ambiguous without explicit forward or own implemenation throws"() {
      when:
        res.ambiguousWithError()
      then:
        def e = thrown(I18NException)
        e.message.startsWith("Ambiguous")
    }

    def "Forwarded resource"() {
      expect:
        res.forwarded() == "Forwarded to ResC"
    }

    def "Resource with parameter"() {
      expect:
        res.parameter("Raffi") == "Raffi is cool!"
    }

    def "Resource with parameter for locale de_CH"() {
      given:
        I18N.setLocale(new Locale("de", "CH"))
      expect:
        res.parameter("Raffi") == "Raffi ist cool!"
    }

    def "Resource with selector"() {
      expect:
        res.selector(selector) == value
      where:
        selector   | value
        FooBar.FOO | "Selected Foo"
        FooBar.BAR | "Selected Bar"
    }

}
