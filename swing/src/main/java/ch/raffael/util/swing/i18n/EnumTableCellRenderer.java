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

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class EnumTableCellRenderer<T extends Enum> extends DefaultTableCellRenderer {

    private final Class<? extends Enum> enumClass;
    private final EnumToStringConverter<T> converter;

    public EnumTableCellRenderer(Class<T> enumClass, ResourceBundle.Resource<String> resource) {
        this.enumClass = enumClass;
        this.converter = new EnumToStringConverter<T>(enumClass, resource);

    }

    public EnumTableCellRenderer(Class<T> enumClass, ResourceBundle bundle, String methodName) {
        this.enumClass = enumClass;
        this.converter = new EnumToStringConverter<T>(enumClass, bundle, methodName);
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setText(converter.asString((T)value));
        return this;
    }
}
