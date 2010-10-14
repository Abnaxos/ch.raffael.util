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

import ch.raffael.util.i18n.ResourceBundle;


/**
* @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
*/
public class DefaultStringLocalizer implements StringLocalizer {

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
            return bundle.meta().resource(String.class, string.substring(marker.length())).get();
        }
        else {
            return string;
        }
    }
}
