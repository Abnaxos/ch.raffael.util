package ch.raffael.util.groovy.dsl

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class List {

    final list = []

    @Dsl
    void add(Object elem) {
        list << elem
    }

}
