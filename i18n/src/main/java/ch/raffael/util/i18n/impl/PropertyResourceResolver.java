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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;

import ch.raffael.util.common.UnexpectedException;
import ch.raffael.util.common.logging.LogUtil;
import ch.raffael.util.i18n.I18NException;
import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class PropertyResourceResolver implements ResourceResolver {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private final Class<? extends ResourceBundle> bundleClass;
    private final URL baseUrl;
    private final Properties properties = new Properties();

    public PropertyResourceResolver(Class<? extends ResourceBundle> bundleClass) {
        this.bundleClass = bundleClass;
        String strippedName = Util.stripPackage(bundleClass);
        URL url = bundleClass.getResource(strippedName + ".properties");
        boolean foundProperties;
        if ( url == null ) {
            log.debug("No resource {} for bundle {}, trying class name as base URL", strippedName + ".properties", bundleClass);
            foundProperties = false;
            url = bundleClass.getResource(strippedName + ".class");
        }
        else {
            foundProperties = true;
        }
        this.baseUrl = url;
        log.debug("Base URL for {} is {}", bundleClass, url);
        if ( foundProperties ) {
            InputStream stream = null;
            try {
                stream = new BufferedInputStream(url.openStream());
                stream = new BufferedInputStream(stream);
                Reader reader;
                try {
                    reader = new InputStreamReader(stream, "UTF-8");
                }
                catch ( UnsupportedEncodingException e ) {
                    throw new UnexpectedException(e);
                }
                properties.load(reader);
            }
            catch ( IOException e ) {
                throw new I18NException("Error reading resource " + strippedName + " for " + bundleClass, e);
            }
            finally {
                if ( stream != null ) {
                    try {
                        stream.close();
                    }
                    catch ( Exception e ) {
                        log.warn("Error closing stream from resource " + strippedName + " for " + bundleClass, e);
                    }
                }
            }
        }
    }

    @Override
    public URL getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getValue(ResourcePointer ptr) {
        String key = ptr.getKeyString();
        String[] search = ptr.getLocaleSearch().getSearch(key, null, '-');
        for ( String l : search ) {
            String value = properties.getProperty(l);
            if ( value != null ) {
                return value;
            }
        }
        return null;
    }
}
