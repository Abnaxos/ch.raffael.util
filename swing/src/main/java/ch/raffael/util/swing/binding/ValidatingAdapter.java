package ch.raffael.util.swing.binding;

import ch.raffael.util.beans.EventEmitter;
import ch.raffael.util.binding.Adapter;
import ch.raffael.util.binding.PresentationModelMember;
import ch.raffael.util.binding.validate.DefaultValidationResult;
import ch.raffael.util.binding.validate.ValidationListener;
import ch.raffael.util.binding.validate.Validator;
import ch.raffael.util.binding.validate.Validators;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class ValidatingAdapter<B, T> extends PresentationModelMember implements Adapter<B, T> {


    private EventEmitter<ValidationListener> validationEvents = EventEmitter.newEmitter(ValidationListener.class);
    private Validator<B> validator;
    protected DefaultValidationResult validationStatus;

    @Override
    public void addValidationListener(ValidationListener listener) {
        validationEvents.addListener(listener);
    }

    @Override
    public void removeValidationListener(ValidationListener listener) {
        validationEvents.removeListener(listener);
    }

    @Override
    public void validate() {
        DefaultValidationResult oldStatus = validationStatus;
        validationStatus = new DefaultValidationResult();
        if ( validator != null ) {
            validator.validate(currentValue(), validationStatus);
        }
        if ( !validationEvents.isEmpty() ) {
            validationStatus.fireEvent(validationEvents.emitter(), oldStatus, this, getTarget());
        }
    }

    public DefaultValidationResult getValidationStatus() {
        return validationStatus;
    }

    public Validator<B> getValidator() {
        return validator;
    }

    public void setValidator(Validator<B> validator) {
        this.validator = validator;
    }

    public ValidatingAdapter withValidator(Validator<B> validator) {
        setValidator(validator);
        return this;
    }

    public ValidatingAdapter withValidators(Validator<B>... validators) {
        setValidator(Validators.and(validators));
        return this;
    }

    protected abstract B currentValue();

}
