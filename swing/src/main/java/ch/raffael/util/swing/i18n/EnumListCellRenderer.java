/*
 * Copyright 2010 Raffael Herzog
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

package ch.raffael.util.swing.i18n;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;

import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class EnumListCellRenderer extends DefaultListCellRenderer {

    private final Class<? extends Enum> enumClass;
    private final ResourceBundle.Resource<String> resource;

    @SuppressWarnings({ "unchecked" })
    public EnumListCellRenderer(Class<? extends Enum> enumClass, ResourceBundle.Resource<String> resource) {
        this.enumClass = enumClass;
        this.resource = resource;

    }

    @SuppressWarnings({ "unchecked" })
    public EnumListCellRenderer(Class<? extends Enum> enumClass, ResourceBundle bundle, String methodName) {
        this(enumClass, bundle.meta().resource(String.class, methodName, enumClass));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        setText(resource.get(value));
        return this;
    }

    public void install(JComboBox comboBox) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for ( Enum e : enumClass.getEnumConstants() ) {
            model.addElement(e);
        }
        comboBox.setModel(model);
        comboBox.setRenderer(this);
    }

}
