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

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import ch.raffael.util.swing.util.Mnemonic;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public abstract class CommonAction extends AbstractAction {

    protected CommonAction(String name) {
        super();
        setName(name);
        init();
    }

    protected CommonAction(String name, KeyStroke accelerator) {
        super();
        setName(name);
        putValue(ACCELERATOR_KEY, accelerator);
        init();
    }

    protected CommonAction(String name, Icon icon) {
        setName(name);
        putValue(SMALL_ICON, icon);
        init();
    }

    protected CommonAction(String name, Icon icon, KeyStroke accelerator) {
        setName(name);
        putValue(SMALL_ICON, icon);
        putValue(ACCELERATOR_KEY, accelerator);
        init();
    }

    protected CommonAction(String name, boolean enabled) {
        super();
        setName(name);
        setEnabled(enabled);
        init();
    }

    protected CommonAction(String name, KeyStroke accelerator, boolean enabled) {
        super();
        setName(name);
        putValue(ACCELERATOR_KEY, accelerator);
        setEnabled(enabled);
        init();
    }

    protected CommonAction(String name, Icon icon, boolean enabled) {
        setName(name);
        putValue(SMALL_ICON, icon);
        setEnabled(enabled);
        init();
    }

    protected CommonAction(String name, Icon icon, KeyStroke accelerator, boolean enabled) {
        setName(name);
        putValue(SMALL_ICON, icon);
        putValue(ACCELERATOR_KEY, accelerator);
        setEnabled(enabled);
        init();
    }

    /**
     * Gives an opportunity to init some stuff in anonymous inner classesl.
     */
    protected void init() {
    }

    private void setName(String name) {
        Mnemonic m = new Mnemonic(name);
        putValue(NAME, m.getLabel());
        if ( m.getMnemonic() != null ) {
            char c = m.getMnemonic();
            if ( c >= 'A' && c <= 'Z' ) {
                putValue(MNEMONIC_KEY, (int)c);
            }
        }
    }
}
