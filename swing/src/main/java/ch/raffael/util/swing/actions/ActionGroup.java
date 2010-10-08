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

import java.util.List;

import javax.swing.Action;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface ActionGroup extends Action {

    String PROPERTY_DEFAULT_ACTION = "defaultAction";
    Separator SEPARATOR = new Separator();

    void addActionGroupListener(ActionGroupListener listener);

    void removeActionGroupListener(ActionGroupListener listener);

    void addAction(Action action);

    void addAction(int index, Action action);

    void removeAction(Action action);

    Action removeActionAt(int index);

    int getActionCount();

    Action getActionAt(int index);

    int indexOfAction(Action action);

    List<Action> getActions();

    Action getDefaultAction();

    void setDefaultAction(Action defaultAction);

    boolean isFlat();

    void setFlat(boolean flat);

}
