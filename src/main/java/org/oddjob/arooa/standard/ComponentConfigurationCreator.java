package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.ArooaElementException;
import org.oddjob.arooa.life.ComponentPersistException;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * An {@link ElementAction} that produces an {@link InstanceConfiguration} for a
 * Component.
 * 
 * @author rob
 *
 */
class ComponentConfigurationCreator 
implements ElementAction<InstanceConfiguration>{

	public ComponentConfiguration onElement(ArooaElement element,
			ArooaContext parentContext) 
	throws ArooaElementException {

		ArooaSession session = parentContext.getSession();
		
		Object proxy = null;
		Object component = null;
		
		ComponentPersister persister = session.getComponentPersister();
		ComponentProxyResolver proxyResolver = 
				session.getComponentProxyResolver();

		ElementMappings mappings = session.getArooaDescriptor(
				).getElementMappings();
		
		if (mappings == null) {
			throw new NullPointerException(
					"No Component Element Mappings in Descriptor.");
		}

		ArooaClass arooaClass = mappings.mappingFor(
				element, new InstantiationContext(parentContext));
		
		if (arooaClass == null) {
			throw new ArooaElementException(element, "No class definition.");
		}
		
		component = arooaClass.newInstance();
		
		String id = element.getAttributes().get(ArooaConstants.ID_PROPERTY);
		if (persister != null && id != null) {
			
			try {
				proxy = persister.restore(id, 
						component.getClass().getClassLoader(), session);
			} 
			catch (ComponentPersistException e) {
				throw new ArooaElementException(element, "Restore failed.", e);
			}
			
			if (proxy != null && proxyResolver != null) {
				component = proxyResolver.restore(proxy, 
						parentContext.getSession());
			}
			
			if (component == null) {
				component = proxy;
			}
		}
			
		if (proxy == null && proxyResolver != null) {
			proxy = proxyResolver.resolve(component, 
					parentContext.getSession());
		}

		if (proxy == null) {
			proxy = component;
		}
		
		ComponentConfiguration instance = new ComponentConfiguration(
				arooaClass,
				component, 
				proxy, 
				element.getAttributes());
		
		return instance;
	}
}
