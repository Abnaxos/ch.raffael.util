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
import javax.swing.JPopupMenu;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PopupMenuPresentationBuilder extends AbstractPresentationBuilder<JPopupMenu> {

    @Override
    protected JPopupMenu createTarget(@Nullable Action action) {
        return new JPopupMenu();
    }

    @Override
    protected void setupTarget(@NotNull JPopupMenu target, @Nullable Action root) {
        super.setupTarget(target, root);
        if ( root != null ) {
            target.setLabel((String)root.getValue(Action.NAME));
        }
    }

    @Override
    protected void addActionComponent(@NotNull JPopupMenu target, @NotNull Action action) {
        target.add(action);
    }

    @NotNull
    @Override
    protected PresentationBuilder<? extends Component> createBuilder(@NotNull ActionGroup action) {
        return new MenuPresentationBuilder().init(action);
    }

    @Override
    protected void addSeparator(JPopupMenu target) {
        target.addSeparator();
    }
}
