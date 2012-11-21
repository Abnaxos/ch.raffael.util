package ch.raffael.util.groovy.dsl

import spock.lang.Shared
import spock.lang.Specification;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslSpec extends Specification {

    @Shared
    def shell = new GroovyShell()

    def "DSL invokes methods annotated with @DSL, evals closures when with @WithBody"() {
      when:
        def root = eval('hello { greet \'DSL\' }')

      then:
        root.result == 'Hello DSL'
    }

    def "Methods not annotated with @DSL won't be available"() {
      when:
        def root = eval("hello { noDsl() }")

      then:
        def e = thrown(MissingMethodException)
        e.method == 'noDsl'
    }

    def "If no annotated method can be found, look for @Dynamic and try that one"() {
      when:
        def root = eval('hello { any "foo", 42 }')

      then:
        root.result == [ "foo", 42 ] as Object[]

    }

    def "Methods with no arguments may be referred to as property"() {
      when:
        def root = eval('hello { noargs }')

      then:
        root.result == 'no args'
    }

    def "If the delegate is a collection, the elements will be tried in order"() {
      when:
        def root = eval("hello2 { $method \"$arg\" }")

      then:
        root.result == result

      where:
        method   | arg          || result
        'greet'  | 'Collection' || 'Hello Collection'
        'second' | 'Foobar'     || 'Second: Foobar'
    }

    def "It's also possible to just call a method with no closure"() {
      when:
        def root = eval("hello.greet 'Shortcut'")

      then:
        root.result == 'Hello Shortcut'
    }

    def "Call closure using invoker if specified in @WithBody"() {
      when:
        def root = eval("withInvoker { name -> greet name }")

      then:
        root.result == 'Hello Invoker'
    }

    def "Specifying a closure on a method without @WithBody won't work"() {
      when:
        eval("hello { greet('Foo') { println 'Error' } }")

      then:
        def e = thrown(MissingMethodException)
        e.method == 'greet'
    }

    def "If there is a method with a closure parameter, it will be called, no action taken with closure"() {
      when:
        def root = eval("hello { closure { return 'This is my closure' } }")

      then:
        root.result() == 'This is my closure'
    }

    def "You can also eval closures"() {
      when:
        DslScript.eval({
            hello {
                greet 'Foo'
            }
            withInvoker { name ->
                greet name
            }
            hello.greet 'Bar'
        }, new DslRoot())

      then:
        // we're not going to test things through here; if there was no exception, it'll worked
        true
    }

    private <T> T eval(String script) {
        def root = new DslRoot()
        DslScript.eval(shell.parse(script), root)
        return root
    }

}
