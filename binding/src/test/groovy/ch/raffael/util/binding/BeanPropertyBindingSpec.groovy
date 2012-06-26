package ch.raffael.util.binding;


import spock.lang.Specification
import spock.util.mop.Use

import java.beans.PropertyChangeListener

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
@Use(PropertyChange)
class BeanPropertyBindingSpec extends Specification {

    def "PropertyChange when a property changes"() {
      given:
        def bean = new MyBean(anInt: 42)
        def binding = new SimpleBinding<MyBean>(bean).property("anInt")
        def listener = Mock(PropertyChangeListener)
        binding.addPropertyChangeListener(listener)

      when:
        bean.anInt = 23

      then:
        1 * listener.propertyChange({ evt -> evt.matches("value", 42, 23) })
        0 * _._
    }

    def "PropertyChange when the underlying bean changes"() {
      given:
        def bean = new MyBean(anInt: 42)
        def newBean = new MyBean(anInt: 23)
        def binding = new SimpleBinding<MyBean>(bean)
        def listener = Mock(PropertyChangeListener)
        binding.addPropertyChangeListener(listener)
        binding.property("anInt").addPropertyChangeListener(listener)

      when:
        binding.value = newBean

      then:
        1 * listener.propertyChange({ evt -> evt.matches("value", bean, newBean) })
        1 * listener.propertyChange({ evt -> evt.matches("value", 42, 23) })
        0 * _._
    }

    def "PropertyChange when the property name changes"() {
      given:
        def bean = new MyBean(anInt: 42, aString: "Foo")
        def binding = new SimpleBinding<MyBean>(bean).property("anInt")
        def listener = Mock(PropertyChangeListener)
        binding.addPropertyChangeListener(listener)

      when:
        binding.propertyName = "aString"

      then:
        1 * listener.propertyChange({ evt -> evt.matches("value", 42, "Foo") })
        0 * _._
    }

}
