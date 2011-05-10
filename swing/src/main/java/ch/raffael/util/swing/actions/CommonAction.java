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

import java.awt.Image;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import ch.raffael.util.i18n.MethodSignature;
import ch.raffael.util.i18n.ResourceBundle;
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

    protected CommonAction(ResourceBundle resources, String baseName) {
        this(resources, baseName, true);
    }

    protected CommonAction(ResourceBundle resources, String baseName, boolean enabled) {
        this((String)resources.meta().resource(baseName).get());
        Set<MethodSignature> methods = resources.meta().methods();
        MethodSignature iconSig = find(methods, baseName + "Icon");
        if ( iconSig != null ) {
            if ( Icon.class.isAssignableFrom(iconSig.getReturnType()) ) {
                putValue(SMALL_ICON, resources.meta().resource(baseName + "Icon").get());
            }
            else if ( Image.class.isAssignableFrom(iconSig.getReturnType()) ) {
                putValue(SMALL_ICON, new ImageIcon((Image)resources.meta().resource(baseName + "Icon").get()));
            }
        }
        MethodSignature keySig = find(methods, baseName + "Key");
        if ( keySig != null && KeyStroke.class.isAssignableFrom(keySig.getReturnType()) ) {
            putValue(ACCELERATOR_KEY, resources.meta().resource(baseName + "Key").get());
        }
    }

    private static MethodSignature find(Set<MethodSignature> sigs, String name) {
        for ( MethodSignature sig : sigs ) {
            if ( sig.getName().equals(name) && sig.getArguments().isEmpty() ) {
                return sig;
            }
        }
        return null;
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
