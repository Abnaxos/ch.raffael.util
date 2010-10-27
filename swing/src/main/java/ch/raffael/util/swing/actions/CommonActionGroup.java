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

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import ch.raffael.util.beans.EventEmitter;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CommonActionGroup extends CommonAction implements ActionGroup {

    private final List<Action> actions = new LinkedList<Action>();
    private final EventEmitter<ActionGroupListener> emitter = EventEmitter.newEmitter(ActionGroupListener.class);

    public CommonActionGroup(String name) {
        super(name);
    }

    public CommonActionGroup(String name, KeyStroke accelerator) {
        super(name, accelerator);
    }

    public CommonActionGroup(String name, Icon icon) {
        super(name, icon);
    }

    public CommonActionGroup(String name, Icon icon, KeyStroke accelerator) {
        super(name, icon, accelerator);
    }

    public CommonActionGroup(String name, boolean enabled) {
        super(name, enabled);
    }

    public CommonActionGroup(String name, KeyStroke accelerator, boolean enabled) {
        super(name, accelerator, enabled);
    }

    public CommonActionGroup(String name, Icon icon, boolean enabled) {
        super(name, icon, enabled);
    }

    public CommonActionGroup(String name, Icon icon, KeyStroke accelerator, boolean enabled) {
        super(name, icon, accelerator, enabled);
    }

    @Override
    protected void init() {
        setDefaultAction(null);
        setFlat(false);
    }

    @Override
    public void addActionGroupListener(ActionGroupListener listener) {
        emitter.addListener(listener);
    }

    @Override
    public void removeActionGroupListener(ActionGroupListener listener) {
        emitter.removeListener(listener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( getDefaultAction() != null && getDefaultAction().isEnabled() ) {
            getDefaultAction().actionPerformed(e);
        }
    }

    @Override
    public void addAction(Action action) {
        addAction(actions.size(), action);
    }

    @Override
    public void addAction(int index, Action action) {
        actions.add(index, action);
        if ( !emitter.isEmpty() ) {
            emitter.emitter().actionAdded(new ActionGroupEvent(this, action, index));
        }
    }

    @Override
    public void removeAction(Action action) {
        int index = actions.indexOf(action);
        if ( index >= 0 ) {
            removeActionAt(index);
        }
    }

    @Override
    public Action removeActionAt(int index) {
        Action action = actions.remove(index);
        if ( !emitter.isEmpty() ) {
            emitter.emitter().actionAdded(new ActionGroupEvent(this, action, actions.size() - 1));
        }
        return action;
    }

    @Override
    public int getActionCount() {
        return actions.size();
    }

    @Override
    public Action getActionAt(int index) {
        return actions.get(index);
    }

    @Override
    public int indexOfAction(Action action) {
        return actions.indexOf(action);
    }

    @Override
    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    @Override
    public Action getDefaultAction() {
        return (Action)getValue("defaultAction");
    }

    @Override
    public void setDefaultAction(Action defaultAction) {
        putValue("defaultAction", defaultAction);
    }

    @Override
    public boolean isFlat() {
        return (Boolean)getValue("flat");
    }

    @Override
    public void setFlat(boolean flat) {
        putValue("flat", flat);
    }

}
