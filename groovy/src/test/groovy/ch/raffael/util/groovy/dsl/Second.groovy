package ch.raffael.util.groovy.dsl

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class Second {

    def parent

    @Dsl
    void greet(String name) {
        parent.result = "Hi $name (won't be called)"
    }

    @Dsl
    void second(String arg) {
        parent.result = "Second: $arg"
    }

}
