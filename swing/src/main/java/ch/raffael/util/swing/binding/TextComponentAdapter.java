/*
 * Copyright 2011 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.raffael.util.swing.binding;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.binding.Adapter;
import ch.raffael.util.binding.Binding;
import ch.raffael.util.binding.PresentationModel;
import ch.raffael.util.binding.util.BindingTracker;
import ch.raffael.util.binding.validate.ValidationResult;
import ch.raffael.util.binding.validate.Validator;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TextComponentAdapter implements Adapter<String, JTextComponent> {

    private UpdateStrategy updateStrategy = UpdateStrategy.IMMEDIATE;
    private JTextComponent component;
    private PresentationModel model;
    private final BindingTracker<String> binding = new BindingTracker<String>() {
        @Override
        public void update(String newValue) {
            if ( component != null ) {
                component.setText(newValue == null ? "" : newValue);
            }
        }
    };
    private Validator<String> validator;

    private ImmediateUpdateHandler immediateUpdateHandler = null;
    private FocusUpdateHandler focusUpdateHandler = null;

    public TextComponentAdapter install(@NotNull JTextComponent component, @NotNull PresentationModel model, @NotNull Binding<String> binding) {
        setComponent(component);
        setModel(model);
        setBinding(binding);
        return this;
    }

    public JTextComponent getComponent() {
        return component;
    }

    @Override
    public JTextComponent getTarget() {
        // FIXME: Not implemented
        return null;
    }

    public void setComponent(JTextComponent component) {
        if ( this.component != null ) {
            switch ( updateStrategy ) {
                case IMMEDIATE:
                    immediateUpdateHandler().uninstall();
                    break;
                case FOCUS:
                    focusUpdateHandler().uninstall();
                    break;
            }
        }
        this.component = component;
        if ( this.component != null ) {
            binding.update();
            switch ( updateStrategy ) {
                case IMMEDIATE:
                    immediateUpdateHandler().install();
                    break;
                case FOCUS:
                    focusUpdateHandler().install();
                    break;
            }
        }
    }

    public PresentationModel getModel() {
        return model;
    }

    public void setModel(PresentationModel model) {
        this.model = model;
    }

    public Binding<String> getBinding() {
        return binding.getBinding();
    }

    @Override
    public void validate(ValidationResult result) {
        if ( validator != null ) {
            validator.validate(component==null?null:component.getText(), result);
        }
    }

    public void setBinding(Binding<String> binding) {
        this.binding.setBinding(binding);
    }

    private ImmediateUpdateHandler immediateUpdateHandler() {
        if ( immediateUpdateHandler == null ) {
            immediateUpdateHandler = new ImmediateUpdateHandler();
        }
        return immediateUpdateHandler;
    }

    private FocusUpdateHandler focusUpdateHandler() {
        if ( focusUpdateHandler == null ) {
            focusUpdateHandler = new FocusUpdateHandler();
        }
        return focusUpdateHandler;
    }
    
    private class ImmediateUpdateHandler implements PropertyChangeListener, DocumentListener {

        private void install() {
            if ( component.getDocument() != null ) {
                component.getDocument().addDocumentListener(this);
            }
            component.addPropertyChangeListener(this);
        }

        private void uninstall() {
            component.removePropertyChangeListener(this);
            if ( component.getDocument() != null ) {
                component.getDocument().removeDocumentListener(this);
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ( evt.getSource() == component && evt.getPropertyName().equals("document") ) {
                if ( evt.getOldValue() != null ) {
                    ((Document)evt.getOldValue()).removeDocumentListener(this);
                }
                if ( evt.getNewValue() != null ) {
                    ((Document)evt.getNewValue()).removeDocumentListener(this);
                }
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            documentUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            documentUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            documentUpdate();
        }

        private void documentUpdate() {
            binding.set(component.getText());
        }
    }

    private class FocusUpdateHandler implements FocusListener {

        private void install() {
            if ( component != null ) {
                component.addFocusListener(this);
            }
        }

        private void uninstall() {
            if ( component != null ) {
                component.removeFocusListener(this);
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
        }

        @Override
        public void focusLost(FocusEvent e) {
            binding.set(component.getText());
        }
    }

}
