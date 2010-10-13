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

package ch.raffael.util.swing.components.browser;

import java.awt.Component;

import javax.swing.Action;

import org.jetbrains.annotations.NotNull;

import ch.raffael.util.beans.EventEmitter;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Page {

    final EventEmitter<PageListener> pageEvents = EventEmitter.newEmitter(PageListener.class);

    private String title;
    private String description;
    private Component component;
    private Action toolBarActions;
    private boolean milestone;
    private Browser browser;

    public Page() {
        this("", "", null);
    }

    public Page(Component component) {
        this("", "", component);
    }

    public Page(String title) {
        this(title, "", null);
    }

    public Page(String title, Component component) {
        this(title, "", component);
    }

    public Page(String title, String description) {
        this(title, description, null);
    }

    public Page(String title, String description, Component component) {
        this.title = title;
        this.description = description;
        this.component = component;
    }

    public void addPageListener(@NotNull PageListener listener) {
        pageEvents.addListener(listener);
    }

    public void removePageListener(@NotNull PageListener listener) {
        pageEvents.removeListener(listener);
    }

    @NotNull
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if ( title == null ) {
            title = "";
        }
        this.title = title;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if ( description == null ) {
            description = "";
        }
        this.description = description;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Action getToolBarActions() {
        return toolBarActions;
    }

    public void setToolBarActions(Action toolBarActions) {
        this.toolBarActions = toolBarActions;
    }

    public boolean isMilestone() {
        return milestone;
    }

    public void setMilestone(boolean milestone) {
        this.milestone = milestone;
    }

    public Browser getBrowser() {
        return browser;
    }

    void setBrowser(Browser browser) {
        this.browser = browser;
    }

    void pageEntered(Browser browser) {
        if ( !pageEvents.isEmpty() ) {
            pageEvents.emitter().pageEntered(new PageEvent(this, browser));
        }
    }

    void pageLeaving(Browser browser) throws VetoException {
        if ( !pageEvents.isEmpty() ) {
            pageEvents.emitter().pageLeaving(new PageEvent(this, browser));
        }
    }

    void pageLeft(Browser browser) {
        if ( !pageEvents.isEmpty() ) {
            pageEvents.emitter().pageLeft(new PageEvent(this, browser));
        }
    }

    void pageClosing(Browser browser) throws VetoException {
        if ( !pageEvents.isEmpty() ) {
            pageEvents.emitter().pageClosing(new PageEvent(this, browser));
        }
    }

    void pageClosed(Browser browser) {
        if ( !pageEvents.isEmpty() ) {
            pageEvents.emitter().pageClosed(new PageEvent(this, browser));
        }
    }

}
