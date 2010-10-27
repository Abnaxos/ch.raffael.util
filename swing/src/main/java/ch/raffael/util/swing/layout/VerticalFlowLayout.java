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

package ch.raffael.util.swing.layout;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;

import ch.raffael.util.common.UnreachableCodeException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class VerticalFlowLayout implements LayoutManager2 {

    private final Map<Component, Alignment> components = new HashMap<Component, Alignment>();

    private Alignment defaultAlignment;
    private int gap;


    public VerticalFlowLayout() {
        this(Alignment.LEADING, 0);
    }

    public VerticalFlowLayout(Alignment defaultAlignment) {
        this(defaultAlignment, 0);
    }

    public VerticalFlowLayout(int gap) {
        this(Alignment.LEADING, gap);
    }

    public VerticalFlowLayout(Alignment defaultAlignment, int gap) {
        this.defaultAlignment = defaultAlignment;
        this.gap = gap;
    }

    public Alignment getDefaultAlignment() {
        return defaultAlignment;
    }

    public void setDefaultAlignment(Alignment defaultAlignment) {
        this.defaultAlignment = defaultAlignment;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        components.put(comp, (Alignment)constraints);
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        // FIXME: implement this?
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return .5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return .5f;
    }

    @Override
    public void invalidateLayout(Container target) {
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        addLayoutComponent(comp, null);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        components.remove(comp);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        if ( components.isEmpty() ) {
            return new Dimension(0, 0);
        }
        Dimension size = new Dimension(0, (components.size() - 1) * gap);
        for ( int i=0; i<parent.getComponentCount(); i++ ) {
            Dimension s = parent.getComponent(i).getPreferredSize();
            size.width = Math.max(s.width, size.width);
            size.height += s.height;
        }
        applyInsets(parent, size);
        return size;
    }

    private void applyInsets(Container parent, Dimension size) {
        Insets insets = parent.getInsets();
        size.width += insets.left + insets.right;
        size.height += insets.top + insets.bottom;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        if ( components.isEmpty() ) {
            return new Dimension(0, 0);
        }
        Dimension size = new Dimension(0, (components.size() - 1) * gap);
        for ( int i=0; i<parent.getComponentCount(); i++ ) {
            Component comp = parent.getComponent(i);
            size.width = Math.max(comp.getMinimumSize().width, size.width);
            size.height += comp.getPreferredSize().height;
        }
        applyInsets(parent, size);
        return size;
    }

    @SuppressWarnings({ "EnumSwitchStatementWhichMissesCases" })
    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int y = insets.top;
        for ( int i = 0; i < parent.getComponentCount(); i++ ) {
            Component comp = parent.getComponent(i);
            Dimension prefSize = comp.getPreferredSize();
            int x, w;
            if ( parent.getWidth() - insets.left - insets.right <= prefSize.width ) {
                x = insets.left;
                w = parent.getWidth() - insets.left - insets.right;
            }
            else {
                Alignment alignment = components.get(comp);
                if ( alignment == null ) {
                    alignment = defaultAlignment;
                }
                if ( alignment == Alignment.LEADING ) {
                    if ( parent.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT ) {
                        alignment = Alignment.RIGHT;
                    }
                    else {
                        alignment = Alignment.LEFT;
                    }
                }
                else if ( alignment == Alignment.TRAILING ) {
                    if ( parent.getComponentOrientation() == ComponentOrientation.RIGHT_TO_LEFT ) {
                        alignment = Alignment.LEFT;
                    }
                    else {
                        alignment = Alignment.RIGHT;
                    }
                }
                switch ( alignment ) {
                    case LEFT:
                        x = insets.left;
                        w = prefSize.width;
                        break;
                    case CENTER:
                        x = (parent.getWidth() - insets.right - insets.left) / 2 - prefSize.width / 2 + insets.left;
                        w = prefSize.width;
                        break;
                    case RIGHT:
                        x = parent.getWidth() - prefSize.width - insets.right;
                        w = prefSize.width;
                        break;
                    case FILL:
                        x = insets.left;
                        w = parent.getWidth() - insets.left - insets.right;
                        break;
                    default:
                        throw new UnreachableCodeException();
                }
            }
            comp.setBounds(x, y, w, prefSize.height);
            y += prefSize.height + gap;
        }
    }

    public static enum Alignment {
        LEFT, CENTER, RIGHT, LEADING, TRAILING, FILL
    }

}
