package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaParser;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.RootContext;

/**
 * Parser a fragment of a configuration. The topmost element is a factory
 * element for either a value or a component.
 * 
 * @author rob
 *
 */
public class StandardFragmentParser implements ArooaParser {

	private final ArooaSession session;

	private Object result;

	private ArooaType type = ArooaType.VALUE;
	
	private InstanceRuntime rootRuntime;
	
	public StandardFragmentParser(ArooaSession session) {
		if (session == null) {
			throw new NullPointerException("Session must not be null.");
		}
		
		this.session = session;
	}

	public StandardFragmentParser(ArooaDescriptor descriptor) {
		this(new StandardArooaSession(descriptor));
	}

	public StandardFragmentParser() {
		this(new StandardArooaSession());
	}
	
	public ConfigurationHandle parse(ArooaConfiguration configuration)
			throws ArooaParseException {

		ArooaHandler handler = new ArooaHandler() {
			public ArooaContext onStartElement(ArooaElement element,
					ArooaContext parentContext) 
			throws ArooaConfigurationException {

				final InstanceConfiguration instanceConfiguration;
				
				if (type == ArooaType.COMPONENT) {
					instanceConfiguration = new ComponentConfigurationCreator().onElement(
							element, parentContext);
				}
				else {
					instanceConfiguration = new ValueConfigurationCreator().onElement(
							element, parentContext);
				}
				
				rootRuntime = new RootRuntime(instanceConfiguration, parentContext);
				
	    		InstanceConfigurationNode node = new InstanceConfigurationNode(
	    				element, rootRuntime);
	    		
				ArooaContext ourContext = new StandardArooaContext(
						type, rootRuntime, node, parentContext);				
				
				rootRuntime.setContext(ourContext);
				
				return rootRuntime.getContext();
			}
		};

		RootContext rootContext = new RootContext(
				type, session, handler);

		ConfigurationHandle handle = null;
		
		handle = configuration.parse(rootContext);
		
		if (rootRuntime != null) {
			rootRuntime.configure();
		}
		
		return handle;
	}

	public Object getRoot() {
		return result;
	}

	public ArooaSession getSession() {
		return session;
	}
	
	class RootRuntime extends InstanceRuntime {

		RootRuntime(InstanceConfiguration item, ArooaContext parentContext) {
			super(item, parentContext);
		}

		@Override
		public void configure() throws ArooaConfigurationException {
			if (type == ArooaType.COMPONENT) {
				getInstanceConfiguration().configure(
						this,
						getContext());
			}
			else {
				getInstanceConfiguration().listenerConfigure(
						RootRuntime.this, 
						getContext());				
			}
		}
		
		ParentPropertySetter getParentPropertySetter() {
			return new ParentPropertySetter() {
				public void parentSetProperty(Object value) {
					result = value;
				}
			};
		}
	}

	public ArooaType getArooaType() {
		return type;
	}
	
	public void setArooaType(ArooaType type) {
		this.type = type;
	}
	
}
