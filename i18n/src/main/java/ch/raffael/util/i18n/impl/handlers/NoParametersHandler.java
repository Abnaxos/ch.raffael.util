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

package ch.raffael.util.i18n.impl.handlers;

import ch.raffael.util.i18n.I18NException;
import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.i18n.impl.Handler;
import ch.raffael.util.i18n.impl.MethodSignature;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class NoParametersHandler implements Handler {

    @Override
    public void validateSignature(Class<? extends ResourceBundle> bundleClass, MethodSignature signature) throws I18NException {
        if ( signature.getParameterCount() > 0 ) {
            throw new I18NException("Parameters not supported for resource type " + signature.getReturnType().getName());
        }
    }

    @Override
    public Object parametrize(Object value, Object[] parameters) throws Exception {
        throw new I18NException("Handler " + getClass().getName() + " cannot handle parameters");
    }
}
