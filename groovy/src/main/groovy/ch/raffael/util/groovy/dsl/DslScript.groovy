package ch.raffael.util.groovy.dsl

import ch.raffael.util.groovy.Groovy

import static ch.raffael.util.groovy.dsl.DslInvoke.getDelegateProperty
import static ch.raffael.util.groovy.dsl.DslInvoke.invokeDelegateMethod

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
    static eval(Script script, Binding binding = null, rootDelegate) {
        if ( binding != null ) {
            script.setBinding(binding)
        }
        DslContext context = new DslContext()
        context.delegateStack.push(new DslDelegate(context, rootDelegate))
        script.metaClass = Groovy.makeExpando(script) { emc ->
            emc.methodMissing = { String name, args ->
                invokeDelegateMethod(context, context.delegateStack[-1].@delegate, name, args as Object[])
            }
            emc.propertyMissing = { String name ->
                getDelegateProperty(context, context.delegateStack[-1].@delegate, name)
            }
        }
        if ( binding != null ) {
            script.setBinding(binding)
        }
        return script.run()
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
        def ctx = new DslContext()
        def dslDelegate = new DslDelegate(ctx, rootDelegate)
        ctx.delegateStack.push(dslDelegate)
        try {
            return Groovy.prepare(closure, dslDelegate, Closure.DELEGATE_FIRST).call(args)
        }
        finally {
            ctx.delegateStack.pop()
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
    static <T> T run(Script script, Binding binding = null, T rootDelegate) {
        eval(script, binding, rootDelegate)
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
        eval(closure, rootDelegate)
        return rootDelegate
    }

}
