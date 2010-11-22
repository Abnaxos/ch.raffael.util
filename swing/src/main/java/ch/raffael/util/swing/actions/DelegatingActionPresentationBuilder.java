/*
 * Copyright 2010 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class DelegatingActionPresentationBuilder<D, T extends D> implements PresentationBuilder<T> {

    private final boolean delegateSetup;
    private PresentationBuilder<D> delegate;
    private T target;

    public DelegatingActionPresentationBuilder(boolean delegateSetup) {
        this.delegateSetup = delegateSetup;
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init() {
        return init(createTarget(null), null, true);
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init(@NotNull T target) {
        return init(target, null, true);
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init(@Nullable Action root) {
        return init(createTarget(root), root, true);
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init(@NotNull T target, @Nullable Action root) {
        return init(target, root, true);
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init(@NotNull T target, boolean setupTarget) {
        return init(target, null, setupTarget);
    }

    @NotNull
    @Override
    public PresentationBuilder<T> init(@NotNull T target, @Nullable Action root, boolean setupTarget) {
        this.target = target;
        delegate = createDelegate(target);
        delegate.init(target, root, delegateSetup && setupTarget);
        if ( setupTarget ) {
            setupTarget(target, root);
        }
        return this;
    }

    @NotNull
    protected abstract PresentationBuilder<D> createDelegate(T target);

    @NotNull
    protected abstract T createTarget(@Nullable Action action);

    protected void setupTarget(@NotNull T target, @Nullable Action root) {
    }

    @Override
    public T getTarget() {
        return target;
    }

    @Override
    @NotNull
    public PresentationBuilder<T> add(@NotNull Action action) {
        checkState();
        delegate.add(action);
        return this;
    }

    @Override
    @NotNull
    public PresentationBuilder<T> add(@NotNull Action... actions) {
        checkState();
        delegate.add(actions);
        return this;
    }

    @Override
    @NotNull
    public PresentationBuilder<T> add(@NotNull Collection<Action> actions) {
        checkState();
        delegate.add(actions);
        return this;
    }

    @Override
    @NotNull
    public PresentationBuilder<T> addFlat(@NotNull Action group) {
        checkState();
        delegate.addFlat(group);
        return this;
    }

    @Override
    @NotNull
    public PresentationBuilder<T> addFlat(@NotNull Action group, boolean separate) {
        checkState();
        delegate.addFlat(group, separate);
        return this;
    }

    @Override
    @NotNull
    public PresentationBuilder<T> separator() {
        checkState();
        delegate.separator();
        return this;
    }

    @Override
    @NotNull
    public T build() {
        checkState();
        delegate.build();
        return target;
    }

    private void checkState() {
        if ( target == null ) {
            init();
        }
    }
}
