package ch.raffael.util.groovy.dsl

import ch.raffael.util.groovy.Groovy

/**
 * Main entry point for the DSL framework.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
final class DslScripts {

    private DslScripts() {
    }

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
        context.withDelegate(rootDelegate) { DslDelegate dsld ->
            //noinspection GroovyMissingReturnStatement
            script.metaClass = Groovy.makeExpando(script) { emc ->
                def invokeDslMethod = { String name, args ->
                    context.invokeDelegateMethod(dsld, name, args as Object[])
                }
                def getDslProperty = { String name ->
                    context.getDelegateProperty(dsld, name)
                }
                emc.invokeDslMethod = invokeDslMethod
                emc.methodMissing = invokeDslMethod
                emc.getDslProperty = getDslProperty
                emc.propertyMissing = getDslProperty
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
        new DslContext().withDelegate(rootDelegate) { DslDelegate dsld ->
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
