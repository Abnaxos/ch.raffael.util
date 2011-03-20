package ch.raffael.util.swing.binding;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.raffael.util.binding.Adapter;
import ch.raffael.util.binding.Binding;
import ch.raffael.util.binding.Bindings;
import ch.raffael.util.binding.validate.ValidationListener;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class EnabledAdapter implements Adapter<Boolean, Component> {

    private Binding<Boolean> binding;
    private Component target;

    private final PropertyChangeListener bindingListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( evt.getPropertyName().equals(Binding.PROPERTY_VALUE) ) {
                update();
            }
        }
    };

    public EnabledAdapter() {
    }

    public EnabledAdapter(Binding<Boolean> binding) {
        setBinding(binding);
    }

    public EnabledAdapter(Component target) {
        setTarget(target);
    }

    public EnabledAdapter(Binding<Boolean> binding, Component target) {
        setBinding(binding);
        setTarget(target);
    }

    public Binding<Boolean> getBinding() {
        return binding;
    }

    public void setBinding(Binding<Boolean> binding) {
        if ( this.binding != null ) {
            this.binding.addPropertyChangeListener(bindingListener);
        }
        this.binding = binding;
        if ( this.binding != null ) {
            this.binding.addPropertyChangeListener(bindingListener);
        }
        update();
    }

    public Component getTarget() {
        return target;
    }

    public void setTarget(Component target) {
        this.target = target;
        update();
    }

    @Override
    public void addValidationListener(ValidationListener listener) {
    }

    @Override
    public void removeValidationListener(ValidationListener listener) {
    }

    @Override
    public void validate() {
    }

    private void update() {
        if ( target != null ) {
            Boolean enabled = Bindings.getValue(binding);
            target.setEnabled(enabled == null ? false : enabled);
        }
    }

}
