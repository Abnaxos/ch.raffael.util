/*
 * Copyright 2010 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.raffael.util.swing.context;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

import org.slf4j.Logger;

import com.google.common.collect.MapMaker;
import com.jidesoft.swing.JideSplitButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import ch.raffael.util.common.logging.LogUtil;
import ch.raffael.util.swing.DefaultWindowPlacementManager;
import ch.raffael.util.swing.SwingUtil;
import ch.raffael.util.swing.WindowPlacementManager;
import ch.raffael.util.swing.actions.ActionPresenter;
import ch.raffael.util.swing.actions.DefaultActionPresenter;
import ch.raffael.util.swing.actions.JideSplitButtonPresentationBuilder;
import ch.raffael.util.swing.actions.MenuPresentationBuilder;
import ch.raffael.util.swing.actions.PopupMenuPresentationBuilder;
import ch.raffael.util.swing.actions.ToolBarPresentationBuilder;
import ch.raffael.util.swing.components.sidepanel.SideMenuBuilder;
import ch.raffael.util.swing.components.sidepanel.SidePanel;
import ch.raffael.util.swing.tasks.DefaultTaskTracker;
import ch.raffael.util.swing.tasks.TaskTracker;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ContextManager {

    @SuppressWarnings("UnusedDeclaration")
    private static final Logger log = LogUtil.getLogger();

    private final static ContextManager INSTANCE = new ContextManager();

    private final Map<Component, Context> mappings =
            new MapMaker().concurrencyLevel(1).weakKeys().makeMap();
    private final Context root = new DefaultContext(true);

    private ContextManager() {
        setupRootContext();
    }

    private void setupRootContext() {
        root.put(WindowPlacementManager.class, new DefaultWindowPlacementManager());
        DefaultActionPresenter presenter = new DefaultActionPresenter();
        presenter.setBuilder(JMenu.class, MenuPresentationBuilder.class);
        presenter.setBuilder(JPopupMenu.class, PopupMenuPresentationBuilder.class);
        if ( SwingUtil.isJideAvailable() ) {
            presenter.setBuilder(JToolBar.class, ch.raffael.util.swing.actions.JideToolBarPresentationBuilder.class);
            presenter.setBuilder(JideSplitButton.class, JideSplitButtonPresentationBuilder.class);
            presenter.setBuilder(SidePanel.class, SideMenuBuilder.class);
        }
        else {
            presenter.setBuilder(JToolBar.class, ToolBarPresentationBuilder.class);
        }
        root.put(ActionPresenter.class, presenter);
        root.put(TaskTracker.class, new DefaultTaskTracker());
        root.put(Map.class, DefaultContext.INJECTOR_CACHE_KEY, new HashMap());
        root.put(InjectionProvider.class, Logger.class, new LoggerInjectionProvider());
    }

    public static ContextManager getInstance() {
        return INSTANCE;
    }

    public static Context context(@NotNull Component component) {
        return getInstance().require(component);
    }

    @NotNull
    public Context getRoot() {
        return root;
    }

    public synchronized void map(@NotNull Context context, @NotNull Component component) {
        mappings.put(component, context);
    }

    public synchronized void unmap(@NotNull Component component) {
        mappings.remove(component);
    }

    @Nullable
    public synchronized Context get(@NotNull Component component) {
        return mappings.get(component);
    }

    @SuppressWarnings({ "ConstantConditions" })
    @Nullable
    public synchronized Context find(@NotNull Component component) {
        Context context;
        do {
            context = mappings.get(component);
            component = component.getParent();
        }
        while ( context == null && component != null );
        return context;
    }

    @NotNull
    public synchronized Context require(@NotNull Component component) {
        Context context = find(component);
        if ( context == null ) {
            throw new ContextException("No context found for component " + component);
        }
        return context;
    }

    @SuppressWarnings({ "ConstantConditions" })
    public synchronized Mapping findMapping(@NotNull Component component) {
        while ( true ) {
            Context context = mappings.get(component);
            if ( context != null ) {
                return new Mapping(context, component);
            }
            component = component.getParent();
            if ( component == null ) {
                return null;
            }
        }
    }

    public static class Mapping {
        private final Context context;
        private final Component component;
        public Mapping(@NotNull Context context, @NotNull Component component) {
            this.context = context;
            this.component = component;
        }
        @Override
        public boolean equals(Object o) {
            if ( this == o ) {
                return true;
            }
            if ( o == null || getClass() != o.getClass() ) {
                return false;
            }
            Mapping that = (Mapping)o;
            if ( !component.equals(that.component) ) {
                return false;
            }
            return context.equals(that.context);
        }
        @Override
        public int hashCode() {
            int result = context.hashCode();
            result = 31 * result + component.hashCode();
            return result;
        }
        @Override
        public String toString() {
            return "Mapping{" +
                    "context=" + context +
                    ", component=" + component +
                    '}';
        }
        public Context getContext() {
            return context;
        }
        public Component getComponent() {
            return component;
        }
    }

}
