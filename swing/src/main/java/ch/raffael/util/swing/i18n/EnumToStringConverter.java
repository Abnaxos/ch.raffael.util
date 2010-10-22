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
public class EnumToStringConverter<T extends Enum> {

    private final Class<? extends Enum> enumClass;
    private final ResourceBundle.Resource<String> resource;

    @SuppressWarnings({ "unchecked" })
    public EnumToStringConverter(Class<T> enumClass, ResourceBundle.Resource<String> resource) {
        this.enumClass = enumClass;
        this.resource = resource;

    }

    @SuppressWarnings({ "unchecked" })
    public EnumToStringConverter(Class<T> enumClass, ResourceBundle bundle, String methodName) {
        this(enumClass, bundle.meta().resource(String.class, methodName, enumClass));
    }

    public static <T extends Enum> EnumToStringConverter<T> create(Class<T> enumClass, ResourceBundle.Resource<String> resource) {
        return new EnumToStringConverter<T>(enumClass, resource);
    }

    public static <T extends Enum> EnumToStringConverter<T> create(Class<T> enumClass, ResourceBundle bundle, String methodName) {
        return new EnumToStringConverter<T>(enumClass, bundle, methodName);
    }

    public Class<? extends Enum> getEnumClass() {
        return enumClass;
    }

    public String asString(T value) {
        return resource.get(value);
    }

}
