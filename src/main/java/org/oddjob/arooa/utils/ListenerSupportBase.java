package org.oddjob.arooa.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * A Helper Class for tracking Listeners. Allows commands to
 * be run on the first listener and when there are no more listeners.
 * This is useful when the events are being notified in a chain and
 * there is no need to subscribe when nothing is listening. For instance
 * in the Oddjob JMX handlers.
 *
 * @param <T> The type of the listener.
 * @author rob
 */
public class ListenerSupportBase<T> {

    private final List<T> listeners =
            new ArrayList<>();

    private Runnable onFirst;

    private Runnable onEmpty;

    protected List<T> copy() {
        synchronized (listeners) {
            return new ArrayList<>(listeners);
        }

    }

    protected void addListener(T listener) {
        synchronized (listeners) {
            if (listeners.isEmpty() && onFirst != null) {
                onFirst.run();
            }
            listeners.add(listener);
        }
    }

    protected void removeListener(T listener) {
        synchronized (listeners) {
            listeners.remove(listener);
            if (listeners.isEmpty() && onEmpty != null) {
                onEmpty.run();
            }
        }
    }

    public Runnable getOnFirst() {
        return onFirst;
    }

    /**
     * Set an action to run when the first listener
     * is added.
     *
     * @param onFirst
     */
    public void setOnFirst(Runnable onFirst) {
        this.onFirst = onFirst;
    }

    public Runnable getOnEmpty() {
        return onEmpty;
    }

    /**
     * Set an action to be run when the last listener has
     * been removed.
     *
     * @param onEmpty
     */
    public void setOnEmpty(Runnable onEmpty) {
        this.onEmpty = onEmpty;
    }

    /**
     * Remove listeners on run {@link #onEmpty}.
     */
    public void destroy() {
        synchronized (listeners) {
            if (!listeners.isEmpty()) {
                listeners.clear();
                onEmpty.run();
            }
        }
    }
}
