package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaSession;


public class SessionOverrideContext extends OverrideContext {

	private final ArooaSession sessionOverride;

	public SessionOverrideContext(ArooaContext context, 
			ArooaSession sessionOverride) {
		super(context);
		this.sessionOverride = sessionOverride;
	}
	
	@Override
	public ArooaSession getSession() {
		return sessionOverride;
	}
	
}