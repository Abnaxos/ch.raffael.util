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

package ch.raffael.util.swing.util;

import java.awt.GridLayout;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ViewMetalTheme {

    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        GridLayout layout = new GridLayout(0, 2);
        JPanel display = new JPanel(layout);
        UIManager.setLookAndFeel(new MetalLookAndFeel());
        MetalTheme theme = MetalLookAndFeel.getCurrentTheme();
        frame.setTitle("Theme: " + theme.getClass().getName());
        PropertyDescriptor[] props = Introspector.getBeanInfo(theme.getClass()).getPropertyDescriptors();
        Arrays.sort(props, new Comparator<PropertyDescriptor>() {
            @Override
            public int compare(PropertyDescriptor a, PropertyDescriptor b) {
                return a.getName().compareTo(b.getName());
            }
        });
        int rows = 1;
        for ( PropertyDescriptor p : props ) {
            if ( !ColorUIResource.class.isAssignableFrom(p.getPropertyType()) ) {
                continue;
            }
            Method reader = p.getReadMethod();
            if ( reader == null ) {
                continue;
            }
            layout.setRows(rows++);
            ColorUIResource color = (ColorUIResource)reader.invoke(theme);
            JLabel label = new JLabel(p.getName());
            label.setForeground(color);
            display.add(label);
            label = new JLabel(p.getName());
            label.setBackground(color);
            label.setOpaque(true);
            display.add(label);
        }
        frame.setContentPane(new JScrollPane(display));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}
