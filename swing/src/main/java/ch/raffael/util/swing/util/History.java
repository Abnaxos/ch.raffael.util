package ch.raffael.util.swing.util;

import ch.raffael.util.beans.EventEmitter;

import static com.google.common.base.Preconditions.*;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class History<T> {

    private final EventEmitter<HistoryListener> historyEvents = EventEmitter.newEmitter(HistoryListener.class);
    private Entry<T> current;

    public void addHistoryListener(HistoryListener listener) {
        historyEvents.addListener(listener);
    }

    public void removeHistoryListener(HistoryListener listener) {
        historyEvents.removeListener(listener);
    }

    public T getCurrent() {
        return current != null ? current.object : null;
    }

    public boolean hasPrevious() {
        return current != null && current.prev != null;
    }

    public boolean hasNext() {
        return current != null && current.next != null;
    }

    public void add(T object) {
        checkNotNull(object, "object");
        if ( current == null || !current.object.equals(object) ) {
            current = new Entry<T>(current, object);
            historyEvents.emitter().historyChanged(new HistoryEvent(this));
        }
    }

    public void clear() {
        if ( current != null ) {
            current = null;
            historyEvents.emitter().historyChanged(new HistoryEvent(this));
        }
    }

    public void back() {
        back(1);
    }

    public void back(int count) {
        if ( !hasPrevious() || count <= 0 ) {
            return;
        }
        for ( int i = 0; i < count; i++ ) {
            if ( current.prev == null ) {
                return;
            }
            current = current.prev;
        }
        historyEvents.emitter().historyChanged(new HistoryEvent(this));
    }

    public void forward() {
        forward(1);
    }

    public void forward(int count) {
        if ( !hasNext() || count <= 0 ) {
            return;
        }
        for ( int i = 0; i < count; i++ ) {
            if ( current.next == null ) {
                return;
            }
            current = current.next;
        }
        historyEvents.emitter().historyChanged(new HistoryEvent(this));
    }

    private static class Entry<T> {
        private Entry<T> prev;
        private Entry<T> next = null;
        private final T object;

        private Entry(Entry<T> prev, T object) {
            this.prev = prev;
            this.object = object;
            if ( prev != null ) {
                prev.next = this;
            }
        }
    }

}
