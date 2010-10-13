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
import javax.swing.JToolBar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ToolBarPresentationBuilder extends AbstractPresentationBuilder<JToolBar> {

    @Override
    protected JToolBar createTarget(@Nullable Action action) {
        return new JToolBar();
    }

    @Override
    protected void setupTarget(@NotNull JToolBar target, @Nullable Action root) {
        super.setupTarget(target, root);
        target.setFloatable(false);
        if ( root != null ) {
            target.setName((String)root.getValue(Action.NAME));
        }
    }

    @Override
    protected void addActionComponent(@NotNull JToolBar target, @NotNull Action action) {
        getTarget().add(action);
    }

    @NotNull
    @Override
    protected PresentationBuilder<? extends Component> createBuilder(@NotNull final ActionGroup group) {
        throw new IllegalArgumentException("Action groups in toolbars not supported");
    }

    @Override
    protected void addSeparator(JToolBar target) {
        target.addSeparator();
    }
}
