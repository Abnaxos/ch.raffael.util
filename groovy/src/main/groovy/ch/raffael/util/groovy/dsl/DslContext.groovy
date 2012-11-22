package ch.raffael.util.groovy.dsl
/**
 * Internal DSL context.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class DslContext {

    final List<DslDelegate> delegateStack = []

    private Map<String, Object> properties = [:]

    DslContext() {
    }

}
