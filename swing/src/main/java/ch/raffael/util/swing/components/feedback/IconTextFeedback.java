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

package ch.raffael.util.swing.components.feedback;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class IconTextFeedback extends JPanel implements Feedback {

    private FeedbackPanel.Placement placement;
    protected final JLabel icon = new JLabel();
    protected final JLabel text = new JLabel();
    private boolean mouseOver;
    private boolean focus;

    private final MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            mouseOver = true;
            updateTextVisibility();
        }
        @Override
        public void mouseExited(MouseEvent e) {
            mouseOver = false;
            updateTextVisibility();
        }
    };
    private FocusListener focusListener = null;

    public IconTextFeedback(Icon icon, String text) {
        this(icon, text, null);
    }

    public IconTextFeedback(Icon icon, String text, FeedbackPanel.Placement placement) {
        super();
        setLayout(new IconTextLayout());
        initComponents();
        setPlacement(placement);
        setIcon(icon);
        setText(text);
    }

    private void initComponents() {
        setOpaque(false);
        setBackground(Color.YELLOW);
        icon.setOpaque(false);
        text.setVisible(false);
        text.setOpaque(true);
        setBackground(Color.YELLOW);
        icon.addMouseListener(mouseListener);
        text.addMouseListener(mouseListener);
        add(text);
        add(icon);
        //add(icon, BorderLayout.NORTH);
    }

    public void attach(Component component) {
        focus = component.hasFocus();
        updateTextVisibility();
        if ( focusListener == null ) {
            focusListener = new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    focus = true;
                    updateTextVisibility();
                }
                @Override
                public void focusLost(FocusEvent e) {
                    focus = false;
                    updateTextVisibility();
                }
            };
        }
        component.addFocusListener(focusListener);
        component.addMouseListener(mouseListener);
    }

    @Override
    public void detach(Component component) {
        component.removeMouseListener(mouseListener);
        component.removeFocusListener(focusListener);
    }

    private void updateTextVisibility() {
        text.setVisible(mouseOver || focus);
    }

    @NotNull
    public FeedbackPanel.Placement getPlacement() {
        return placement;
    }

    public void setPlacement(FeedbackPanel.Placement placement) {
        if ( placement == null ) {
            placement = FeedbackPanel.Placement.BOTTOM_RIGHT;
        }
        this.placement = placement;
    }

    @Override
    public void setBackground(Color bg) {
        if ( text != null ) {
            text.setBackground(bg);
            text.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bg.darker()),
                    BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        }
    }

    public Icon getIcon() {
        return icon.getIcon();
    }

    public void setIcon(Icon icon) {
        this.icon.setIcon(icon);
    }

    public String getText() {
        return text.getText();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    @Override
    public void prepare(@NotNull FeedbackPanel.Placement placement) {
        setPlacement(placement);
    }

    @NotNull
    @Override
    public Point translate(@NotNull Point reference, @NotNull FeedbackPanel.Placement placement) {
        if ( getIcon() != null ) {
            placement.translate(reference, -getIcon().getIconWidth() / 2, getIcon().getIconHeight() / 2);
        }
        return reference;
    }

    private class IconTextLayout implements LayoutManager2 {
        @Override
        public Dimension maximumLayoutSize(Container parent) {
            return getPreferredSize();
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Insets insets = parent.getInsets();
            Dimension pref = icon.getPreferredSize();
            if ( text.isVisible() ) {
                Dimension textPref = text.getPreferredSize();
                pref.height += textPref.height;
                textPref.width += pref.width / 2;
                if ( textPref.width > pref.width ) {
                    pref.width = textPref.width;
                }
            }
            pref.width += insets.left + insets.right;
            pref.height += insets.top + insets.bottom;
            return pref;
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return preferredLayoutSize(parent);
        }

        @Override
        public void layoutContainer(Container parent) {
            Insets insets = parent.getInsets();
            Rectangle bounds = parent.getBounds();
            icon.setSize(icon.getPreferredSize());
            text.setSize(text.getPreferredSize());
            icon.setLocation(placement.isLeft() ? insets.left : bounds.width - icon.getWidth() - insets.right,
                             (placement.isTop() ? text.getHeight() : 0) + insets.top);
            text.setLocation(placement.isLeft() ? insets.left + icon.getWidth() / 2 : bounds.width - text.getWidth() - icon.getWidth() / 2 - insets.right,
                             placement.isTop() ? 0 : icon.getHeight());
        }

        @Override
        public void addLayoutComponent(Component comp, Object constraints) {}
        @Override
        public void removeLayoutComponent(Component comp) {}
        @Override
        public float getLayoutAlignmentX(Container target) {return 0f;}
        @Override
        public float getLayoutAlignmentY(Container target) {return 0f;}
        @Override
        public void invalidateLayout(Container target) {}
        @Override
        public void addLayoutComponent(String name, Component comp) {}
    }

}
