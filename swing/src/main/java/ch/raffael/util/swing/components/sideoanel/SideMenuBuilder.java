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

import java.awt.Component;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.SimpleScrollPane;
import com.jidesoft.swing.TitledSeparator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.swing.actions.AbstractPresentationBuilder;
import ch.raffael.util.swing.actions.ActionGroup;
import ch.raffael.util.swing.actions.JideSplitButtonPresentationBuilder;
import ch.raffael.util.swing.actions.PresentationBuilder;
import ch.raffael.util.swing.layout.VerticalFlowLayout;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class SideMenuBuilder extends AbstractPresentationBuilder<SidePanel> {

    private int groupSpacing = 5;
    private int horizontalAlignment = SwingConstants.LEADING;

    public SideMenuBuilder() {
    }

    public int getGroupSpacing() {
        return groupSpacing;
    }

    public void setGroupSpacing(int groupSpacing) {
        if ( groupSpacing < 0 ) {
            throw new IllegalArgumentException("Invalid spacing: " + groupSpacing);
        }
        if ( this.groupSpacing != groupSpacing ) {
            this.groupSpacing = groupSpacing;
            getMenuComponent(getTarget()).setBorder(BorderFactory.createEmptyBorder(groupSpacing, groupSpacing, groupSpacing, groupSpacing));
        }
    }

    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    @Override
    protected SidePanel createTarget(@Nullable Action action) {
        return new SidePanel();
    }

    @Override
    protected void setupTarget(@NotNull SidePanel target, @Nullable Action root) {
        super.setupTarget(target, root);
        JPanel menu = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.Alignment.FILL));
        menu.setBorder(BorderFactory.createEmptyBorder(groupSpacing, groupSpacing, groupSpacing, groupSpacing));
        SimpleScrollPane scroller = new SimpleScrollPane(menu);
        scroller.setBorder(null);
        target.setComponent(scroller);
        //target.setComponent(menu);
    }

    @Override
    protected void addActionComponent(@NotNull SidePanel target, @NotNull Action action) {
        JideButton button = new JideButton(action);
        button.setButtonStyle(JideButton.TOOLBAR_STYLE);
        button.setHorizontalAlignment(horizontalAlignment);
        addComponent(target, button);
    }

    @NotNull
    @Override
    protected PresentationBuilder<? extends Component> createBuilder(@NotNull ActionGroup action) {
        return new SideMenuBuilder() {
            @Override
            protected SidePanel createTarget(@Nullable Action action) {
                // don't change the target
                return SideMenuBuilder.this.getTarget();
            }
            @Override
            protected void setupTarget(@NotNull SidePanel target, @Nullable Action root) {
                // let's add the separator
                if ( groupSpacing > 0 ) {
                    addComponent(target, Box.createVerticalStrut(groupSpacing));
                }
                addComponent(target, createSeparator(root != null ? (String)root.getValue(Action.NAME) : ""));
            }
            @NotNull
            @Override
            protected PresentationBuilder<? extends Component> createBuilder(@NotNull ActionGroup action) {
                JideSplitButtonPresentationBuilder builder = new JideSplitButtonPresentationBuilder();/* {
                    @Override
                    protected void setupTarget(@NotNull JideSplitButton button, @Nullable Action root) {
                        super.setupTarget(button, root);
                        button.setPreferredPopupHorizontalAlignment(SwingConstants.LEADING);
                        button.setPreferredPopupVerticalAlignment(SwingConstants.NORTH);
                    }
                };*/
                builder.setHideActionText(false);
                return builder;
            }
        };
    }

    @Override
    protected void addSeparator(SidePanel target) {
        addComponent(target, createSeparator(""));
    }

    protected Component createSeparator(String title) {
        return new TitledSeparator(title, TitledSeparator.TYPE_PARTIAL_ETCHED, horizontalAlignment);
    }

    @Override
    protected void addComponent(SidePanel target, Component component) {
        getMenuComponent(target).add(component);
    }

    protected JComponent getMenuComponent(SidePanel target) {
        return (JComponent)((SimpleScrollPane)target.getComponent()).getViewport().getComponent(0);
        //return (JComponent)target.getComponent();
    }

}
