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

package ch.raffael.util.swing.context;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Used by {@link Context#instantiate(Class)}. It allows to specify what to inject into
 * constructor or method parameters. If a method is annotated with this annotation, the
 * method itself will be called after construction. If the method has just one parameter,
 * any specifications here will be applied to that parameter, if the method has more,
 * specifications here will be ignored, annotate the methods instead. Only methods
 * annotated with @Inject will be called after construction.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Documented
@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

    Class<Void> USE_PARAM_CLASS = Void.class;

    Class<?> type() default Void.class;

    String key() default "";

    boolean optional() default true;

}
