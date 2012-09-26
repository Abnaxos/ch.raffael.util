/*
 * Copyright (c) 2009, Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.raffael.util.classloader;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface DelegatingClassLoaderMBean {

    int REPORT_ALL = 0;
    int REPORT_INTERNAL = 1;
    int REPORT_IMPORTED = 2;
    int REPORT_FAILED = 3;

    int getLoadedCount();

    int getImportedCount();

    int getFailedCount();

    int getInternalCount();

    int getTotalCount();

    TabularData getReport(int type, int startIndex) throws OpenDataException;

}
