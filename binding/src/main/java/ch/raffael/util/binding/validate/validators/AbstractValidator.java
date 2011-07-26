package ch.raffael.util.binding.validate.validators;

import ch.raffael.util.binding.PresentationModelMember;
import ch.raffael.util.binding.validate.AndValidator;
import ch.raffael.util.binding.validate.OrValidator;
import ch.raffael.util.binding.validate.Validator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractValidator<T> extends PresentationModelMember implements Validator<T> {

    public AndValidator<T> and(Validator<T> and) {
        return new AndValidator<T>(this).add(and);
    }

    public OrValidator<T> or(Validator<T> or) {
        return new OrValidator<T>(this).add(or);
    }

    public OrValidator<T> or(Validator<T> or, boolean keepWarnings) {
        return new OrValidator<T>(or, keepWarnings);
    }

}
