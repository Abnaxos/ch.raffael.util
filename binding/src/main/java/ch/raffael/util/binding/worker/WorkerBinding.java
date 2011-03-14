/*
 * Copyright 2011 Raffael Herzog
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

package ch.raffael.util.binding.worker;

import java.util.concurrent.Executor;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class WorkerBinding {

    private static final Splitter IN_PLACE_SPLITTER = new Splitter() {
        private final Executor executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        };
        @Override
        public Executor worker() {
            return executor;
        }
        @Override
        public Executor display() {
            return executor;
        }
    };

    public static Splitter inPlaceSplitter() {
        return IN_PLACE_SPLITTER;
    }

    public static interface Splitter {
        Executor worker();
        Executor display();

    }
}
