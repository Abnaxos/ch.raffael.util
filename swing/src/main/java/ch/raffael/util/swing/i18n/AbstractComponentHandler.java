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

package ch.raffael.util.swing.i18n;

import java.awt.Component;

import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class AbstractComponentHandler<T> implements ComponentHandler {

    protected final Class<T> clazz;

    public AbstractComponentHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void localize(Component component, StringLocalizer localizer, ResourceBundle bundle) {
        if ( clazz.isInstance(component) ) {
            doLocalize((T)component, localizer, bundle);
        }
    }

    protected abstract void doLocalize(T component, StringLocalizer localizer, ResourceBundle bundle);
}
