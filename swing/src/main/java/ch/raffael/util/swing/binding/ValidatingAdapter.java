package ch.raffael.util.swing.binding;

import ch.raffael.util.beans.EventEmitter;
import ch.raffael.util.binding.Adapter;
import ch.raffael.util.binding.PresentationModelMember;
import ch.raffael.util.binding.ValidatingBinding;
import ch.raffael.util.binding.util.ValidatorHolder;
import ch.raffael.util.binding.validate.DefaultValidationResult;
import ch.raffael.util.binding.validate.ValidationListener;
import ch.raffael.util.binding.validate.Validator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class ValidatingAdapter<B, T> extends PresentationModelMember implements Adapter<B, T> {


    private EventEmitter<ValidationListener> validationEvents = EventEmitter.newEmitter(ValidationListener.class);
    private final ValidatorHolder<B> validator = new ValidatorHolder<B>();
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
        B value = currentValue();
        if ( validator != null ) {
            validator.validate(value, validationStatus);
        }
        if ( getBinding() instanceof ValidatingBinding ) {
            ((ValidatingBinding<B>)getBinding()).validate(value, validationStatus);
        }
        if ( !validationEvents.isEmpty() ) {
            validationStatus.fireEvent(validationEvents.emitter(), oldStatus, this, getTarget());
        }
    }

    public DefaultValidationResult getValidationStatus() {
        return validationStatus;
    }

    public Validator<B> getValidator() {
        return validator.getValidator();
    }

    public void setValidator(Validator<B> validator) {
        this.validator.setValidator(validator);
    }

    public ValidatingAdapter validator(Validator<B> validator) {
        this.validator.append(validator);
        return this;
    }

    protected abstract B currentValue();

}
