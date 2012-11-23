package ch.raffael.util.groovy

import spock.lang.Specification

import static ch.raffael.util.groovy.MapValidator.validate

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class MapValidatorSpec extends Specification {

    def "Missing required arguments throw an exception"() {
      when:
        validate(foo: 42, bar: 'foobar') {
            required 'foo'
            required 'bar'
            required 'miss'
        }

      then:
        def e = thrown IllegalArgumentException
        e.message.contains("miss is required")
    }

    def "Unknown arguments throw an exception"() {
      when:
        validate(foo: 42, bar: 'foobar', unknown: 'throw') {
            required 'foo'
            optional 'bar'
            optional 'foobar'
        }

      then:
        def e = thrown IllegalArgumentException
        e.message == 'Unknown values: [unknown]'
    }

    def "Wrong types throw an exception"() {
      when:
        validate(foo: 'foo') {
            delegate."$method" 'foo', Integer, Double
        }

      then:
        def e = thrown IllegalArgumentException
        e.message.endsWith('[class java.lang.Integer, class java.lang.Double]')

      where:
        method << [ 'required', 'optional' ]
    }

    def "Multiple types are possible"() {
      when:
        validate(foo: val) {
            delegate."$method" 'foo', Integer, String
        }

      then:
        notThrown(Throwable)

      where:
        [val, method] << [[ 42, 'foo' ], [ 'required', 'optional' ]].combinations()
    }

    def "No exception if everything's just fine"() {
      when:
        validate([foo: 'bar', bar: 42]) {
            required 'foo', String, GString
            optional 'bar', Integer, String
            optional 'missing', Boolean
        }

      then:
        notThrown(Throwable)
    }

}
