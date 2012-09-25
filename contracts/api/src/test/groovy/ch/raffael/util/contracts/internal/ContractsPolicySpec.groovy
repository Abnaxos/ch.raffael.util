package ch.raffael.util.contracts.internal;


import spock.lang.Specification

import static ch.raffael.util.contracts.internal.ContractsContext.*

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings(["GroovyPointlessArithmetic", "GroovyAccessibility"])
class ContractsPolicySpec extends Specification {

    def "Creation of children"() {
      given:
        def name = '$' + UUID.randomUUID().toString().replaceAll('-', '.\\$')
        def prevSize = ContractsContext.CONTEXTS.size()

      when: "Get a policy"
        def p = getContext(name)
      and: "Get the same policy a second time"
        def p2 = getContext(name)

      then:
        p != null
        p == p2
        ContractsContext.CONTEXTS.size() == prevSize + 5
    }

    def "Invalid names throw IllegalArgumentException"() {
      when:
        getContext(name)

      then:
        def iae = thrown(IllegalArgumentException)
        iae.message.startsWith('Illegal policy name:')

      where:
        name << [
                '',
                '.',
                ' ',
                'foo.3bar',
                '.foo.bar',
                'foo.bar.',
                'foo..bar'
        ]
    }

    def "Use class name as policy name"() {
      when:
        def p = getContext(ContractsPolicySpec)

      then:
        p.name == ContractsPolicySpec.name
    }

    def "Use outer class for inner classes"() {
      when:
        def p = getContext(ContractsPolicySpec.Inner)

      then:
        p.name == ContractsPolicySpec.name
    }

    def "Use package name as policy name"() {
      when:
        def p = getContext(ContractsPolicySpec.getPackage())

      then:
        p.name == ContractsPolicySpec.getPackage().getName()
    }

    private class Inner {}

}
