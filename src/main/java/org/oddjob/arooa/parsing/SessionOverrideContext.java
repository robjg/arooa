package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * An {@link ArooaContext} that provides a new session but delegate all other methods
 * to an existing context.
 */
public class SessionOverrideContext extends OverrideContext<ArooaContext> implements ArooaContext {

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

	@Override
	public RuntimeConfiguration getRuntime() {
		return getExistingContext().getRuntime();
	}

	@Override
	public ArooaHandler getArooaHandler() {
		return getExistingContext().getArooaHandler();
	}
}
