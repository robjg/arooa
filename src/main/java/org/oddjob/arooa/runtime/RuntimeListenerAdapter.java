package org.oddjob.arooa.runtime;

import org.oddjob.arooa.ArooaException;

/**
 * Provide empty methods for {@link RuntimeListener}s.
 * 
 * @author rob
 *
 */
public class RuntimeListenerAdapter implements RuntimeListener {

	public void afterConfigure(RuntimeEvent event) throws ArooaException {
	}

	public void afterDestroy(RuntimeEvent event) throws ArooaException {
	}

	public void afterInit(RuntimeEvent event) throws ArooaException {
	}

	public void beforeConfigure(RuntimeEvent event) throws ArooaException {
	}

	public void beforeDestroy(RuntimeEvent event) throws ArooaException {
	}

	public void beforeInit(RuntimeEvent event) throws ArooaException {
	}

}
