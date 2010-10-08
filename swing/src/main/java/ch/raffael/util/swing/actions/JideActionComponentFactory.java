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

package ch.raffael.util.swing.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import com.jidesoft.swing.ButtonStyle;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JidePopupMenu;
import com.jidesoft.swing.JideSplitButton;
import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class JideActionComponentFactory extends DefaultActionComponentFactory {


    @Override
    public void append(@NotNull JToolBar toolBar, @NotNull Action action) {
        if ( action instanceof Separator ) {
            toolBar.addSeparator();
        }
        else if ( action instanceof ActionGroup ) {
            final ActionGroup group = (ActionGroup)action;
            final JideSplitButton button = new JideSplitButton(action);
            if ( (action.getValue(Action.SMALL_ICON) != null || action.getValue(Action.LARGE_ICON_KEY) != null) ) {
                button.setHideActionText(true);
            }
            //button.setBorderPainted(false);
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.BOTTOM);
            button.setButtonStyle(ButtonStyle.TOOLBAR_STYLE);
            if ( group.getDefaultAction() == null ) {
                button.setAlwaysDropdown(true);
            }
            else {
                button.setAlwaysDropdown(!group.getDefaultAction().isEnabled());
            }
            group.addPropertyChangeListener(new PropertyChangeListener() {
                private Action defaultAction = group.getDefaultAction();
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ( evt.getSource() == group && evt.getPropertyName().equals(ActionGroup.PROPERTY_DEFAULT_ACTION) ) {
                        if ( defaultAction != null ) {
                            defaultAction.removePropertyChangeListener(this);
                        }
                        defaultAction = (Action)evt.getNewValue();
                        if ( defaultAction != null ) {
                            defaultAction.addPropertyChangeListener(this);
                            button.setAlwaysDropdown(!defaultAction.isEnabled());
                        }
                    }
                    else if ( evt.getSource() == defaultAction && evt.getPropertyName().equals("enabled") ) {
                        button.setAlwaysDropdown(!(Boolean)evt.getNewValue());
                    }
                }
            });
            toolBar.add(button);
        }
        else {
            JideButton button = new JideButton(action);
            if (action != null && (action.getValue(Action.SMALL_ICON) != null || action.getValue(Action.LARGE_ICON_KEY) != null)) {
                button.setHideActionText(true);
            }
            button.setBorderPainted(false);
            button.setHorizontalTextPosition(JButton.CENTER);
            button.setVerticalTextPosition(JButton.BOTTOM);
            button.setButtonStyle(ButtonStyle.TOOLBAR_STYLE);
            toolBar.add(button);
        }
    }

    @Override
    protected JPopupMenu createPopupMenuComponent(ActionGroup group) {
        return new JidePopupMenu();
    }
}
