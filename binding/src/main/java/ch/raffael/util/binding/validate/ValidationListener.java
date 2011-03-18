package ch.raffael.util.binding.validate;

import java.util.EventListener;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ValidationListener extends EventListener {

    void validationChanged(ValidationEvent event);

}
