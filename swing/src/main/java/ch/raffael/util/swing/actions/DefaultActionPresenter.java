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

import java.util.HashMap;
import java.util.Map;

import ch.raffael.util.beans.BeanException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultActionPresenter implements ActionPresenter {

    private Map<Class<?>, Class<? extends PresentationBuilder>> builders = new HashMap<Class<?>, Class<? extends PresentationBuilder>>();

    @SuppressWarnings({ "unchecked" })
    @Override
    public <T> PresentationBuilder<T> builder(Class<T> targetClass) {
        Class<? extends PresentationBuilder<T>> builderClass = (Class<? extends PresentationBuilder<T>>)builders.get(targetClass);
        if ( builderClass == null ) {
            throw new IllegalArgumentException("No builder for class " + targetClass.getName());
        }
        try {
            return builderClass.newInstance();
        }
        catch ( InstantiationException e ) {
            throw new BeanException("Error instantiating class " + builderClass, e);
        }
        catch ( IllegalAccessException e ) {
            throw new BeanException("Error instantiating class " + builderClass, e);
        }
    }

    public <T> void setBuilder(Class<T> targetClass, Class<? extends PresentationBuilder<T>> builderClass) {
        builders.put(targetClass, builderClass);
    }

}
