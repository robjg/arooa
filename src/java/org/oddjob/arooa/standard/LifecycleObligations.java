package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.life.ArooaContextAware;
import org.oddjob.arooa.life.ArooaLifeAware;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.life.LifecycleInterfaceFactory;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

/**
 * Package utility class to handle the lifecycle obligations Arooa
 * promises.
 * 
 * @author rob
 *
 */
class LifecycleObligations {

	/**
	 * Honour obligations for both the proxy and the component, if they
	 * are different.
	 * 
	 * @param instanceRuntime
	 * @param context
	 * @return
	 */
	ArooaContext honour(InstanceRuntime instanceRuntime, ArooaContext context) {
		
		Object proxy = instanceRuntime.getInstance().getObjectToSet();
		
		honourObligations(proxy, instanceRuntime, context);
		
		Object object = instanceRuntime.getInstance().getWrappedObject();
		
		if (object != proxy) {
			honourObligations(object, instanceRuntime, context);
		}		
		
		return context;
	}
	
	private void honourObligations(final Object object, 
			RuntimeConfiguration runtime, ArooaContext context) {
		
		final ArooaLifeAware lifecycle;
		if (object instanceof ArooaLifeAware) { 
			lifecycle = (ArooaLifeAware) object;
		}
		else {
			lifecycle = new LifecycleInterfaceFactory().lifeCycleFor(
					object, context.getSession());
		}

		if (lifecycle != null) {
			runtime.addRuntimeListener(
					new RuntimeListener(){
						public void beforeInit(RuntimeEvent event)
								throws ArooaException {
						}
						public void afterInit(RuntimeEvent event)
								throws ArooaException {
							lifecycle.initialised();
						}
						public void beforeConfigure(RuntimeEvent event)
								throws ArooaException {
						}
						public void afterConfigure(RuntimeEvent event)
								throws ArooaException {
							lifecycle.configured();
						}
						public void beforeDestroy(RuntimeEvent event)
								throws ArooaException {
							lifecycle.destroy();
						}
						public void afterDestroy(RuntimeEvent event)
								throws ArooaException {
						}
					});
		}
		
		if (object instanceof ArooaContextAware) {
			((ArooaContextAware) object).setArooaContext(context);
		}
		
		if (object instanceof ArooaSessionAware) {
			((ArooaSessionAware) object).setArooaSession(
					context.getSession());
		}
		
	}
}
