package ch.raffael.util.binding;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.ObservableSupport;
import ch.raffael.util.binding.convert.Converter;
import ch.raffael.util.binding.validate.ValidationResult;
import ch.raffael.util.binding.validate.Validator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ConverterBinding<T, S> extends AbstractBinding<T> implements ChainedBinding<T, S>, ValidatingBinding<T> {

    private final ObservableSupport observableSupport = new ObservableSupport(this);
    private final PropertyChangeListener propertyChangeForwarder = new PropertyChangeListener() {
        @SuppressWarnings( { "unchecked" })
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( evt.getPropertyName().equals(PROPERTY_VALUE) ) {
                Object oldVal = converter.sourceToTarget((S)evt.getOldValue());
                Object newVal = converter.sourceToTarget((S)evt.getNewValue());
                observableSupport.firePropertyChange(PROPERTY_VALUE, oldVal, newVal);
            }
        }
    };
    private final Converter<S, T> converter;
    private Validator<T> validator;

    private Binding<S> source;

    public ConverterBinding(@NotNull Converter<S, T> converter) {
        this.converter = converter;
    }

    public ConverterBinding(Converter<S, T> converter, Binding<S> source) {
        this(converter);
        this.source = source;
        if ( source != null ) {
            source.addPropertyChangeListener(propertyChangeForwarder);
        }
    }


    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

    @Override
    public Binding<S> getSource() {
        return source;
    }

    @Override
    public void setSource(Binding<S> source) {
        if ( !Bindings.equal(this.source, source) ) {
            T oldValue = getValue();
            Binding<S> oldSource = this.source;
            if ( oldSource != null ) {
                oldSource.removePropertyChangeListener(propertyChangeForwarder);
            }
            this.source = source;
            if ( source != null ) {
                source.addPropertyChangeListener(propertyChangeForwarder);
            }
            observableSupport.firePropertyChange(PROPERTY_SOURCE, oldSource, source);
            observableSupport.firePropertyChange(PROPERTY_VALUE, oldValue, getValue());
        }
    }

    @Override
    public T getValue() {
        return converter.sourceToTarget(Bindings.getValue(source));
    }

    @Override
    public void setValue(T value) {
        Bindings.setValue(source, converter.targetToSource(value));
    }

    @Override
    public void validate(T value, ValidationResult result) {
        S convertedValue;
        try {
            convertedValue = converter.targetToSource(value);
        }
        catch ( InvalidValueException e ) {
            result.addError(e.getLocalizedMessage(), e);
            return;
        }
        if ( source instanceof ValidatingBinding ) {
            ((ValidatingBinding<S>)source).validate(convertedValue, result);
        }
    }

    public Validator<T> getValidator() {
        return validator;
    }

    public void setValidator(Validator<T> validator) {
        this.validator = validator;
    }

    public ConverterBinding validate(Validator<T> validator) {
        setValidator(validator);
        return this;
    }
}
