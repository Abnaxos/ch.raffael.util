package ch.raffael.util.groovy.dsl

import ch.raffael.util.groovy.Groovy

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslContext {

    final List<DslDelegate> delegateStack = []

    private Map<String, Object> properties = [:]

    DslContext() {
    }

    def eval(delegate, Closure block, Object... args) {
        def dslDelegate = new DslDelegate(this, delegate)
        delegateStack.push(dslDelegate)
        try {
            return Groovy.prepare(block, dslDelegate, Closure.DELEGATE_FIRST).call(args)
        }
        finally {
            delegateStack.pop()
        }
    }

}
