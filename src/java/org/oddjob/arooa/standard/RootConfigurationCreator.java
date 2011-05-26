package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;


class RootConfigurationCreator implements ElementAction<InstanceConfiguration>{

	private final Object root;
	private boolean component;
	
	RootConfigurationCreator(Object root, 
			boolean component) {
		this.root = root;
		this.component = component;
	}
	
	
	public InstanceConfiguration onElement(ArooaElement element,
			ArooaContext context) {
		
		ArooaSession session = context.getSession();
	
		PropertyAccessor accessor = session.getTools().getPropertyAccessor();

		ArooaClass arooaClass = accessor.getClassName(root);
		
		if (component) {
			
			Object proxy = null;
			
			ComponentProxyResolver proxyResolver = session.getComponentProxyResolver();

			if (proxyResolver != null) {
				proxy = proxyResolver.resolve(root, context);
			}
					
			if (proxy == null) {
				proxy = root;
			}
			
			ComponentConfiguration instance = new ComponentConfiguration(
					arooaClass,
					root, 
					proxy, 
					element.getAttributes());
			
			return instance;
		}
		else {
			InstanceConfiguration instance = new ObjectConfiguration(
					arooaClass,
					root, 
					element.getAttributes());
			
			return instance;
			
		}
		
	}
}
