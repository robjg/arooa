package org.oddjob.arooa.utils;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.parsing.SessionDelegate;
import org.oddjob.arooa.runtime.PropertyManager;
import org.oddjob.arooa.standard.StandardPropertyManager;

/**
 * A version of an {@link ArooaSession} that creates it's own copy
 * of a {@link PropertyManager}.
 * 
 * @author rob
 *
 */
public class PropertiesOverrideSession extends SessionDelegate
implements ArooaSession {

	private final PropertyManager propertyManager;
	
	/**
	 * Constructor.
	 * 
	 * @param original The original session.
	 */
	public PropertiesOverrideSession(ArooaSession original) {
		super(original);
		
		this.propertyManager = new StandardPropertyManager(
				original.getPropertyManager());
	}
		
	@Override
	public PropertyManager getPropertyManager() {
		return propertyManager;
	}		
		
}
