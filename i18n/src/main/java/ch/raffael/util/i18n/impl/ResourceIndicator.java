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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ResourceIndicator {

    public static final ResourceIndicator META = new ResourceIndicator("meta", null);

    private final String methodName;
    private final List<Class<?>> parameterTypes;

    public ResourceIndicator(@NotNull String methodName, @Nullable Class<?>[] parameterTypes) {
        this.methodName = methodName;
        if ( parameterTypes == null ) {
            this.parameterTypes = Collections.emptyList();
        }
        else {
            this.parameterTypes = Collections.unmodifiableList(Arrays.asList(parameterTypes));
        }
    }

    @Override
    public String toString() {
        if ( parameterTypes.size() == 0 ) {
            return methodName;
        }
        else {
            StringBuilder buf = new StringBuilder(methodName);
            for ( Class<?> parameterType : parameterTypes ) {
                buf.append(',').append(parameterType.getName());
            }
            return buf.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        ResourceIndicator that = (ResourceIndicator)o;
        if ( !methodName.equals(that.methodName) ) {
            return false;
        }
        return parameterTypes.equals(that.parameterTypes);
    }

    @Override
    public int hashCode() {
        int result = methodName.hashCode();
        result = 31 * result + parameterTypes.hashCode();
        return result;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<Class<?>> getParameterTypes() {
        return parameterTypes;
    }
}
