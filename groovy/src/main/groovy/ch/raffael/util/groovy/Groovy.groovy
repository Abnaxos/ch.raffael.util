package ch.raffael.util.groovy

import groovy.transform.CompileStatic

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@CompileStatic
final class Groovy {

    private Groovy() {
    }

    static boolean groovyTrue(Object object) {
        object ? true : false
    }

    static <T> Closure<T> prepare(Closure<T> closure, delegate, int resolveStrategy = Closure.OWNER_FIRST) {
        closure = (Closure)closure.clone()
        closure.delegate = delegate
        closure.resolveStrategy = resolveStrategy
        return closure
    }

}
