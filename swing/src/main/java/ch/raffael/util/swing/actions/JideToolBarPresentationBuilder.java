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

import java.awt.Component;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.jidesoft.swing.ButtonStyle;
import com.jidesoft.swing.JideButton;
import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class JideToolBarPresentationBuilder extends ToolBarPresentationBuilder {

    @Override
    protected void addActionComponent(@NotNull JToolBar target, @NotNull Action action) {
        JideButton button = new JideButton(action);
        if ( action.getValue(Action.SMALL_ICON) != null || action.getValue(Action.LARGE_ICON_KEY) != null ) {
            button.setHideActionText(true);
        }
        button.setBorderPainted(false);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setButtonStyle(ButtonStyle.TOOLBAR_STYLE);
        target.add(button);
    }

    @NotNull
    @Override
    protected PresentationBuilder<? extends Component> createBuilder(@NotNull ActionGroup group) {
        return new JideSplitButtonPresentationBuilder().init(group);
    }

}
