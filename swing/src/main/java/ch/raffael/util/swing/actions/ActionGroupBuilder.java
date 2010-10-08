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

import java.util.LinkedList;

import javax.swing.Action;

import org.jetbrains.annotations.NotNull;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ActionGroupBuilder {

    private final LinkedList<ActionGroup> stack = new LinkedList<ActionGroup>();

    public ActionGroupBuilder(@NotNull ActionGroup root) {
        stack.push(root);
    }

    public ActionGroupBuilder add(Action action) {
        stack.peek().addAction(action);
        return this;
    }

    public ActionGroupBuilder separator() {
        add(Separator.SEPARATOR);
        return this;
    }

    public ActionGroupBuilder group(ActionGroup group) {
        add(group);
        stack.push(group);
        return this;
    }

    public ActionGroupBuilder endgroup() {
        stack.pop();
        return this;
    }

    public ActionGroup get() {
        return stack.peek();
    }

    public ActionGroupBuilder reset(@NotNull ActionGroup root) {
        stack.clear();
        stack.push(root);
        return this;
    }

}
