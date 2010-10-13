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

import java.util.Collection;

import javax.swing.Action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Builds a presentation (like a menu or toolbar) for a set of actions.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface PresentationBuilder<T> {

    @NotNull
    PresentationBuilder<T> init();
    @NotNull
    PresentationBuilder<T> init(@NotNull T target);

    @NotNull
    PresentationBuilder<T> init(@NotNull T target, @Nullable Action root);

    @NotNull
    PresentationBuilder<T> init(@Nullable Action root);

    @NotNull
    PresentationBuilder<T> init(@NotNull T target, boolean setupTarget);

    T getTarget();

    /**
     * Add a single action.
     * <p>
     * If the action is a:
     *
     * <dl>
     * <dt>{@link javax.swing.Action}</dt>
     * <dd>Adds this action (e.g. menu item)</dd>
     *
     * <dt>{@link ActionGroup} (flat==<code>false</code>)</dt>
     * <dd>Adds the action group as a single action with sub-actions (e.g. submenu).</dd>
     *
     * <dt>{@link ActionGroup} (flat==<code>true</code>)</dt>
     * <dd>Adds the actions in the group as multiple items, ensuring the group is
     * optically separated from other actions/groups (e.g. using a separator).</dd>
     * </dl>
     *
     * @param action The action.
     * @return <code>this</code>
     */
    @NotNull
    PresentationBuilder<T> add(@NotNull Action action);

    @NotNull
    PresentationBuilder<T> add(@NotNull Action... actions);

    @NotNull
    PresentationBuilder<T> add(@NotNull Collection<Action> actions);

    @NotNull
    PresentationBuilder<T> addFlat(@NotNull Action group);

    @NotNull
    PresentationBuilder<T> addFlat(@NotNull Action group, boolean separate);

    @NotNull
    PresentationBuilder<T> separator();

    @NotNull
    T build();

}
