package ch.raffael.util.binding.util;

import ch.raffael.util.binding.validate.AndValidator;
import ch.raffael.util.binding.validate.ValidationResult;
import ch.raffael.util.binding.validate.Validator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ValidatorHolder<T> implements Validator<T> {

    private Validator<T> validator;

    @Override
    public void validate(T value, ValidationResult result) {
        if ( validator != null ) {
            validator.validate(value, result);
        }
    }

    public void checkValue(T value) {
        validate(value, ValidationResult.THROW);
    }

    public Validator<T> getValidator() {
        return validator;
    }

    public void setValidator(Validator<T> validator) {
        this.validator = validator;
    }

    public ValidatorHolder<T> append(Validator<T> validator) {
        if ( this.validator == null ) {
            this.validator = validator;
        }
        else if ( this.validator instanceof AndValidator ) {
            ((AndValidator<T>)this.validator).add(validator);
        }
        else {
            this.validator = new AndValidator<T>(this.validator).add(validator);
        }
        return this;
    }

}
