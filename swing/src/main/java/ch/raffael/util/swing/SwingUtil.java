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

package ch.raffael.util.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;

import com.jidesoft.plaf.LookAndFeelFactory;

import ch.raffael.util.common.logging.LogUtil;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class SwingUtil {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private static final FocusListener SELECT_ALL_FOCUS_LISTENER = new FocusListener() {
        @Override
        public void focusGained(FocusEvent e) {
            ((JTextComponent)e.getSource()).selectAll();
        }

        @Override
        public void focusLost(FocusEvent e) {
        }
    };

    public static int spacing = 5;
    private static boolean isJideAvailable = (SwingUtil.class.getClassLoader().getResource("com/jidesoft/swing/JideButton.class") != null);

    private SwingUtil() {
    }

    public static int getSpacing() {
        return spacing;
    }

    public static void setSpacing(int spacing) {
        SwingUtil.spacing = spacing;
    }

    public static Border spacingBorder() {
        return BorderFactory.createEmptyBorder(spacing, spacing, spacing, spacing);
    }

    public static Window findWindow(Component component) {
        while ( component != null && !(component instanceof Window) ) {
            component = getParent(component);
        }
        return (Window)component;
    }

    public static void center(Component component, Component parent) {
        component.setLocation(parent.getX() + parent.getWidth() / 2 - component.getWidth() / 2,
                              parent.getY() + parent.getHeight() / 2 - component.getWidth() / 2);
    }

    public static boolean invoke(Action action, Object source) {
        if ( action == null || !action.isEnabled() ) {
            return false;
        }
        ActionEvent evt = new ActionEvent(source, ActionEvent.ACTION_PERFORMED, (String)action.getValue(Action.ACTION_COMMAND_KEY), 0);
        action.actionPerformed(evt);
        return true;
    }

    public static JRootPane findRootPane(Component component) {
        while ( component != null ) {
            if ( component instanceof JRootPane ) {
                return (JRootPane)component;
            }
            component = getParent(component);
        }
        return null;
    }

    private static Component getParent(Component component) {
        if ( component instanceof JPopupMenu ) {
            component = ((JPopupMenu)component).getInvoker();
        }
        else {
            component = component.getParent();
        }
        return component;
    }

    public static int getMWidth(Component component, int count) {
        return getFontMetrics(component).charWidth('m') * count;
    }

    public static int getLineHeight(Component component, int count) {
        return getFontMetrics(component).getHeight() * count;
    }

    public static FontMetrics getFontMetrics(Component component) {
        Font font = component.getFont();
        if ( font == null ) {
            font = Font.getFont(Font.DIALOG);
        }
        return component.getFontMetrics(font);
    }

    public static void selectAllOnFocus(JTextComponent textComponent) {
        textComponent.addFocusListener(SELECT_ALL_FOCUS_LISTENER);
    }

    public static void fitRectangle(Rectangle rect, Rectangle target) {
        if ( rect.width > target.width ) {
            rect.width = target.width;
        }
        if ( rect.height > target.height ) {
            rect.height = target.height;
        }
        if ( rect.x < target.x ) {
            rect.x = target.x;
        }
        else if ( rect.x + rect.width > target.x + target.width ) {
            rect.x = target.x + target.width - rect.width;
        }
        if ( rect.y < target.y ) {
            rect.y = target.y;
        }
        else if ( rect.y + rect.height > target.y + target.height ) {
            rect.y = target.y + target.height - rect.height;
        }
    }

    public static boolean isJideAvailable() {
        return isJideAvailable;
    }

    public static void setupMetalLookAndFeel() {
        try {
            // set color theme here
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        }
        catch ( UnsupportedLookAndFeelException e ) {
            log.warn("Error installing Look and Feel", e);
        }
        UIManager.put("swing.boldMetal", false);
        if ( isJideAvailable() ) {
            LookAndFeelFactory.installJideExtension();
        }
    }

}