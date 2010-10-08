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
public interface ActionComponentFactory {
    // FIXME: support ToggleActions, ToggleActionGroups

    @NotNull
    JToolBar createToolBar(@NotNull Action... actions);
    @NotNull
    JToolBar createToolBar(@NotNull ActionGroup group);
    @NotNull
    JToolBar createToolBar(@NotNull Iterable<Action> actions);

    @NotNull
    JMenu createMenu(@NotNull Action... actions);
    @NotNull
    JMenu createMenu(@NotNull ActionGroup group);
    @NotNull
    JMenu createMenu(@NotNull Iterable<Action> actions);

    @NotNull
    JPopupMenu createPopupMenu(@NotNull Action... actions);
    @NotNull
    JPopupMenu createPopupMenu(@NotNull ActionGroup group);
    @NotNull
    JPopupMenu createPopupMenu(@NotNull Iterable<Action> actions);

    void append(@NotNull JToolBar toolBar, @NotNull Action action);
    void append(@NotNull JToolBar toolBar, @NotNull Action... actions);
    void append(@NotNull JToolBar toolBar, @NotNull Iterable<Action> actions);
    void append(@NotNull JMenu menu, @NotNull Action action);
    void append(@NotNull JMenu menu, @NotNull Action... actions);
    void append(@NotNull JMenu menu, @NotNull Iterable<Action> actions);
    void append(@NotNull JPopupMenu popupMenu, @NotNull Action action);
    void append(@NotNull JPopupMenu popupMenu, @NotNull Action... actions);
    void append(@NotNull JPopupMenu popupMenu, @NotNull Iterable<Action> actions);

    // FIXME: single menu items, handle action groups correctly

    @NotNull
    JButton createButton(@NotNull Action action);

}
