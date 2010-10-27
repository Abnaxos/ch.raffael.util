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
import java.awt.Container;
import java.util.Collection;

import javax.swing.Action;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractPresentationBuilder<T extends Container> implements PresentationBuilder<T> {

    private T target;
    private boolean addSeparator = false;
    private boolean atBeginning = true;

    @NotNull
    @Override
    public PresentationBuilder<T> init() {
        init(createTarget(null), null, true);
        return this;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init(@Nullable Action root) {
        init(createTarget(root), root, true);
        return this;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init(@NotNull T target) {
        init(target, null, true);
        return this;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init(@NotNull T target, @Nullable Action root) {
        init(target, root, true);
        return this;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init(@NotNull T target, boolean setupTarget) {
        init(target, null, setupTarget);
        return this;
    }

    private void init(@NotNull T target, @Nullable Action root, boolean setupTarget) {
        this.target = target;
        atBeginning = true;
        addSeparator = false;
        if ( setupTarget ) {
            setupTarget(target, root);
        }
    }

    protected abstract T createTarget(@Nullable Action action);

    protected void setupTarget(@NotNull T target, @Nullable Action root) {
    }

    public T getTarget() {
        return target;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> add(@NotNull Action action) {
        checkState();
        maybeAddSeparator();
        if ( action instanceof ActionGroup ) {
            ActionGroup group = (ActionGroup)action;
            if ( group.isFlat() ) {
                addFlat(group, true);
            }
            else {
                Component component = createBuilder(group).init(group).add(group.getActions()).build();
                if ( component != target ) {
                    addComponent(target, component);
                }
                atBeginning = false;
            }
        }
        else if ( action instanceof Separator ) {
            separator();
        }
        else {
            addActionComponent(target, action);
            atBeginning = false;
        }
        return this;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> add(@NotNull Action... actions) {
        checkState();
        for ( Action a : actions ) {
            add(a);
        }
        return this;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> add(@NotNull Collection<Action> actions) {
        checkState();
        for ( Action a : actions ) {
            add(a);
        }
        return this;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> addFlat(@NotNull Action group) {
        checkState();
        return addFlat(group, true);
    }

    @NotNull
    @Override
    public PresentationBuilder<T> addFlat(@NotNull Action action, boolean separate) {
        checkState();
        if ( separate ) {
            separator();
        }
        if ( action instanceof ActionGroup ) {
            add(((ActionGroup)action).getActions());
        }
        else {
            add(action);
        }
        if ( separate ) {
            separator();
        }
        return this;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> separator() {
        checkState();
        if ( !atBeginning ) {
            addSeparator = true;
        }
        return this;
    }

    @NotNull
    @Override
    public T build() {
        checkState();
        return target;
    }

    protected void addComponent(T target, Component component) {
        target.add(component);
    }

    protected abstract void addActionComponent(@NotNull T target, @NotNull Action action);

    @NotNull
    protected abstract PresentationBuilder<? extends Component> createBuilder(@NotNull ActionGroup action);

    protected abstract void addSeparator(T target);

    private void checkState() {
        if ( target == null ) {
            init();
        }
    }

    private void maybeAddSeparator() {
        if ( addSeparator ) {
            addSeparator(target);
            addSeparator = false;
        }
    }

}
