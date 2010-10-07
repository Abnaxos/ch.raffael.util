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

package ch.raffael.util.common;

import org.jetbrains.annotations.NotNull;


/**
 * Exception used to indicate that some unexpected exception has been caught. E.g.,
 * <code>new String(myByteArray, "UTF-8")</code> is declared to throw a
 * <code>UnsupportedEncodingException</code>, however, by specification, the charset
 * UTF-8 must be supported by any JVM.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class UnexpectedException extends RuntimeException {

    public UnexpectedException(@NotNull Throwable cause) {
        super(cause);
    }

    public UnexpectedException(String message, @NotNull Throwable cause) {
        super((message == null ? cause.toString() : message), cause);
    }
}
