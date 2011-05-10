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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.raffael.util.beans.EventEmitter;
import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class CommonToggleActionGroup extends CommonActionGroup implements ToggleActionGroup {

    private static final ToggleAction[] EMPTY_SELECTION = new ToggleAction[0];

    private final EventEmitter<ItemListener> itemEvents = EventEmitter.newEmitter(ItemListener.class);
    private boolean adjusting = false;
    private final ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            if ( isMutuallyExclusive() && ((ToggleAction)e.getSource()).isSelected() ) {
                for ( Action a : getActions() ) {
                    if ( a!=e.getSource()) {
                        if ( a instanceof ToggleAction && ((ToggleAction)a).isSelected() ) {
                            ((ToggleAction)a).setSelected(false);
                        }
                    }
                }
            }
            if ( !adjusting && !itemEvents.isEmpty() ) {
                int stateChange = ((ToggleAction)e.getSource()).isSelected() ? ItemEvent.SELECTED : ItemEvent.DESELECTED;
                itemEvents.emitter().itemStateChanged(new ItemEvent(CommonToggleActionGroup.this, 0,
                                                                    e.getSource(), stateChange));
            }
        }
    };

    public CommonToggleActionGroup(String name) {
        super(name);
    }

    public CommonToggleActionGroup(String name, KeyStroke accelerator) {
        super(name, accelerator);
    }

    public CommonToggleActionGroup(String name, Icon icon) {
        super(name, icon);
    }

    public CommonToggleActionGroup(String name, Icon icon, KeyStroke accelerator) {
        super(name, icon, accelerator);
    }

    public CommonToggleActionGroup(String name, boolean enabled) {
        super(name, enabled);
    }

    public CommonToggleActionGroup(String name, KeyStroke accelerator, boolean enabled) {
        super(name, accelerator, enabled);
    }

    public CommonToggleActionGroup(String name, Icon icon, boolean enabled) {
        super(name, icon, enabled);
    }

    public CommonToggleActionGroup(String name, Icon icon, KeyStroke accelerator, boolean enabled) {
        super(name, icon, accelerator, enabled);
    }

    public CommonToggleActionGroup(ResourceBundle resources, String baseName) {
        super(resources, baseName);
    }

    public CommonToggleActionGroup(ResourceBundle resources, String baseName, boolean enabled) {
        super(resources, baseName, enabled);
    }

    @Override
    protected void init() {
        super.init();
        if ( getValue(MUTUALLY_EXCLUSIVE_PROPERTY) == null ) {
            setMutuallyExclusive(true);
        }
        addActionGroupListener(new ActionGroupListener() {
            @Override
            public void actionAdded(ActionGroupEvent evt) {
                if ( evt.getAction() instanceof ToggleAction && ((ToggleAction)evt.getAction()).isSelected() ) {
                    ((ToggleAction)evt.getAction()).setSelected(false);
                }
            }
            @Override
            public void actionRemoved(ActionGroupEvent evt) {
            }
        });
    }

    @Override
    public boolean isMutuallyExclusive() {
        return (Boolean)getValue(MUTUALLY_EXCLUSIVE_PROPERTY);
    }

    @Override
    public void setMutuallyExclusive(boolean mutuallyExclusive) {
        putValue(MUTUALLY_EXCLUSIVE_PROPERTY, mutuallyExclusive);
    }

    @Override
    public ToggleAction[] getSelectedObjects() {
        List<ToggleAction> selected = null;
        for ( Action a : getActions() ) {
            if ( a instanceof ToggleAction && ((ToggleAction)a).isSelected() ) {
                if ( selected == null ) {
                    selected = new ArrayList<ToggleAction>();
                }
                selected.add((ToggleAction)a);
            }
        }
        if ( selected != null ) {
            return selected.toArray(new ToggleAction[selected.size()]);
        }
        else {
            return EMPTY_SELECTION;
        }
    }

    @Override
    public void addItemListener(ItemListener l) {
        itemEvents.addListener(l);
    }

    @Override
    public void removeItemListener(ItemListener l) {
        itemEvents.removeListener(l);
    }
}
