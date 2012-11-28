package ch.raffael.util.groovy.dsl

import spock.lang.Shared
import spock.lang.Specification

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslSpec extends Specification {

    @Shared
    def shell = new GroovyShell()

    def "DSL invokes methods annotated with @DSL, evals closures when with @WithBody"() {
      when:
        def root = run('hello { greet \'DSL\' }')

      then:
        root.result == 'Hello DSL'
    }

    def "Methods not annotated with @DSL won't be available"() {
      when:
        def root = run("hello { noDsl() }")

      then:
        def e = thrown(MissingMethodException)
        e.method == 'noDsl'
    }

    def "Methods won't be available in nested closures"() {
      when:
        def firstRun = run {
            hello2 {
                greet 'FirstRun'
            }
        }
        //def root = run("hello { hello2 { throw Exception('Will not happen') } }")
        def root = run {
            hello {
                hello2 {
                    throw Exception('Will not happen')
                }
            }
        }

      then:
        def e = thrown(MissingMethodException)
        e.method == 'hello2'
        firstRun.result == 'Hello FirstRun'
    }

    def "Methods of DSL delegates annotated with @Inherited WILL be available in nested closures"() {
      when:
        DslScripts.run({
            hello {
                inheritThis("Foo")
            }
        }, [ new DslRoot(), new Inherit() ])

      then:
        def e = thrown(IllegalStateException)
        e.message == "Inherited: Foo"
    }

    def "If no annotated method can be found, look for @Dynamic and try that one"() {
      when:
        def root = run('hello { any "foo", 42 }')

      then:
        root.result == [ "foo", 42 ] as Object[]

    }

    def "Methods with no arguments may be referred to as property"() {
      when:
        def root = run('hello { noargs }')

      then:
        root.result == 'no args'
    }

    def "If the delegate is a collection, the elements will be tried in order"() {
      when:
        def root = run("hello2 { $method \"$arg\" }")

      then:
        root.result == result

      where:
        method   | arg          || result
        'greet'  | 'Collection' || 'Hello Collection'
        'second' | 'Foobar'     || 'Second: Foobar'
    }

    def "It's also possible to just call a method with no closure"() {
      when:
        def root = run("hello.greet 'Shortcut'")

      then:
        root.result == 'Hello Shortcut'
    }

    def "Shortcut won't work on @WithBody(required = true)"() {
      when:
        run {
            hello2.greet 'This throws'
        }

      then:
        def e = thrown MissingMethodException
        e.method == 'hello2'
    }

    def "Call closure using invoker if specified in @WithBody"() {
      when:
        def root = run("withInvoker { name -> greet name }")

      then:
        root.result == 'Hello Invoker'
    }

    def "Specifying a closure on a method without @WithBody won't work"() {
      when:
        run("hello { greet('Foo') { println 'Error' } }")

      then:
        def e = thrown(MissingMethodException)
        e.method == 'greet'
    }

    def "If there is a method with a closure parameter, it will be called, no action taken with closure"() {
      when:
        def root = run("hello { closure { return 'This is my closure' } }")

      then:
        root.result() == 'This is my closure'
    }

    def "You can also eval closures"() {
      when:
        DslScripts.run({
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

    def "Embedded logic works"() {
      when:
        def root = run {
            list {
                (1..5).each {
                    add it
                    try {
                        hello {
                            greet "Raffi"
                        }
                        assert false
                    }
                    catch ( MissingMethodException e ) {
                        assert e.method == 'hello'
                    }
                }
            }
        }

      then:
        root.result == [ 1, 2, 3, 4, 5 ]
    }

    private DslRoot run(Closure script) {
        DslScripts.run(script, new DslRoot())
    }

    private DslRoot run(String script) {
        def root = new DslRoot()
        DslScripts.run(shell.parse(script), root)
        return root
    }

}
