package ch.raffael.util.groovy.dsl

import ch.raffael.util.groovy.Groovy

import java.lang.reflect.Method

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
final class DslInvoke {

    private final static Object[] EMPTY_ARGS = new Object[0]

    private DslInvoke() {
    }

    static Method findJavaMethod(MetaMethod metaMethod) {
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

    static getDelegateProperty(DslContext ctx, delegate, String name) {
        if ( delegate instanceof Collection ) {
            assert delegate
            MissingPropertyException lastMissingProperty = null
            for ( d in delegate ) {
                try {
                    return getDelegateProperty(ctx, d, name)
                }
                catch ( MissingPropertyException e ) {
                    lastMissingProperty = e
                }
            }
            assert lastMissingProperty
            throw lastMissingProperty
        }
        else {
            try {
                return invokeDelegateMethod(ctx, delegate, name, EMPTY_ARGS)
            }
            catch ( MissingMethodException e ) {
                throw new MissingPropertyException(name, delegate.getClass())
            }
            //
            // Decided not to support getters
            //
            //MetaProperty property = delegate.metaClass.getMetaProperty(name)
            //if ( property == null ) {
            //    try {
            //        return invokeDelegateMethod(delegate, name, EMPTY_ARGS, voidRetval)
            //    }
            //    catch ( MissingMethodException e ) {
            //        throw new MissingPropertyException(name, delegate.getClass())
            //    }
            //}
            //def getter = delegate.metaClass.getMetaMethod(MetaProperty.getGetterName(property.name, property.type))
            //def javaGetter = DslDelegates.findJavaMethod(getter)
            //if ( javaGetter == null || !javaGetter.getAnnotation(DSL) ) {
            //    throw new MissingPropertyException(name, delegate.getClass())
            //}
            //return getter.doMethodInvoke(delegate)
        }
    }

    static invokeDelegateMethod(DslContext ctx, delegate, String name, Object[] args) {
        if ( delegate instanceof Collection ) {
            assert delegate
            MissingMethodException lastMissingMethod = null
            for ( d in delegate ) {
                try {
                    return invokeDelegateMethod(ctx, d, name, args as Object[])
                }
                catch ( MissingMethodException e ) {
                    lastMissingMethod = e
                }
            }
            assert lastMissingMethod
            throw lastMissingMethod
        }
        else {
            Closure closure = null
            Object[] callArgs = args
            MetaMethod method = pickMethod(delegate, name, callArgs)
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
            WithBody withBody = javaMethod.getAnnotation(WithBody)
            if ( !withBody && closure ) {
                // no @WithBody, but we have a closure -> no match
                return tryFallback(delegate, name, args)
            }
            def retval = method.doMethodInvoke(delegate, callArgs)
            if ( withBody ) {
                if ( closure != null ) {
                    def del = new DslDelegate(ctx, retval)
                    closure = Groovy.prepare(closure, del, Closure.DELEGATE_FIRST)
                    ctx.delegateStack.push(del)
                    try {
                        if ( withBody.invoker() ) {
                            retval = delegate."${withBody.invoker()}"(closure)
                        }
                        else {
                            retval = closure.call()
                        }
                    }
                    finally {
                        ctx.delegateStack.pop()
                    }
                }
            }
            return retval
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
