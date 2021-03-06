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

package ch.raffael.util.swing.error;

import java.awt.Component;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ErrorDisplayer {

    void displayError(@NotNull Component component, String message);

    void displayError(@NotNull Component component, Throwable exception);

    void displayError(@NotNull Component component, String message, Throwable exception);

    void displayError(@NotNull Component component, String message, Object details);

    void displayError(@NotNull Component component, Throwable exception, Object details);

    void displayError(@NotNull Component component, String message, Throwable exception, Object details);

}
