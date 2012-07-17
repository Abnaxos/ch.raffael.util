package ch.raffael.util.contracts.internal;


import spock.lang.Specification

import static ch.raffael.util.contracts.internal.ContractsPolicy.*

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings(["GroovyPointlessArithmetic", "GroovyAccessibility"])
class ContractsPolicySpec extends Specification {

    def "Creation of children"() {
      given:
        def name = '$' + UUID.randomUUID().toString().replaceAll('-', '.\\$')
        def prevSize = ContractsPolicy.POLICIES.size()

      when: "Get a policy"
        def p = getPolicy(name)
      and: "Get the same policy a second time"
        def p2 = getPolicy(name)

      then:
        p != null
        p == p2
        ContractsPolicy.POLICIES.size() == prevSize + 5
    }

    def "Invalid names throw IllegalArgumentException"() {
      when:
        getPolicy(name)

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
        def p = getPolicy(ContractsPolicySpec)

      then:
        p.name == ContractsPolicySpec.name
    }

    def "Use outer class for inner classes"() {
      when:
        def p = getPolicy(ContractsPolicySpec.Inner)

      then:
        p.name == ContractsPolicySpec.name
    }

    def "Use package name as policy name"() {
      when:
        def p = getPolicy(ContractsPolicySpec.getPackage())

      then:
        p.name == ContractsPolicySpec.getPackage().getName()
    }

    private class Inner {}

}
