package org.oddjob.arooa.parsing;

import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class RuntimeOverrideContext extends OverrideContext {

	private final RuntimeConfiguration runtime;
	
	public RuntimeOverrideContext(ArooaContext context,
			RuntimeConfiguration runtime) {
		super(context);
		this.runtime = runtime;
	}
	
	@Override
	public RuntimeConfiguration getRuntime() {
		return runtime;
	}
}
