package ch.raffael.util.groovy.dsl
/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Inherited
class Inherit {

    @Dsl
    def inheritThis(String str) {
        throw new IllegalStateException("Inherited: $str")
    }

    //@DSL @WithBody
    //def hello() {
    //    return new Hello(parent: this)
    //}

}
