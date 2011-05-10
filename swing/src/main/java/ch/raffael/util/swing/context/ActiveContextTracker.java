package ch.raffael.util.swing.context;

import java.beans.PropertyChangeListener;

import com.google.common.base.Objects;

import ch.raffael.util.beans.Observable;
import ch.raffael.util.beans.ObservableSupport;


/**
 * Utility class to track the active context. The active context may be used e.g. in
 * MDI UIs, where some actions may depend on the active/focused component somewhere down
 * the component tree.
 * <p>
 * An example for this would be the save action: In an MDI UI, this action would reside in
 * the JMenuBar of the frame, but operate within the context of the active window in a
 * JDesktopPane or JTabbedPane. It could, however, also reside in a toolbar within the
 * window or tab itself and always operate in this context. This is, where this class
 * comes to play: The save action looks up an ActiveContextTracker from its event source.
 * The frame has a tracker associated which will be updated by the desktop. Each document
 * within the desktop is also associated with an active context tracker, but in fixed
 * (unchangeable) mode. This way, when invoked from the menu bar, the action will apply
 * the the currently selected window or tab. When invoked from a tool bar from within the
 * window, it will always apply to the context the action was invoked from.
 * <p>
 * TODO: Consider integrating this into the Context API
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ActiveContextTracker implements Observable {

    private final ObservableSupport observableSupport = new ObservableSupport(this);

    private boolean fixed;
    private Context activeContext;

    public ActiveContextTracker() {
        this(null, false);
    }

    public ActiveContextTracker(boolean fixed) {
        this(null, fixed);
    }

    public ActiveContextTracker(Context activeContext) {
        this(activeContext, false);
    }

    public ActiveContextTracker(Context activeContext, boolean fixed) {
        this.fixed = fixed;
        this.activeContext = activeContext;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        observableSupport.removePropertyChangeListener(listener);
    }

    public void setActiveContext(Context activeContext) {
        if ( !Objects.equal(activeContext, this.activeContext) ) {
            if ( fixed && this.activeContext != null ) {
                throw new IllegalStateException("Active context cannot be changed");
            }
            Context oldContext = this.activeContext;
            this.activeContext = activeContext;
            observableSupport.firePropertyChange("activeContext", oldContext, activeContext);
        }
    }

    public Context getActiveContext() {
        return activeContext;
    }

    public boolean isFixed() {
        return fixed;
    }

    public void setFixed(boolean fixed) {
        this.fixed = fixed;
    }
}
