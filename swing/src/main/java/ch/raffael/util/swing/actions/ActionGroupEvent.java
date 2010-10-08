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

package ch.raffael.util.swing.actions;

import java.util.EventObject;

import javax.swing.Action;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ActionGroupEvent extends EventObject {

    private final Action action;
    private final int index;

    public ActionGroupEvent(ActionGroup source, Action action, int index) {
        super(source);
        this.index = index;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public int getIndex() {
        return index;
    }
}
