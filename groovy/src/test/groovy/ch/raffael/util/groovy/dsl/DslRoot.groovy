package ch.raffael.util.groovy.dsl

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslRoot {

    def result

    DslRoot() {
    }

    @DSL @WithBody
    def hello() {
        new Hello(parent: this)
    }

    @DSL @WithBody
    def hello2() {
        [new Hello(parent: this), new Second(parent: this)]
    }

    @DSL @WithBody(invoker='myInvoker')
    def withInvoker() {
        new Hello(parent: this)
    }

    @DSL @WithBody
    def list() {
        def l = new List()
        result = l.list
        return l
    }

    def myInvoker(Closure closure) {
        closure.call('Invoker')
    }

}
