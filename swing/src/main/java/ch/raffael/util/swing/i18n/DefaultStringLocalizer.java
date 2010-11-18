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

package ch.raffael.util.swing.i18n;

import ch.raffael.util.common.logging.LogUtil;
import ch.raffael.util.i18n.MethodSignature;
import ch.raffael.util.i18n.ResourceBundle;
import org.slf4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class DefaultStringLocalizer implements StringLocalizer {

    private static final Logger log = LogUtil.getLogger();
    private final String marker;

    public DefaultStringLocalizer(String marker) {
        this.marker = marker;
    }

    @Override
    public boolean shouldLocalize(String string) {
        return string.startsWith(marker);
    }

    @Override
    public String localize(String string, ResourceBundle bundle) {
        if ( shouldLocalize(string) ) {
            String key = string.substring(marker.length());
            int pos = key.indexOf('#');
            if ( pos < 0 ) {
                return bundle.meta().resource(String.class, key).get();
            }
            else {
                String name = key.substring(0, pos);
                String arg = key.substring(pos + 1);
                MethodSignature method = null;
                Object methodArgument = null;
                for ( MethodSignature sig : bundle.meta().methods() ) {
                    if ( sig.getReturnType().equals(String.class) && sig.getName().equals(name) && sig.getArguments().size() == 1 ) {
                        MethodSignature.Argument argument = sig.getArguments().get(0);
                        if ( argument.isSelector() ) {
                            Object argValue;
                            if ( argument.getType().equals(String.class) ) {
                                argValue = arg;
                            }
                            else if ( argument.getType().isEnum() ) {
                                try {
                                    argValue = enumValue(arg, argument);
                                }
                                catch ( IllegalArgumentException e ) {
                                    log.error("Cannot resolve {} in {}", new Object[] { string, bundle, e });
                                    return string;
                                }
                            }
                            else {
                                try {
                                    Constructor ctor = argument.getType().getConstructor(String.class);
                                    argValue = ctor.newInstance(arg);
                                }
                                catch ( NoSuchMethodException e ) {
                                    continue; // not a candidate
                                }
                                catch ( InvocationTargetException e ) {
                                    log.error("Cannot resolve {} in {}", new Object[] { string, bundle, e });
                                    return string;
                                }
                                catch ( InstantiationException e ) {
                                    log.error("Cannot resolve {} in {}", new Object[] { string, bundle, e });
                                    return string;
                                }
                                catch ( IllegalAccessException e ) {
                                    log.error("Cannot resolve {} in {}", new Object[] { string, bundle, e });
                                    return string;
                                }
                            }
                            if ( argValue != null ) {
                                if ( method != null ) {
                                    log.error("Ambiguous resource {} in {}", string, bundle);
                                    return string;
                                }
                                method = sig;
                                methodArgument = argValue;
                            }
                        }
                    }
                }
                if ( method != null ) {
                    return (String)bundle.meta().resource(method.getName(), method.getArguments().get(0).getType()).get(methodArgument);
                }
                else {
                    log.error("No resource matching {} found in {}");
                    return string;
                }
            }
        }
        else {
            return string;
        }
    }

    @SuppressWarnings({ "unchecked" })
    private Enum enumValue(String arg, MethodSignature.Argument argument) {
        return Enum.valueOf((Class<Enum>)argument.getType(), arg);
    }
}
