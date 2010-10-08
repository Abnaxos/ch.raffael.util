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

import java.net.URL;

import javax.imageio.ImageIO;

import ch.raffael.util.common.NotImplementedException;
import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.i18n.impl.ResourcePointer;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ImageHandler extends NoParametersHandler {

    @Override
    public Object resolve(Class<? extends ResourceBundle> bundleClass, ResourcePointer ptr, URL baseUrl, String value) throws Exception {
        return ImageIO.read(new URL(baseUrl, value));
    }

    @Override
    public Object notFound(Class<? extends ResourceBundle> bundleClass, ResourcePointer ptr, URL baseUrl) throws Exception {
        throw new NotImplementedException("notFound"); // FIXME: not implemented
    }
}
