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

import javax.swing.JLabel;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.binding.Binding;
import ch.raffael.util.binding.PresentationModel;
import ch.raffael.util.binding.util.BindingTracker;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class LabelComponentAdapter {

    private JLabel component;
    private PresentationModel model;
    private final BindingTracker<String> binding = new BindingTracker<String>() {
        @Override
        public void update(String newValue) {
            if ( component != null ) {
                component.setText(newValue == null ? "" : newValue);
            }
        }
    };

    public LabelComponentAdapter install(@NotNull JLabel component, @NotNull PresentationModel model, @NotNull Binding<String> binding) {
        setComponent(component);
        setModel(model);
        setBinding(binding);
        return this;
    }

    public JLabel getComponent() {
        return component;
    }

    public void setComponent(JLabel component) {
        this.component = component;
        if ( component != null ) {
            binding.update();
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

    public void setBinding(Binding<String> binding) {
        this.binding.setBinding(binding);
    }

}
