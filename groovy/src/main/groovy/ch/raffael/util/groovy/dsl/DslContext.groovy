package ch.raffael.util.groovy.dsl

import ch.raffael.util.groovy.Groovy

import java.lang.reflect.Method

/**
 * Internal DSL context.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslContext {

    private final static Object[] EMPTY_ARGS = new Object[0]

    DslDelegate currentDelegate = null

    DslContext() {
    }

    def withDelegate(DslDelegate parent, delegate, Closure closure) {
        DslDelegate wrapper = new DslDelegate(parent, this, delegate)
        DslDelegate prev = currentDelegate
        currentDelegate = wrapper
        try {
            Groovy.prepare(closure, null).call(wrapper)
        }
        finally {
            currentDelegate = prev
        }
    }

    def getDelegateProperty(DslDelegate dslDelegate, String name) {
        doInvokeDelegateMethod(dslDelegate, dslDelegate, name, EMPTY_ARGS)
    }

    def invokeDelegateMethod(DslDelegate dslDelegate, String name, Object[] args) {
        doInvokeDelegateMethod(dslDelegate, dslDelegate, name, args)
    }

    private doInvokeDelegateMethod(DslDelegate dslDelegate, DslDelegate topInvoker, String name, Object[] args) {
        MissingMethodException missingMethod = null
        for ( delegate in dslDelegate.@delegates ) {
            Closure closure = null
            Object[] callArgs = args
            MetaMethod method
            WithBody withBody
            try {
                if ( !dslDelegate.is(currentDelegate) && delegate.class.getAnnotation(Inherited) == null ) {
                    throw new MissingMethodException(name, DslDelegate, args)
                }
                method = pickMethod(delegate, name, callArgs)
                if ( method == null ) {
                    // try without the closure, if there is one
                    if ( args && args[-1] instanceof Closure ) {
                        closure = (Closure)args[-1]
                        callArgs = args.take(args.length-1)
                        method = pickMethod(delegate, name, callArgs)
                    }
                }
                Method javaMethod = method ? findJavaMethod(method) : null
                if ( javaMethod == null || !javaMethod.getAnnotation(DSL) ) {
                    return tryFallback(delegate, name, args)
                }
                withBody = javaMethod.getAnnotation(WithBody)
                if ( !withBody && closure ) {
                    // no @WithBody, but we have a closure -> no match
                    return tryFallback(delegate, name, args)
                }
            }
            catch ( MissingMethodException e ) {
                missingMethod = e
                continue
            }
            def retval = method.doMethodInvoke(delegate, callArgs)
            if ( withBody ) {
                if ( closure != null ) {
                    withDelegate(topInvoker, retval) { DslDelegate dsld ->
                        closure = Groovy.prepare(closure, dsld, Closure.DELEGATE_FIRST)
                        if ( withBody.invoker() ) {
                            retval = delegate."${withBody.invoker()}"(closure)
                        }
                        else {
                            retval = closure.call()
                        }
                    }
                }
            }
            return retval
        }
        assert missingMethod != null
        // try the delegate stack upwards
        if ( dslDelegate.@parent != null ) {
            try {
                doInvokeDelegateMethod(dslDelegate.@parent, topInvoker, name, args)
            }
            catch ( MissingMethodException e ) {
                throw missingMethod
            }
        }
        else {
            throw missingMethod
        }
    }

    private static Method findJavaMethod(MetaMethod metaMethod) {
        if ( !metaMethod.isPublic() || metaMethod.isStatic()) {
            return null
        }
        Class javaClass = metaMethod.declaringClass.theClass
        try {
            return javaClass.getMethod(metaMethod.name, metaMethod.nativeParameterTypes)
        }
        catch ( NoSuchMethodException e ) {
            return null
        }
    }

    private static tryFallback(delegate, String name, Object[] args) {
        for ( method in delegate.metaClass.methods.reverse() ) {
            def javaMethod = findJavaMethod(method)
            if ( javaMethod?.getAnnotation(Dynamic.class) && method.isValidMethod([String, Object[]] as Class[])) {
                try {
                    return method.doMethodInvoke(delegate, [name, args] as Object[])
                }
                catch ( MissingMethodException e ) {
                    // ignore
                }
            }
        }
        throw new MissingMethodException(name, DslDelegate, args)
    }

    private static MetaMethod pickMethod(delegate, String name, Object[] args) {
        delegate.metaClass.pickMethod(name, args.collect({ arg -> arg?.getClass() }) as Class[])
    }
}
