package org.oddjob.arooa.xml;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaParser;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.RootContext;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.runtime.PropertyManager;

/**
 * An {@link ArooaParser} that parses an {@link ArooaConfiguration}
 * into an XML string.
 * <p>
 * Once the {@link #parse(ArooaConfiguration)} method has been
 * called the XML is available using the {@link #getXml()} 
 * method.
 * 
 * @author rob
 *
 */
public class XMLArooaParser implements ArooaParser {

	private XmlHandler2 handler; 
		
	public ConfigurationHandle parse(ArooaConfiguration configuration) throws ArooaParseException {

		handler = new XmlHandler2();
		
		RootContext context = new RootContext(
				null,
				new XMLParserSession(), 
				handler);
		
		return configuration.parse(context);

	}
	
	public String getXml() {
		if (handler == null) {
			return null;
		}
		return handler.getXml();
	}
}

class XMLParserSession implements ArooaSession {
	
	@Override
	public ArooaDescriptor getArooaDescriptor() {
		throw new UnsupportedOperationException("Not required for XMLParser.");
	}
	
	@Override
	public ComponentPersister getComponentPersister() {
		throw new UnsupportedOperationException("Not required for XMLParser.");
	}
	
	@Override
	public ComponentPool getComponentPool() {
		return new ComponentPool() {

			@Override
			public void configure(Object component) {
				throw new UnsupportedOperationException("Not required for XMLParser.");
			}

			@Override
			public ArooaContext contextFor(Object component) {
				throw new UnsupportedOperationException("Not required for XMLParser.");
			}

			@Override
			public String getIdFor(Object either) {
				throw new UnsupportedOperationException("Not required for XMLParser.");
			}
			
			@Override
			public Iterable<ComponentTrinity> allTrinities() {
				throw new UnsupportedOperationException("Not required for XMLParser.");
			}
			
			@Override
			public ComponentTrinity trinityForId(String id) {
				throw new UnsupportedOperationException("Not required for XMLParser.");
			}
			
			@Override
			public void registerComponent(ComponentTrinity trinity, String id) {
				throw new UnsupportedOperationException("Not required for XMLParser.");
			}

			@Override
			public void remove(Object component) {
				throw new UnsupportedOperationException("Not required for XMLParser.");
			}

			@Override
			public void save(Object component) {
				throw new UnsupportedOperationException("Not required for XMLParser.");
			}
		};
	}

	@Override
	public BeanRegistry getBeanRegistry() {
		throw new UnsupportedOperationException("Not required for XMLParser.");
	}
	
	@Override
	public PropertyManager getPropertyManager() {
		throw new UnsupportedOperationException("Not required for XMLParser.");
	}
	
	@Override
	public ComponentProxyResolver getComponentProxyResolver() {
		throw new UnsupportedOperationException("Not required for XMLParser.");
	}
	
	@Override
	public ArooaTools getTools() {
		throw new UnsupportedOperationException("Not required for XMLParser.");
	}
}
