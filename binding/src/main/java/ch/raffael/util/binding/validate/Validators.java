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

package ch.raffael.util.binding.validate;

/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Validators {

    private Validators() {
    }

    public static Message.Severity max(Message.Severity a, Message.Severity b) {
        if ( a == null ) {
            return b;
        }
        else if ( b == null ) {
            return a;
        }
        else if ( a.compareTo(b) > 0 ) {
            return a;
        }
        else {
            return b;
        }
    }
}
