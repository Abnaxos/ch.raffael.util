package ch.raffael.util.groovy

import groovy.transform.CompileStatic

/**
 * Provides some useful utilities.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
final class Groovy {

    private Groovy() {
    }

    /**
     * Apply groovy-truth to an object. This is mainly useful for Java code.
     *
     * @param object    The object to apply groovy-truth to.
     * @return true if the object is true according to groovy-truth, false otherwise
     */
    @CompileStatic
    static boolean groovyTrue(Object object) {
        object ? true : false
    }

    /**
     * Prepare a closure. This method will clone the the closure and set the delegate and
     * the resolve strategy.
     *
     * @param closure            The closure to be prepared
     * @param delegate           The delegate for the closure
     * @param resolveStrategy    The resolve strategy (OWNER_FIRST by default)
     *
     * @return The prepared closure.
     */
    static <T> Closure<T> prepare(Closure<T> closure, delegate, int resolveStrategy = Closure.OWNER_FIRST) {
        closure = (Closure)closure.clone()
        closure.delegate = delegate
        closure.resolveStrategy = resolveStrategy
        return closure
    }

    /**
     * Create and initialize a new ExpandoMetaClass. The closure will be called to do
     * setup work on the meta class (it will both be the delegate and passed as argument).
     * If the given target is a class, the ExpandoMetaClass will be globally registered,
     * otherwise it'll be a per-instance meta class.
     *
     * @param target      The target object or class.
     * @param setup       Closure to setup the meta class
     *
     * @return The initialized meta class.
     */
    static ExpandoMetaClass makeExpando(target, Closure<?> setup = null) {
        return makeExpando(target, target instanceof Class, setup)
    }

    /**
     * Create and initialize a new ExpandoMetaClass. The closure will be called to do
     * setup work on the meta class (it will both be the delegate and passed as argument).
     *
     * @param target      The target object or class.
     * @param register    Whether to register the meta class globally or not
     * @param setup       Closure to setup the meta class
     *
     * @return The initialized meta class.
     */
    static ExpandoMetaClass makeExpando(target, boolean register, Closure<?> setup = null) {
        def emc = new ExpandoMetaClass(target instanceof Class ? target : target.getClass(),
                                       register ?: target instanceof Class)
        prepare(setup, emc).call(emc)
        emc.initialize()
        return emc
    }

}
