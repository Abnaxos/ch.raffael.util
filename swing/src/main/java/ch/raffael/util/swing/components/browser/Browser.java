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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;

import ch.raffael.util.beans.EventEmitter;
import ch.raffael.util.common.logging.LogUtil;
import ch.raffael.util.i18n.I18N;
import ch.raffael.util.swing.actions.ActionPresenter;
import ch.raffael.util.swing.actions.CommonAction;
import ch.raffael.util.swing.actions.PresentationBuilder;
import ch.raffael.util.swing.context.Context;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Browser extends JPanel {

    private final Logger log = LogUtil.getLogger(this);
    private final BrowserResources res = getResources();

    private final EventEmitter<BrowserListener> browserEvents = EventEmitter.newEmitter(BrowserListener.class);
    private final EventEmitter<ChangeListener> changeEvents = EventEmitter.newEmitter(ChangeListener.class);
    private final EventEmitter<PageListener> pageEvents = EventEmitter.newEmitter(PageListener.class);

    private final PageListener pageEventBroadcaster = new PageListener() {
        @Override
        public void pageEntered(PageEvent evt) {
            pageEvents.emitter().pageEntered(evt);
        }
        @Override
        public void pageLeaving(PageEvent evt) throws VetoException {
            pageEvents.emitter().pageLeaving(evt);
        }
        @Override
        public void pageLeft(PageEvent evt) {
            pageEvents.emitter().pageLeft(evt);
        }
        @Override
        public void pageClosing(PageEvent evt) throws VetoException {
            pageEvents.emitter().pageClosing(evt);
        }
        @Override
        public void pageClosed(PageEvent evt) {
            pageEvents.emitter().pageClosed(evt);
        }
    };
    
    private final Context context;

    private final List<Page> pages = new ArrayList<Page>();
    private int activeIndex = 0;
    private boolean closeable = true;
    private boolean milestoneNavigationEnabled = false;

    private final JToolBar toolBar = new JToolBar(JToolBar.HORIZONTAL);
    private Component currentComponent;

    private final CommonAction homeAction = new CommonAction(res.home(), res.homeIcon()) {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if ( !pages.isEmpty() ) {
                try {
                    setActiveIndex(0);
                }
                catch ( VetoException e ) {
                    log.debug("Home vetoed", e);
                }
            }
        }
    };
    private final CommonAction backAction = new CommonAction(res.back(), res.backIcon()) {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if ( activeIndex > 0 ) {
                try {
                    setActiveIndex(activeIndex - 1);
                }
                catch ( VetoException e ) {
                    log.debug("Back vetoed", e);
                }
            }
        }
    };
    private final CommonAction forwardAction = new CommonAction(res.forward(), res.forwardIcon()) {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if ( activeIndex >= 0 && activeIndex < pages.size() - 1 ) {
                try {
                    setActiveIndex(activeIndex + 1);
                }
                catch ( VetoException e ) {
                    log.debug("Forward vetoed", e);
                }
            }
        }
    };
    private final CommonAction backMilestoneAction = new CommonAction(res.backMilestone(), res.backMilestoneIcon()) {
        @Override
        public void actionPerformed(ActionEvent evt) {
            int i;
            for ( i = activeIndex - 1; i >= 0; i-- ) {
                if ( pages.get(i).isMilestone() ) {
                    break;
                }
            }
            if ( i < 0 ) {
                i = 0;
            }
            try {
                setActiveIndex(i);
            }
            catch ( VetoException e ) {
                log.debug("Back milestone vetoed", e);
            }
        }
    };
    private final CommonAction forwardMilestoneAction = new CommonAction(res.forwardMilestone(), res.forwardMilestoneIcon()) {
        @Override
        public void actionPerformed(ActionEvent evt) {
            int i;
            for ( i = activeIndex + 1; i < pages.size(); i++ ) {
                if ( pages.get(i).isMilestone() ) {
                    break;
                }
            }
            if ( i > pages.size() - 1 ) {
                i = pages.size() - 1;
            }
            try {
                setActiveIndex(i);
            }
            catch ( VetoException e ) {
                log.debug("Forward milestone vetoed", e);
            }
        }
    };
    private final CommonAction closeAction = new CommonAction(res.close(), res.closeIcon()) {
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                close();
            }
            catch ( VetoException e ) {
                log.debug("Close vetoed", e);
            }
        }
    };

    public Browser(Context context) {
        this(context, null);
    }

    public Browser(Context context, Page homePage) {
        super(new BorderLayout());
        this.context = context;
        toolBar.setFloatable(false);
        add(toolBar, BorderLayout.NORTH);
        if ( homePage != null ) {
            pages.add(homePage);
        }
        updateBrowser();
    }

    protected BrowserResources getResources() {
        return I18N.getBundle(BrowserResources.class);
    }

    public void addBrowserListener(BrowserListener listener) {
        browserEvents.addListener(listener);
    }
    
    public void removeBrowserListener(BrowserListener listener) {
        browserEvents.removeListener(listener);
    }

    public void addChangeListener(ChangeListener listener) {
        changeEvents.addListener(listener);
    }

    public void addPageListener(PageListener listener) {
        pageEvents.addListener(listener);
    }

    public void removePageListener(PageListener listener) {
        pageEvents.removeListener(listener);
    }
    
    public void removeChangeListener(ChangeListener listener) {
        changeEvents.removeListener(listener);
    }

    public boolean isCloseable() {
        return closeable;
    }

    public void setCloseable(boolean closeable) {
        if ( this.closeable != closeable ) {
            this.closeable = closeable;
            updateActions();
        }
    }

    public boolean isMilestoneNavigationEnabled() {
        return milestoneNavigationEnabled;
    }

    public void setMilestoneNavigationEnabled(boolean milestoneNavigationEnabled) {
        if ( this.milestoneNavigationEnabled != milestoneNavigationEnabled ) {
            this.milestoneNavigationEnabled = milestoneNavigationEnabled;
            updateActions();
        }
    }

    public JToolBar getToolBar() {
        return toolBar;
    }

    public List<Page> getPages() {
        return Collections.unmodifiableList(pages);
    }

    public int getPageCount() {
        return pages.size();
    }

    public int indexOf(Page page) {
        return pages.indexOf(page);
    }

    public Page getActivePage() {
        if ( activeIndex >= 0 ) {
            return pages.get(activeIndex);
        }
        else {
            return null;
        }
    }

    public void setActivePage(Page page) throws VetoException {
        int index = pages.indexOf(page);
        if ( index < 0 ) {
            throw new IllegalArgumentException("Page " + page + " not in browser");
        }
        setActiveIndex(index);
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) throws VetoException {
        if ( activeIndex == this.activeIndex ) {
            return;
        }
        Page currentPage = pages.get(this.activeIndex);
        Page newPage = pages.get(activeIndex);
        currentPage.pageLeaving(this);
        currentPage.pageLeft(this);
        this.activeIndex = activeIndex;
        updateBrowser();
        newPage.pageEntered(this);
    }

    public void goTo(Page page) throws VetoException {
        int index = pages.indexOf(page);
        if ( index >= 0 && index <= activeIndex ) {
            throw new IllegalArgumentException("Duplicate page: " + page);
        }
        if ( page.getBrowser() != null && page.getBrowser() != this ) {
            throw new IllegalStateException("Page " + page + " is already in browser " + page.getBrowser());
        }
        closeToActive(false);
        pages.add(page);
        page.setBrowser(this);
        page.addPageListener(pageEventBroadcaster);
        activeIndex++;
        updateBrowser();
        page.pageEntered(this);
    }

    public void closeToActive() throws VetoException {
        closeToActive(true);
    }

    private void closeToActive(boolean update) throws VetoException {
        for ( int i = pages.size() - 1; i > activeIndex; i-- ) {
            pages.get(i).pageClosing(this);
        }
        for ( int i = pages.size() - 1; i > activeIndex; i-- ) {
            close(i);
        }
        if ( update ) {
            updateBrowser();
        }
    }

    private void close(int index) {
        Page p = pages.remove(index);
        p.pageClosed(this);
        p.removePageListener(pageEventBroadcaster);
        p.setBrowser(null);
    }

    public void close() throws VetoException {
        if ( pages.isEmpty() ) {
            return;
        }
        pages.get(activeIndex).pageLeaving(this);
        for ( int i = pages.size() - 1; i >= 0; i-- ) {
            pages.get(i).pageClosing(this);
        }
        pages.get(activeIndex).pageLeft(this);
        for ( int i = pages.size() - 1; i >= 0; i-- ) {
            Page p = pages.remove(i);
            p.pageClosed(this);
            p.setBrowser(null);
        }
        updateBrowser();
    }

    public void updateBrowser() {
        if ( currentComponent != null ) {
            remove(currentComponent);
        }
        while ( toolBar.getComponentCount() > 0 ) {
            toolBar.remove(0);
        }
        updateActions();
        if ( pages.isEmpty() ) {
            activeIndex = -1;
            currentComponent = null;
            revalidate();
            repaint();
            changeEvents.emitter().stateChanged(new ChangeEvent(this));
            browserEvents.emitter().browserClosed(new BrowserEvent(this));
        }
        else {
            currentComponent = pages.get(activeIndex).getComponent();
            if ( currentComponent != null ) {
                add(currentComponent, BorderLayout.CENTER);
            }
            homeAction.setEnabled(activeIndex > 0);
            backAction.setEnabled(activeIndex > 0);
            backMilestoneAction.setEnabled(activeIndex > 0);
            forwardAction.setEnabled(activeIndex < pages.size() - 1);
            forwardMilestoneAction.setEnabled(activeIndex < pages.size() - 1);
            revalidate();
            repaint();
            changeEvents.emitter().stateChanged(new ChangeEvent(this));
        }
    }

    protected void updateActions() {
        PresentationBuilder<JToolBar> builder = context.require(ActionPresenter.class).builder(JToolBar.class).init(toolBar);
        builder.add(homeAction);
        if ( milestoneNavigationEnabled ) {
            builder.add(backMilestoneAction);
        }
        builder.add(backAction);
        builder.add(forwardAction);
        if ( milestoneNavigationEnabled ) {
            builder.add(forwardMilestoneAction);
        }
        if ( !pages.isEmpty() ) {
            Action pageActions = pages.get(activeIndex).getToolBarActions();
            if ( pageActions != null ) {
                builder.separator();
                builder.addFlat(pageActions);
            }
        }
        if ( isCloseable() ) {
            builder.getTarget().add(Box.createHorizontalGlue());
            builder.add(closeAction);
        }
        builder.build();
        toolBar.revalidate();
        toolBar.repaint();
    }

}
