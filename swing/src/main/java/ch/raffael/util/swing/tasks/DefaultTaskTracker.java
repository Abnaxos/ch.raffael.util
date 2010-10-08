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

package ch.raffael.util.swing.tasks;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.WeakHashMap;

import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DefaultTaskTracker implements TaskTracker {
    // FIXME: handle LONG_BLOCKING (=> progress meter)

    public static final MouseListener EMPTY_MOUSE_LISTENER = new MouseAdapter(){};
    public static final KeyListener EMPTY_KEY_LISTENER = new KeyAdapter(){};

    private int blockingLevel;
    private WeakHashMap<Component, Object> knownGlassPanes = new WeakHashMap<Component, Object>();
    private final Timer waitCursorTimer;

    public DefaultTaskTracker() {
        waitCursorTimer = new Timer(250, new ActionListener() {
            private final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
            @Override
            public void actionPerformed(ActionEvent e) {
                for ( Window window : Window.getWindows() ) {
                    if ( window instanceof RootPaneContainer ) {
                        Component glassPane = ((RootPaneContainer)window).getGlassPane();
                        if ( !knownGlassPanes.containsKey(glassPane) ) {
                            glassPane.addMouseListener(EMPTY_MOUSE_LISTENER);
                            glassPane.addKeyListener(EMPTY_KEY_LISTENER);
                        }
                        glassPane.setCursor(waitCursor);
                        glassPane.setVisible(true);
                    }
                }
            }
        });
        waitCursorTimer.setRepeats(false);
    }

    @Override
    public void execute(Task task) {
        execute(task, null);
    }

    @Override
    public void execute(final Task task, final Component component) {
        if ( !SwingUtilities.isEventDispatchThread() ) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    execute(task, component);
                }
            });
            return;
        }
        if ( task.getType().isBlocking() ) {
            if ( blockingLevel == 0 ) {
                blockingLevel++;
                block(component);
            }
        }
        task.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ( evt.getPropertyName().equals("state") ) {
                    SwingWorker.StateValue state = (SwingWorker.StateValue)evt.getNewValue();
                    if ( state == SwingWorker.StateValue.DONE ) {
                        task.removePropertyChangeListener(this);
                        blockingLevel--;
                        if ( blockingLevel == 0 ) {
                            unblock(component);
                        }
                    }
                }
            }
        });
        task.execute();
    }

    public int getWaitCursorTimeout() {
        return waitCursorTimer.getDelay();
    }

    public void setWaitCursorTimeout(int waitCursorTimeout) {
        waitCursorTimer.setDelay(waitCursorTimeout);
        waitCursorTimer.setInitialDelay(waitCursorTimeout);
    }

    protected void block(Component component) {
        for ( Window window : Window.getWindows() ) {
            if ( window instanceof RootPaneContainer ) {
                Component glassPane = ((RootPaneContainer)window).getGlassPane();
                if ( !knownGlassPanes.containsKey(glassPane) ) {
                    glassPane.addMouseListener(EMPTY_MOUSE_LISTENER);
                    glassPane.addKeyListener(EMPTY_KEY_LISTENER);
                    knownGlassPanes.put(glassPane, this);
                }
                glassPane.setVisible(true);
            }
        }
        waitCursorTimer.start();
    }

    protected void unblock(Component component) {
        waitCursorTimer.stop();
        for ( Window window : Window.getWindows() ) {
            if ( window instanceof RootPaneContainer ) {
                Component glassPane = ((RootPaneContainer)window).getGlassPane();
                glassPane.setCursor(Cursor.getDefaultCursor());
                glassPane.setVisible(false);
            }
        }
    }

}
