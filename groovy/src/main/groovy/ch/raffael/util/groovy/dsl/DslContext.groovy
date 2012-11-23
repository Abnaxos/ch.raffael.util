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

    final List<DslDelegate> delegateStack = []

    private Map<String, Object> properties = [:]

    DslContext() {
    }

    def getDelegateProperty(DslDelegate dslDelegate, String name) {
        assert !delegateStack.empty
        doInvokeDelegateMethod(dslDelegate, false, name, EMPTY_ARGS)
    }

    def invokeDelegateMethod(DslDelegate dslDelegate, String name, Object[] args) {
        assert !delegateStack.empty
        doInvokeDelegateMethod(dslDelegate, false, name, args)
    }

    private doInvokeDelegateMethod(DslDelegate dslDelegate, boolean inheritedOnly, String name, Object[] args) {
        MissingMethodException missingMethod = null
        for ( delegate in dslDelegate.@delegates ) {
            Closure closure = null
            Object[] callArgs = args
            MetaMethod method
            WithBody withBody
            try {
                if ( inheritedOnly && delegate.class.getAnnotation(Inherited) == null ) {
                    throw new MissingMethodException(name, delegate.getClass(), args)
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
                    def del = new DslDelegate(this, retval)
                    closure = Groovy.prepare(closure, del, Closure.DELEGATE_FIRST)
                    delegateStack.push(del)
                    try {
                        if ( withBody.invoker() ) {
                            retval = delegate."${withBody.invoker()}"(closure)
                        }
                        else {
                            retval = closure.call()
                        }
                    }
                    finally {
                        delegateStack.pop()
                    }
                }
            }
            return retval
        }
        assert missingMethod != null
        // try the delegate stack upwards
        int me = -1
        try {
            for ( d in delegateStack ) {
                me++
                if ( d.is(dslDelegate) ) {
                    if ( me == 0 ) {
                        throw missingMethod
                    }
                    else {
                        return doInvokeDelegateMethod(delegateStack[me-1], true, name, args)
                    }
                }
            }
        }
        catch ( MissingMethodException e ) {
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
        throw new MissingMethodException(name, delegate.getClass(), args)
    }

    private static MetaMethod pickMethod(delegate, String name, Object[] args) {
        delegate.metaClass.pickMethod(name, args.collect({ arg -> arg?.getClass() }) as Class[])
    }
}
