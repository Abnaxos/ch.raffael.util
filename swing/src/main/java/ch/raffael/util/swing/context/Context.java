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

package ch.raffael.util.swing.context;

import java.awt.Component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface Context {

    @Nullable
    Context getParent();

    @Nullable
    <T> T find(@NotNull Class<T> clazz);

    @Nullable
    <T> T find(@NotNull Class<T> clazz, @Nullable Object key);

    @NotNull
    <T> T require(@NotNull Class<T> clazz);

    @NotNull
    <T> T require(@NotNull Class<T> clazz, @Nullable Object key);

    @Nullable
    <T> T get(@NotNull Class<T> clazz);

    @Nullable
    <T> T get(@NotNull Class<T> clazz, @Nullable Object key);

    @Nullable
    <T> T remove(@NotNull Class<T> clazz);

    @Nullable
    <T> T remove(@NotNull Class<T> clazz, @Nullable Object key);

    @Nullable
    <T> T put(@NotNull Class<T> clazz, @NotNull T value);

    @Nullable
    <T> T put(@NotNull Class<T> clazz, @Nullable Object key, @NotNull T value);

    void attach(@NotNull Component component);

    boolean detach(@NotNull Component component);

    Context create();

    Context create(@NotNull Component attachTo);

    <T> T instantiate(Class<T> clazz);
}
