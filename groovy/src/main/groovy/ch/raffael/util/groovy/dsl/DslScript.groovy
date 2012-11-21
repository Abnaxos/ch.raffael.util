package ch.raffael.util.groovy.dsl

import ch.raffael.util.groovy.Groovy

import static ch.raffael.util.groovy.dsl.DslInvoke.getDelegateProperty
import static ch.raffael.util.groovy.dsl.DslInvoke.invokeDelegateMethod

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslScript {

    static eval(Script script, Binding binding = null, rootDelegate) {
        if ( binding != null ) {
            script.setBinding(binding)
        }
        DslContext context = new DslContext()
        context.delegateStack.push(new DslDelegate(context, rootDelegate))
        def emc = new ExpandoMetaClass(script.getClass(), false)
        emc.methodMissing = { String name, args ->
            invokeDelegateMethod(context, context.delegateStack[-1].@delegate, name, args as Object[])
        }
        emc.propertyMissing = { String name ->
            getDelegateProperty(context, context.delegateStack[-1].@delegate, name)
        }
        emc.initialize()
        script.metaClass = emc
        return script.run()
    }

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

    static <T> T run(Script script, Binding binding = null, T rootDelegate) {
        eval(script, binding, rootDelegate)
        return rootDelegate
    }

    static <T> T run(Closure closure, T rootDelegate) {
        eval(closure, rootDelegate)
        return rootDelegate
    }

}
