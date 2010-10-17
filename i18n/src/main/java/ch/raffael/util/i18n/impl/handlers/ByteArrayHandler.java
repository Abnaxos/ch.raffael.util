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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;

import ch.raffael.util.common.logging.LogUtil;
import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.i18n.impl.ResourcePointer;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ByteArrayHandler extends NoParametersHandler {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    @Override
    public Object resolve(Class<? extends ResourceBundle> bundleClass, ResourcePointer ptr, URL baseUrl, String value) throws Exception {
        URL url = new URL(baseUrl, value);
        InputStream input = new BufferedInputStream(url.openStream());
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int count;
            while ( (count = input.read(buf)) >= 0 ) {
                bytes.write(buf, 0, count);
            }
            return bytes.toByteArray();
        }
        finally {
            try {
                input.close();
            }
            catch ( Exception e ) {
                log.error("Error closing stream from " + url, e);
            }
        }
    }

    @Override
    public Object notFound(Class<? extends ResourceBundle> bundleClass, ResourcePointer ptr, URL baseUrl) {
        return new byte[0];
    }
}
