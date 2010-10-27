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

package ch.raffael.util.swing.components.sideoanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jidesoft.swing.JideButton;
import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.EventEmitter;
import ch.raffael.util.i18n.I18N;
import ch.raffael.util.swing.SwingUtil;
import ch.raffael.util.swing.actions.ActionGroupBuilder;
import ch.raffael.util.swing.actions.CommonAction;
import ch.raffael.util.swing.actions.CommonActionGroup;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class SidePanel extends JPanel {

    private final SidePanelResources res = getResources();

    private final EventEmitter<ChangeListener> collapseEvents = EventEmitter.newEmitter(ChangeListener.class);

    private final JideButton collapseButton = new JideButton();
    private Placement placement;
    private boolean collapsed;
    private boolean collapsible = true;
    private Component component;

    public SidePanel() {
        this(Placement.EAST, false);
    }

    public SidePanel(@NotNull Placement placement) {
        this(placement, false);
    }

    public SidePanel(boolean collapsed) {
        this(Placement.EAST, collapsed);
    }

    public SidePanel(@NotNull Placement placement, boolean collapsed) {
        super(new BorderLayout());
        setPlacement(placement);
        this.collapsed = collapsed;
        collapseButton.setButtonStyle(JideButton.FLAT_STYLE);
        collapseButton.setMargin(new Insets(2, 2, 2, 2));
        collapseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleCollapsed();
            }
        });
        updateCollapseButton();
    }

    protected SidePanelResources getResources() {
        return I18N.getBundle(SidePanelResources.class);
    }

    public void addCollapseListener(ChangeListener listener) {
        collapseEvents.addListener(listener);
    }

    public void removeCollapseListener(ChangeListener listener) {
        collapseEvents.removeListener(listener);
    }

    public ChangeListener[] getCollapseListeners(ChangeListener listener) {
        return collapseEvents.getListeners();
    }

    public JideButton getCollapseButton() {
        return collapseButton;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        if ( component != this.component ) {
            if ( this.component != null ) {
                remove(component);
            }
            this.component = component;
            if ( this.component != null ) {
                component.setVisible(!isCollapsed());
                add(component, BorderLayout.CENTER);
            }
        }
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        if ( !collapsible ) {
            // just set the flag internally, but don't do anything
            this.collapsed = collapsed;
        }
        else if ( collapsed != this.collapsed ) {
            this.collapsed = collapsed;
            updateCollapsed();
        }
    }

    public boolean isCollapsible() {
        return collapsible;
    }

    public void setCollapsible(boolean collapsible) {
        if ( this.collapsible != collapsible ) {
            boolean wasCollapsed = isCollapsed();
            this.collapsible = collapsible;
            collapseButton.setVisible(collapsible);
            if ( wasCollapsed != isCollapsed() ) {
                updateCollapsed();
            }
        }
    }

    private void updateCollapsed() {
        collapseEvents.emitter().stateChanged(new ChangeEvent(this));
        if ( component != null ) {
            component.setVisible(!isCollapsed());
        }
        updateCollapseButton();
        revalidate();
    }

    public void toggleCollapsed() {
        setCollapsed(!isCollapsed());
    }

    @NotNull
    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        if ( this.placement != placement ) {
            this.placement = placement;
            remove(collapseButton);
            switch ( placement ) {
                case EAST:
                    add(collapseButton, BorderLayout.WEST);
                    break;
                case WEST:
                    add(collapseButton, BorderLayout.EAST);
                    break;
                case NORTH:
                    add(collapseButton, BorderLayout.SOUTH);
                    break;
                case SOUTH:
                    add(collapseButton, BorderLayout.NORTH);
                    break;
            }
            updateCollapseButton();
        }
    }

    private void updateCollapseButton() {
        switch ( placement ) {
            case EAST:
                if ( isCollapsed() ) {
                    collapseButton.setIcon(res.smallArrowLeft());
                }
                else {
                    collapseButton.setIcon(res.smallArrowRight());
                }
                break;
            case WEST:
                if ( isCollapsed() ) {
                    collapseButton.setIcon(res.smallArrowRight());
                }
                else {
                    collapseButton.setIcon(res.smallArrowLeft());
                }
                break;
            case NORTH:
                if ( isCollapsed() ) {
                    collapseButton.setIcon(res.smallArrowDown());
                }
                else {
                    collapseButton.setIcon(res.smallArrowUp());
                }
                break;
            case SOUTH:
                if ( isCollapsed() ) {
                    collapseButton.setIcon(res.smallArrowUp());
                }
                else {
                    collapseButton.setIcon(res.smallArrowDown());
                }
                break;
        }
    }

    public static enum Placement {
        EAST(BorderLayout.EAST),
        WEST(BorderLayout.WEST),
        NORTH(BorderLayout.NORTH),
        SOUTH(BorderLayout.SOUTH);

        private final String borderLayoutPosition;
        private Placement(String borderLayoutPosition) {
            this.borderLayoutPosition = borderLayoutPosition;
        }
        public String getBorderLayoutPosition() {
            return borderLayoutPosition;
        }
    }

    public static void main(String[] args) throws Exception {
        SwingUtil.setupMetalLookAndFeel();
        final JFrame frame = new JFrame("SidePanel Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DefaultComboBoxModel placementModel = new DefaultComboBoxModel();
        for ( Placement p : Placement.values() ) {
            placementModel.addElement(p);
        }
        final JComboBox placement = new JComboBox(placementModel);
        placement.setSelectedItem(Placement.EAST);
        frame.add(placement, BorderLayout.NORTH);
        final JPanel demoPanel = new JPanel(new BorderLayout());
        frame.add(demoPanel, BorderLayout.CENTER);
        demoPanel.add(new JScrollPane(new JTable()), BorderLayout.CENTER);
        CommonActionGroup group = new CommonActionGroup("SideMenu");
        ActionGroupBuilder builder = new ActionGroupBuilder(group);
        builder.group(new CommonActionGroup("A group"));
        //builder.get().setFlat(true);
        builder.add(new CommonAction("An Action") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                    }
                });
        builder.add(new CommonAction("Another Action") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        builder.endgroup();
        builder.group(new CommonActionGroup("Another Group"));
        //builder.get().setFlat(true);
        builder.add(new CommonAction("Delete") {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        builder.group(new CommonActionGroup("Say"));
        builder.add(new CommonAction("Hello") {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        builder.add(new CommonAction("World") {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        builder.endgroup();
        builder.endgroup();
        final SidePanel sidePanel = new SideMenuBuilder().add(builder.get().getActions()).build();
        demoPanel.add(sidePanel, BorderLayout.EAST);
        placement.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                frame.remove(sidePanel);
                sidePanel.setPlacement((Placement)placement.getSelectedItem());
                demoPanel.add(sidePanel, ((Placement)placement.getSelectedItem()).getBorderLayoutPosition());
                demoPanel.revalidate();
            }
        });
        ////menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        //for ( Action a : group.getActions() ) {
        //    JideButton b = new JideButton(a);
        //    //b.setMaximumSize(new Dimension(Integer.MAX_VALUE, b.getMaximumSize().height));
        //    b.setButtonStyle(JideButton.TOOLBAR_STYLE);
        //    menu.add(b);
        //}
        //sidePanel.setComponent(new SimpleScrollPane(menu));
        frame.pack();
        frame.setVisible(true);
    }

}
