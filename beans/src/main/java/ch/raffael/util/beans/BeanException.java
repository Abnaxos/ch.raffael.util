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

package ch.raffael.util.beans;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class BeanException extends RuntimeException {

    public BeanException() {
        super();
    }

    public BeanException(String message) {
        super(message);
    }

    public BeanException(Throwable cause) {
        super(cause);
    }

    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }
}
