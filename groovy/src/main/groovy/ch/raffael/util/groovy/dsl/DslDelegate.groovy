package ch.raffael.util.groovy.dsl
/**
 * Internal wrapper for DSL delegates. It inspects the delegate for methods annotated with
 * @DSL.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslDelegate {

    final parent
    final String path
    final List delegates

    DslDelegate(delegate) {
        this.@parent = null
        this.@path = '/'
        this.@delegates = delegate instanceof Collection ? delegate as List : [ delegate ]
    }

    DslDelegate(DslDelegate parent, String method, delegate) {
        this.@parent = parent
        if ( parent.@path == '/' ) {
            this.@path = '/' + method
        }
        else {
            this.@path = parent.@path + '/' + method
        }
        this.@delegates = delegate instanceof Collection ? delegate as List : [ delegate ]
    }

    @Override
    String toString() {
        'DslDelegate{' + this.@path + '->' + this.@delegates + '}'
    }

    @Override
    def getProperty(String name) {
        DslInvoke.getDelegateProperty(this, name)
    }

    @Override
    void setProperty(String name, value) {
        // FIXME: See whether such a property could be read, throw MissingProperty otherwise
        throw new MissingPropertyException(name, super.getClass())
        // we could actually also look for a method $name(value); I think this makes no sense, though
    }

    @Override
    def invokeMethod(String name, args) {
        DslInvoke.invokeDelegateMethod(this, name, args as Object[])
    }

}
