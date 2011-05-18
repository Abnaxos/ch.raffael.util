package ch.raffael.util.binding;

import ch.raffael.util.binding.validate.ValidationResult;
import ch.raffael.util.binding.validate.Validator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ValidatingBindingSupport<T> implements Validator<T> {

    private Validator<T> validator;

    @Override
    public void validate(T value, ValidationResult result) {
        if ( validator != null ) {
            validator.validate(value, result);
        }
    }



}
