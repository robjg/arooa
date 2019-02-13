package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.runtime.AbstractRuntimeConfiguration;

import java.util.Objects;

/**
 * Base class for all {@link org.oddjob.arooa.runtime.RuntimeConfiguration}s
 * that are the result of the Standard Parser.
 *
 * @see StandardArooaParser
 * @see ContainerRuntime
 * @see InstanceRuntime
 */
abstract class StandardRuntime extends AbstractRuntimeConfiguration {

    /** The parent context. */
	private final ArooaContext parentContext;

	/** The context that owns this runtime. Due to the chicken and egg nature of
     * contexts and runtimes, this can only be set once the context has been
     * created with a reference to this runtime. */
	private ArooaContext context;

    /**
     * Constructor.
     *
     * @param parentContext The parent context.
     */
	StandardRuntime(ArooaContext parentContext) {

        Objects.requireNonNull(parentContext);

		this.parentContext = parentContext;
	}
	
	protected void fireBeforeInit() 
	throws ArooaConfigurationException {
		super.fireBeforeInit();
	}
		
	protected void fireAfterInit() 
	throws ArooaConfigurationException {
		super.fireAfterInit();
	}
	
	protected void fireBeforeConfigure() 
	throws ArooaConfigurationException {
		super.fireBeforeConfigure();
	}
	
	protected void fireAfterConfigure() 
	throws ArooaConfigurationException {
		super.fireAfterConfigure();
	}
	
	protected void fireBeforeDestroy() 
	throws ArooaConfigurationException {
		super.fireBeforeDestroy();
	}

	protected void fireAfterDestroy() 
	throws ArooaConfigurationException {
		super.fireAfterDestroy();
	}

	abstract ArooaHandler getHandler();
		
	void setContext(ArooaContext context) 
	throws ArooaConfigurationException {
		this.context = context;
	}
	
	ArooaContext getContext() {
		return context;
	}

	ArooaContext getParentContext() {
		return parentContext;
	}	
}
