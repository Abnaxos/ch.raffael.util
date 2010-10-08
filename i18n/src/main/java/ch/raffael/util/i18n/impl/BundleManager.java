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

package ch.raffael.util.i18n.impl;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.common.annotations.Singleton;
import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Singleton
public class BundleManager {

    public static final BundleManager INSTANCE = new BundleManager();

    private final Map<Class<? extends ResourceBundle>, Bundle> bundles = new HashMap<Class<? extends ResourceBundle>, Bundle>();
    
    private BundleManager() {
    }

    public static BundleManager getInstance() {
        return INSTANCE;
    }

    @NotNull
    public synchronized Bundle getOrLoad(@NotNull Class<? extends ResourceBundle> bundleClass) {
        // FIXME: check at end?
        Bundle bundle = bundles.get(bundleClass);
        if ( bundle == null ) {
            bundle = new Bundle(bundleClass);
            bundles.put(bundleClass, bundle);
            bundle.init();
        }
        return bundle;
    }

}
