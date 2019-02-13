package org.oddjob.arooa.runtime;

import java.util.ArrayList;
import java.util.List;

import org.oddjob.arooa.ArooaConfigurationException;

/**
 * Base class for {@link RuntimeConfiguration}s.
 *
 *
 */
abstract public class AbstractRuntimeConfiguration 
implements RuntimeConfiguration {

	private final List<RuntimeListener> configurationListeners =
			new ArrayList<>();
	
	public void addRuntimeListener(RuntimeListener listener) {
		synchronized (configurationListeners) {
			configurationListeners.add(listener);
		}
	}
	
	public void removeRuntimeListener(RuntimeListener listener) {
		synchronized (configurationListeners) {
			configurationListeners.remove(listener);
		}
	}
	
	protected void fireBeforeInit() 
	throws ArooaConfigurationException {
		
		List<RuntimeListener> copy;
		synchronized (configurationListeners) {
			if (configurationListeners.size() == 0) {
				return;
			}
			copy = new ArrayList<>(
                    configurationListeners);
		}

		RuntimeEvent event = new RuntimeEvent(this);
		
		for (RuntimeListener listener: copy) {
			listener.beforeInit(event);
		}
	}
		
	protected void fireAfterInit() 
	throws ArooaConfigurationException {
		
		List<RuntimeListener> copy;
		synchronized (configurationListeners) {
			if (configurationListeners.size() == 0) {
				return;
			}
			copy = new ArrayList<>(
                    configurationListeners);
		}
		
		RuntimeEvent event = new RuntimeEvent(this);
		for (RuntimeListener listener: copy) {
			listener.afterInit(event);
		}
	}
	
	protected void fireBeforeConfigure() 
	throws ArooaConfigurationException {

		List<RuntimeListener> copy;
		synchronized (configurationListeners) {
			if (configurationListeners.size() == 0) {
				return;
			}
			copy = new ArrayList<>(
                    configurationListeners);
		}
		
		RuntimeEvent event = new RuntimeEvent(this);
		for (RuntimeListener listener: copy) {
			listener.beforeConfigure(event);
		}
	}
	
	protected void fireAfterConfigure() 
	throws ArooaConfigurationException {
		
		List<RuntimeListener> copy;
		synchronized (configurationListeners) {
			if (configurationListeners.size() == 0) {
				return;
			}
			copy = new ArrayList<>(
                    configurationListeners);
		}
		
		RuntimeEvent event = new RuntimeEvent(this);
		for (RuntimeListener listener: copy) {
			listener.afterConfigure(event);
		}
	}
	
	protected void fireBeforeDestroy() 
	throws ArooaConfigurationException {

		List<RuntimeListener> copy;
		synchronized (configurationListeners) {
			if (configurationListeners.size() == 0) {
				return;
			}
			copy = new ArrayList<>(
                    configurationListeners);
		}
		
		RuntimeEvent event = new RuntimeEvent(this);
		for (RuntimeListener listener: copy) {
			listener.beforeDestroy(event);
		}
	}

	protected void fireAfterDestroy() 
	throws ArooaConfigurationException {
		
		List<RuntimeListener> copy;
		synchronized (configurationListeners) {
			if (configurationListeners.size() == 0) {
				return;
			}
			copy = new ArrayList<>(
                    configurationListeners);
		}
		
		RuntimeEvent event = new RuntimeEvent(this);
		for (RuntimeListener listener: copy) {
			listener.afterDestroy(event);
		}
	}
}
