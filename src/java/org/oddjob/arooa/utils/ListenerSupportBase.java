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
 * @author rob
 *
 * @param <T> The type of the listener.
 */
public class ListenerSupportBase<T> {

	private final List<T> listeners =
		new ArrayList<T>();
	
	private Runnable onFirst;
	
	private Runnable onEmpty;
	
	protected List<T> copy() {
		synchronized (listeners) {
			return new ArrayList<T>(listeners);
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

	public void setOnFirst(Runnable onFirst) {
		this.onFirst = onFirst;
	}

	public Runnable getOnEmpty() {
		return onEmpty;
	}

	public void setOnEmpty(Runnable onEmpty) {
		this.onEmpty = onEmpty;
	}
}
