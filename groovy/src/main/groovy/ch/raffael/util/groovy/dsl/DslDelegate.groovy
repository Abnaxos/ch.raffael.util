package ch.raffael.util.groovy.dsl

/**
 * Internal wrapper for DSL delegates. It inspects the delegate for methods annotated with
 * @DSL.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslDelegate {

    private final DslContext context
    final List delegates
    private boolean forceInvoke

    DslDelegate(DslContext context, delegate, boolean forceInvoke = false) {
        this.@context = context
        this.@delegates = delegate instanceof Collection ? delegate as List : [ delegate ]
        this.@forceInvoke = forceInvoke
    }

    @Override
    def getProperty(String name) {
        if ( this.@forceInvoke || this.@context.delegateStack && this.@context.delegateStack[-1] == this ) {
            return this.@context.getDelegateProperty(this, name)
        }
        else {
            throw new MissingPropertyException(name, this.@context.delegateStack[-1].getClass())
        }
    }

    @Override
    void setProperty(String name, value) {
        // FIXME: See whether such a property could be read, throw MissingProperty otherwise
        throw new ReadOnlyPropertyException(name, this.@context.delegateStack[-1].getClass())
        // we could actually also look for a method $name(value); I think this makes no sense, though
    }

    @Override
    def invokeMethod(String name, args) {
        if ( this.@forceInvoke || this.@context.delegateStack && this.@context.delegateStack[-1] == this ) {
            return this.@context.invokeDelegateMethod(this, name, args as Object[])
        }
        else {
            throw new MissingMethodException(name, this.@context.delegateStack[-1].getClass(), args)
        }
    }



    //@Override
    //def getProperty(String name) {
    //    try {
    //        return super.getProperty(name)
    //    }
    //    catch ( MissingPropertyException e ) {
    //        if ( context.delegateStack && context.delegateStack[-1] == this ) {
    //            return getDelegateProperty(delegate, name)
    //        }
    //        else {
    //            throw e
    //        }
    //    }
    //}

    //@Override
    //def invokeMethod(String name, argsObj) {
    //    try {
    //        return super.invokeMethod(name, argsObj)
    //    }
    //    catch ( MissingMethodException e ) {
    //        if ( context.delegateStack && context.delegateStack[-1] == this ) {
    //            return invokeDelegateMethod(delegate, name, argsObj as Object[])
    //        }
    //        else {
    //            throw e
    //        }
    //    }
    //}

}
