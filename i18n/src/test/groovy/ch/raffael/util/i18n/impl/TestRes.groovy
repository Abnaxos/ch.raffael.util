package ch.raffael.util.i18n.impl

import ch.raffael.util.i18n.Forward
import ch.raffael.util.i18n.Selector

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface TestRes extends ResA, ResB {

    @Forward(bundle = ResB.class)
    String ambiguousWithForward();

    String mine();
    String parameter(String name);
    String selector(@Selector FooBar sel);

    @Forward(bundle = ResC.class)
    String forwarded();

}
