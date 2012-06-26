package ch.raffael.util.binding;


import spock.lang.Specification
import spock.util.mop.Use

import java.beans.PropertyChangeListener

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@SuppressWarnings("GroovyPointlessArithmetic")
@Use(PropertyChange)
class BufferedBindingSpec extends Specification {

    private SimpleBinding<String> binding = new SimpleBinding<String>()
    private BufferedBinding<String> buffer = new BufferedBinding<String>(binding)
    private PropertyChangeListener listener = { ->
        def l = Mock(PropertyChangeListener)
        binding.addPropertyChangeListener(l)
        buffer.addPropertyChangeListener(l)
        return l
    }()

    def "Events on bottom-up flush"() {
      when:
        binding.value = "Foo"
        buffer.flush()

      then:
        1 * listener.propertyChange({ evt -> evt.matches("value", null, "Foo", binding) })
        1 * listener.propertyChange({ evt -> evt.matches("buffering", false, true) })

      then:
        1 * listener.propertyChange({ evt -> evt.matches("value", null, "Foo", buffer) })
        1 * listener.propertyChange({ evt -> evt.matches("buffering", true, false) })

        buffer.value == binding.value
        0 * _._
    }

    def "Events on top-down flush"() {
      when:
        buffer.value = "Foo"
        buffer.flush()

      then:
        1 * listener.propertyChange({ evt -> evt.matches("value", null, "Foo", buffer) })
        1 * listener.propertyChange({ evt -> evt.matches("buffering", false, true) })

      then:
        1 * listener.propertyChange({ evt -> evt.matches("value", "Foo", null, buffer )})
        1 * listener.propertyChange({ evt -> evt.matches("buffering", true, false )})

        buffer.value == null
        binding.value == null
        0 * _._
    }

    def "Events on commit"() {
      when:
        buffer.value = "Foo"
        buffer.commit()

      then:
        1 * listener.propertyChange({ evt -> evt.matches("buffering", false, true) })
        // FIXME: WTF ... AbstractBufferedBinding#reset() clearly forces a PropertyChangeEvent; but WHY?
        2 * listener.propertyChange({ evt -> evt.matches("value", null, "Foo", buffer) })
        1 * listener.propertyChange({ evt -> evt.matches("buffering", true, false) })
        1 * listener.propertyChange({ evt -> evt.matches("value", null, "Foo", binding) })

        buffer.value == "Foo"
        binding.value == "Foo"
        0 * _._
    }

    def "Events when the underlying binding changes while not buffering"() {
      given:
        def newBinding = new SimpleBinding<String>("Bar")
        binding.value = "Foo"
        buffer.flush()

      when:
        buffer.source = newBinding

      then:
        1 * listener.propertyChange({ evt -> evt.matches("source", binding, newBinding) })
        1 * listener.propertyChange({ evt -> evt.matches("value", "Foo", "Bar", buffer) })

        binding.value == "Foo"
        newBinding.value == "Bar"
        buffer.value == "Bar"
        buffer.buffering == false
        0 * _._
    }

    def "Events when underlying binding changes while buffering (buffer flushes)"() {
      given:
        def newBinding = new SimpleBinding<String>("Bar")
        binding.value = "Foo"
        buffer.flush()

      when:
        buffer.value = "Foobar"
        buffer.source = newBinding

      then:
        1 * listener.propertyChange({ evt -> evt.matches("value", "Foo", "Foobar", buffer) })
        1 * listener.propertyChange({ evt -> evt.matches("buffering", false, true) })

      then:
        1 * listener.propertyChange({ evt -> evt.matches("source", binding, newBinding) })
        1 * listener.propertyChange({ evt -> evt.matches("buffering", true, false) })
        1 * listener.propertyChange({ evt -> evt.matches("value", "Foobar", "Bar", buffer) })

        !buffer.buffering
        buffer.value == "Bar"
        0 * _._
    }

}
