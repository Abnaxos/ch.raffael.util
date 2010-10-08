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

import java.awt.Component;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Mnemonic {

    private static Pattern MNEMONIC_RE = Pattern.compile("(?<!&)&(?!&)(.)");
    private static Pattern ESCAPED_MNEMONIC_RE = Pattern.compile("&(&+)");

    private final String label;
    private final Character mnemonic;

    public Mnemonic(String label) {
        String mnemonic = null;
        Matcher m = MNEMONIC_RE.matcher(label);
        if ( m.find() ) {
            mnemonic = m.group(1);
            label = m.replaceAll("$1");
        }
        m = ESCAPED_MNEMONIC_RE.matcher(label);
        this.label = m.replaceAll("$1");
        if ( mnemonic != null ) {
            this.mnemonic = Character.toUpperCase(mnemonic.charAt(0));
        }
        else {
            this.mnemonic = null;
        }
    }

    public String getLabel() {
        return label;
    }

    public Character getMnemonic() {
        return mnemonic;
    }

    public static JLabel boundLabel(JLabel label, Component comp) {
        Mnemonic m = new Mnemonic(label.getText());
        label.setText(m.getLabel());
        if ( m.getMnemonic() != null ) {
            label.setDisplayedMnemonic(m.getMnemonic());
        }
        label.setLabelFor(comp);
        return label;
    }

}