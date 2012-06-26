package ch.raffael.util.i18n.impl

import ch.raffael.util.i18n.ResourceBundle

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ResA extends ResourceBundle {

    String inherited();

    String ambiguousWithForward();
    String ambiguousWithImpl();
    String ambiguousWithError();

}