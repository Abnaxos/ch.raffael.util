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

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.raffael.util.beans.EventEmitter;
import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class CommonToggleAction extends CommonAction implements ToggleAction {

    protected final EventEmitter<ChangeListener> changeEvents = EventEmitter.newEmitter(ChangeListener.class);

    public CommonToggleAction(String name) {
        super(name);
        setSelected(false);
    }

    public CommonToggleAction(String name, KeyStroke accelerator) {
        super(name, accelerator);
    }

    public CommonToggleAction(String name, Icon icon) {
        super(name, icon);
    }

    public CommonToggleAction(String name, Icon icon, KeyStroke accelerator) {
        super(name, icon, accelerator);
    }

    public CommonToggleAction(String name, boolean enabled) {
        super(name, enabled);
    }

    public CommonToggleAction(String name, KeyStroke accelerator, boolean enabled) {
        super(name, accelerator, enabled);
    }

    public CommonToggleAction(String name, Icon icon, boolean enabled) {
        super(name, icon, enabled);
    }

    public CommonToggleAction(String name, Icon icon, KeyStroke accelerator, boolean enabled) {
        super(name, icon, accelerator, enabled);
    }

    protected CommonToggleAction(ResourceBundle resources, String baseName) {
        super(resources, baseName);
    }

    protected CommonToggleAction(ResourceBundle resources, String baseName, boolean enabled) {
        super(resources, baseName, enabled);
    }

    @Override
    protected void init() {
        super.init();
        if ( getValue(SELECTED_PROPERTY) == null ) {
            setSelected(false);
        }
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
        changeEvents.addListener(changeListener);
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
        changeEvents.removeListener(changeListener);
    }

    @Override
    public boolean isSelected() {
        return (Boolean)getValue(SELECTED_PROPERTY);
    }

    @Override
    public void setSelected(boolean selected) {
        putValue(SELECTED_PROPERTY, selected);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ( isSelected() ) {
            deselecting(e);
        }
        else {
            selecting(e);
        }
        setSelected(!isSelected());
        if ( !changeEvents.isEmpty() ) {
            changeEvents.emitter().stateChanged(new ChangeEvent(this));
        }
    }

    protected abstract void selecting(ActionEvent e);
    protected abstract void deselecting(ActionEvent e);

}
