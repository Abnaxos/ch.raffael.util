package ch.raffael.util.groovy.dsl

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class Hello {

    def parent

    @DSL
    void greet(String name) {
        parent.result = "Hello ${name}"
    }

    void noDsl() {
        assert false
    }

    @Dynamic
    void dynamic(String name, Object[] args) {
        if ( name == 'any' ) {
            parent.result = args
        }
        else {
            throw new MissingMethodException(name, getClass(), args)
        }
    }

    @DSL
    void noargs() {
        parent.result = "no args"
    }

    @DSL
    def closure(Closure closure) {
        parent.result = closure
    }

    @DSL @WithBody
    def closure() {
        parent.result = "Nope, the method with closure parameter has precedence"
    }

}
