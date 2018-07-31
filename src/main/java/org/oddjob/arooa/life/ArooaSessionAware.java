package org.oddjob.arooa.life;

import org.oddjob.arooa.ArooaSession;

/**
 * Something that can accept an {@link ArooaSession}.
 * 
 * @author rob
 *
 */
public interface ArooaSessionAware {

	/**
	 * Accept the current Arooa Session.
	 * 
	 * @param session The Arooa Session. Never null.
	 */
	public void setArooaSession(ArooaSession session);
}
