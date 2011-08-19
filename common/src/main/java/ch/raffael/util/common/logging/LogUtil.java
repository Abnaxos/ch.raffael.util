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

package ch.raffael.util.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.common.Classes;
import ch.raffael.util.common.annotations.Utility;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
@Utility
public class LogUtil {

    private LogUtil() {
    }

    @NotNull
    public static Logger getLogger(@NotNull Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    @NotNull
    public static Logger getLogger(@NotNull Object obj) {
        return LoggerFactory.getLogger(obj.getClass());
    }

    @NotNull
    public static Logger getLogger() {
        return getLogger(Classes.callerClass(LogUtil.class));
    }

}
