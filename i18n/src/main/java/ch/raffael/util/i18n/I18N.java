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

package ch.raffael.util.i18n;

import java.util.Locale;

import org.slf4j.Logger;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.common.annotations.Utility;
import ch.raffael.util.common.logging.LogUtil;
import ch.raffael.util.i18n.impl.BundleManager;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Utility
public class I18N {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private static volatile boolean lenient = false;
    private static Locale locale = Locale.getDefault();
    private static LocaleSearch localeSearch = new LocaleSearch(locale);

    private I18N() {
    }

    public static synchronized Locale getLocale() {
        return locale;
    }

    public static synchronized void setLocale(Locale locale) {
        I18N.locale = locale;
        localeSearch = new LocaleSearch(I18N.locale);
    }

    public static synchronized LocaleSearch getLocaleSearch() {
        return localeSearch;
    }

    public static boolean isLenient() {
        return lenient;
    }

    public static void setLenient(boolean lenient) {
        I18N.lenient = lenient;
    }

    @NotNull
    public static <T extends ResourceBundle> T getBundle(Class<T> bundleClass) {
        return BundleManager.getInstance().getOrLoad(bundleClass).getResourceBundle(bundleClass);
    }

}
