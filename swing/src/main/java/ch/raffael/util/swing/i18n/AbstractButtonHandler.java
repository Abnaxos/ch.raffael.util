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

package ch.raffael.util.swing.i18n;

import javax.swing.AbstractButton;

import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.swing.util.Mnemonic;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class AbstractButtonHandler extends AbstractComponentHandler<AbstractButton> {

    public AbstractButtonHandler() {
        super(AbstractButton.class);
    }

    @Override
    protected void doLocalize(AbstractButton component, StringLocalizer localizer, ResourceBundle resources) {
        if ( localizer.shouldLocalize(component.getText()) ) {
            Mnemonic m = new Mnemonic(localizer.localize(component.getText(), resources));
            component.setText(m.getLabel());
            if ( m.getMnemonic() != null ) {
                component.setMnemonic(m.getMnemonic());
            }
        }
    }

}
