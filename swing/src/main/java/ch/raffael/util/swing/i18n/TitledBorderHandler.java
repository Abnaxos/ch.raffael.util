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

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import ch.raffael.util.i18n.ResourceBundle;
import ch.raffael.util.swing.util.Mnemonic;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TitledBorderHandler extends AbstractComponentHandler<JComponent> {

    public TitledBorderHandler() {
        super(JComponent.class);
    }

    @Override
    protected void doLocalize(JComponent component, StringLocalizer localizer, ResourceBundle bundle) {
        if ( component.getBorder() != null ) {
            handleBorder(component.getBorder(), localizer, bundle);
        }
    }

    protected void handleBorder(Border border, StringLocalizer localizer, ResourceBundle bundle) {
        if ( border instanceof TitledBorder ) {
            TitledBorder tb = (TitledBorder)border;
            tb.setTitle(new Mnemonic(localizer.localize(tb.getTitle(), bundle)).getLabel());
        }
        else if ( border instanceof CompoundBorder ) {
            CompoundBorder cb = (CompoundBorder)border;
            handleBorder(cb.getOutsideBorder(), localizer, bundle);
            handleBorder(cb.getInsideBorder(), localizer, bundle);
        }
    }

}
