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

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import ch.raffael.util.i18n.ResourceBundle;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class GUILocalizer {

    public static final StringLocalizer DEFAULT_STRING_LOCALIZER = new DefaultStringLocalizer("##");
    public static final MultiComponentHandler SWING = new MultiComponentHandler(
            new JLabelHandler(),
            new AbstractButtonHandler(),
            new JTextComponentHandler(),
            new JComboBoxHandler(),
            new JTabbedPaneHandler(),
            new TitledBorderHandler());

    private final ResourceBundle resources;
    private StringLocalizer stringLocalizer = DEFAULT_STRING_LOCALIZER;
    private List<ComponentHandler> handlers = new LinkedList<ComponentHandler>();

    public GUILocalizer(ResourceBundle resources) {
        this(resources, SWING);
    }

    public GUILocalizer(ResourceBundle resources, ComponentHandler... handlers) {
        this(resources, DEFAULT_STRING_LOCALIZER, handlers);
    }

    public GUILocalizer(ResourceBundle resources, Collection<ComponentHandler> handlers) {
        this(resources, DEFAULT_STRING_LOCALIZER, handlers);
    }

    public GUILocalizer(ResourceBundle resources, StringLocalizer stringLocalizer, ComponentHandler... handlers) {
        this.resources = resources;
        this.stringLocalizer = stringLocalizer;
        this.handlers = new ArrayList<ComponentHandler>(Arrays.asList(handlers));
    }

    public GUILocalizer(ResourceBundle resources, StringLocalizer stringLocalizer, Collection<ComponentHandler> handlers) {
        this.resources = resources;
        this.stringLocalizer = stringLocalizer;
        this.handlers = new LinkedList<ComponentHandler>();
    }

    public ResourceBundle getResources() {
        return resources;
    }

    public StringLocalizer getStringLocalizer() {
        return stringLocalizer;
    }

    public void setStringLocalizer(StringLocalizer stringLocalizer) {
        this.stringLocalizer = stringLocalizer;
    }

    public List<ComponentHandler> getHandlers() {
        return handlers;
    }

    public void localize(Component component) {
        for ( ComponentHandler h : handlers ) {
            h.localize(component, stringLocalizer, resources);
        }
        if ( component instanceof Container ) {
            Container container = (Container)component;
            for ( int i = 0; i < container.getComponentCount(); i++ ) {
                localize(container.getComponent(i));
            }
        }
    }

}
