package ch.raffael.util.groovy.dsl

import ch.raffael.util.groovy.Groovy

/**
 * Main entry point for the DSL framework.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslScript {

    /**
     * Evaluate a DSL script using the given root delegate.
     *
     * @param script          The script.
     * @param binding         The bindings for the script.
     * @param rootDelegate    The root delegate.
     *
     * @return The return value of the script.
     */
    static eval(Script script, rootDelegate, Binding binding = null, Closure expandoSetup = null) {
        if ( binding != null ) {
            script.setBinding(binding)
        }
        DslContext context = new DslContext()
        context.withDelegate(null, rootDelegate) { DslDelegate dsld ->
            script.metaClass = Groovy.makeExpando(script) { emc ->
                emc.methodMissing = { String name, args ->
                    context.invokeDelegateMethod(dsld, name, args as Object[])
                }
                emc.propertyMissing = { String name ->
                    context.getDelegateProperty(dsld, name)
                }
                if ( expandoSetup != null ) {
                    Groovy.prepare(expandoSetup, null).call(emc)
                }
            }
            if ( binding != null ) {
                script.setBinding(binding)
            }
            script.run()
        }
    }

    /**
     * Evaluate a DSL closure using the given root delegate.
     *
     * @param closure         The closure.
     * @param rootDelegate    The root delegate.
     * @param args            Arguments to the closure.
     *
     * @return The return value of the closure.
     */
    static eval(Closure closure, rootDelegate, Object... args) {
        new DslContext().withDelegate(null, rootDelegate) { DslDelegate dsld ->
            Groovy.prepare(closure, dsld, Closure.DELEGATE_FIRST).call(args)
        }
    }

    /**
     * Run a DSL script using the given root delegate.
     *
     * @param script          The script.
     * @param binding         The binding for the script.
     * @param rootDelegate    The root delegate.
     *
     * @return The root delegate.
     */
    static <T> T run(Script script, T rootDelegate, Binding binding = null, Closure expandoSetup = null) {
        eval(script, rootDelegate, binding, expandoSetup)
        return rootDelegate
    }

    /**
     * Run a DSL closure using the given root delegate.
     *
     * @param closure         The closure.
     * @param rootDelegate    The root delegate.
     * @param args            Arguments to the closure
     *
     * @return The root delegate.
     */
    static <T> T run(Closure closure, T rootDelegate, Object... args) {
        eval(closure, rootDelegate, args)
        return rootDelegate
    }

}
