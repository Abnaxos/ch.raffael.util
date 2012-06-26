package ch.raffael.util.binding;


import spock.lang.Specification
import spock.util.mop.Use

import java.beans.PropertyChangeListener

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
@Use(PropertyChange)
class SimpleBindingSpec extends Specification {

    def "PropertyChange on value change"() {
      given:
        def binding = new SimpleBinding<String>()
        def listener = Mock(PropertyChangeListener)
        binding.addPropertyChangeListener(listener)

      when:
        binding.value = "Foo"
        binding.value = "Bar"

      then:
        1 * listener.propertyChange({ evt -> evt.matches("value", null, "Foo")})
        1 * listener.propertyChange({ evt -> evt.matches("value", "Foo", "Bar")})
        0 * _._
    }

    def "No PropertyChange when value is set to equal value"() {
      given:
        def binding = new SimpleBinding<String>("Foo")
        def listener = Mock(PropertyChangeListener)
        binding.addPropertyChangeListener(listener)

      when:
        binding.value = "Foo"

      then:
        0 * _._
    }

}
