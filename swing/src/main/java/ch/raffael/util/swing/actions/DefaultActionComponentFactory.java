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

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultActionComponentFactory implements ActionComponentFactory {
    // FIXME: support ToggleActions, ToggleActionGroups

    private boolean defaultFloatableToolBars = false;

    @NotNull
    @Override
    public JToolBar createToolBar(@NotNull Action... actions) {
        JToolBar toolBar = createToolBar();
        append(toolBar, actions);
        return toolBar;
    }

    @NotNull
    @Override
    public JToolBar createToolBar(@NotNull ActionGroup group) {
        return createToolBar(group.getActions());
    }

    @NotNull
    @Override
    public JToolBar createToolBar(@NotNull Iterable<Action> actions) {
        JToolBar toolBar = createToolBar();
        append(toolBar, actions);
        return toolBar;
    }

    protected JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        return toolBar;
    }

    @NotNull
    @Override
    public JButton createButton(@NotNull Action action) {
        return new JButton(action);
    }

    @NotNull
    @Override
    public JMenu createMenu(@NotNull Action... actions) {
        JMenu menu = createMenuComponent(null);
        append(menu, actions);
        return menu;
    }

    @NotNull
    @Override
    public JMenu createMenu(@NotNull ActionGroup group) {
        JMenu menu = createMenuComponent(group);
        append(menu, group.getActions());
        return menu;
    }

    @NotNull
    @Override
    public JMenu createMenu(@NotNull Iterable<Action> actions) {
        JMenu menu = createMenuComponent(null);
        append(menu, actions);
        return menu;
    }

    protected JMenu createMenuComponent(ActionGroup actionGroup) {
        if ( actionGroup == null ) {
            return new JMenu();
        }
        else {
            return new JMenu(actionGroup);
        }
    }

    @NotNull
    @Override
    public JPopupMenu createPopupMenu(@NotNull Action... actions) {
        JPopupMenu popupMenu = createPopupMenuComponent(null);
        append(popupMenu, actions);
        return popupMenu;
    }

    @NotNull
    @Override
    public JPopupMenu createPopupMenu(@NotNull ActionGroup group) {
        JPopupMenu popupMenu = createPopupMenuComponent(group);
        append(popupMenu, group.getActions());
        return popupMenu;
    }

    @NotNull
    @Override
    public JPopupMenu createPopupMenu(@NotNull Iterable<Action> actions) {
        JPopupMenu popupMenu = createPopupMenuComponent(null);
        append(popupMenu, actions);
        return popupMenu;
    }

    protected JPopupMenu createPopupMenuComponent(ActionGroup group) {
        return new JPopupMenu();
    }

    @Override
    public void append(@NotNull JToolBar toolBar, @NotNull Action action) {
        if ( action instanceof Separator ) {
            toolBar.addSeparator();
        }
        else {
            toolBar.add(action);
        }
    }

    @Override
    public void append(@NotNull JToolBar toolBar, @NotNull Action... actions) {
        for ( Action action : actions ) {
            append(toolBar, action);
        }
    }

    @Override
    public void append(@NotNull JToolBar toolBar, @NotNull Iterable<Action> actions) {
        for ( Action action : actions ) {
            append(toolBar, action);
        }
    }

    @Override
    public void append(@NotNull JMenu menu, @NotNull Action action) {
        if ( action instanceof Separator ) {
            menu.addSeparator();
        }
        else if ( action instanceof ActionGroup ) {
            menu.add(createMenu((ActionGroup)action));
        }
        else {
            menu.add(action);
        }
    }

    @Override
    public void append(@NotNull JMenu menu, @NotNull Action... actions) {
        for ( Action action : actions ) {
            append(menu, action);
        }
    }

    @Override
    public void append(@NotNull JMenu menu, @NotNull Iterable<Action> actions) {
        for ( Action action : actions ) {
            append(menu, action);
        }
    }

    @Override
    public void append(@NotNull JPopupMenu popupMenu, @NotNull Action action) {
        if ( action instanceof Separator ) {
            popupMenu.addSeparator();
        }
        else if ( action instanceof ActionGroup ) {
            popupMenu.add(createPopupMenu((ActionGroup)action));
        }
        else {
            popupMenu.add(action);
        }
    }

    @Override
    public void append(@NotNull JPopupMenu popupMenu, @NotNull Action... actions) {
        for ( Action action : actions ) {
            append(popupMenu, action);
        }
    }

    @Override
    public void append(@NotNull JPopupMenu popupMenu, @NotNull Iterable<Action> actions) {
        for ( Action action : actions ) {
            append(popupMenu, action);
        }
    }

    public boolean isDefaultFloatableToolBars() {
        return defaultFloatableToolBars;
    }

    public void setDefaultFloatableToolBars(boolean defaultFloatableToolBars) {
        this.defaultFloatableToolBars = defaultFloatableToolBars;
    }

}
