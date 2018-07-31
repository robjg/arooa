/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaParser;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.RootContext;
import org.oddjob.arooa.registry.ComponentPool;

/**
 * An {@link ArooaParser} that configures a provided root
 * Object. The root Object is considered to be a component and
 * so may be given an id and is registered with the
 * the {@link ComponentPool}
 * 
 * @author rob
 *
 */
public class StandardArooaParser implements ArooaParser {

	/** The provided root object. */
	private final Object root;

	/** The ArooaSession. Provided or defaulted. */
	private final ArooaSession session;
	
	/** The optional document element to check. */
	private ArooaElement expectedDocumentElement; 
	
	/**
	 * Constructor with a {@link ArooaSession}.
	 * 
	 * @param root The root object. Must not be null.
	 * @param session The ArooaSession. Must not be null.
	 */
	public StandardArooaParser(Object root, ArooaSession session) {
		if (root == null) {
			throw new NullPointerException("Root Object must not be null.");
		}
		if (session == null) {
			throw new NullPointerException("Session must not be null.");
		}
		
		this.session = session;		
		this.root = root;
	}
	
	/**
	 * Constructor with a {@link ArooaDescriptor} that will be
	 * used in a {@link StandardArooaSession}.
	 * 
	 * @param root The root object. Must not be null.
	 * @param descriptor The Descriptor. May be null.
	 */
	public StandardArooaParser(Object root, ArooaDescriptor descriptor) {
		this(root, new StandardArooaSession(descriptor));
	}

	/**
	 * Constructor that will use a {@link StandardArooaSession} with
	 * a {@link StandardArooaDescriptor}.
	 * 
	 * @param root The root object. Must not be null.
	 */
	public StandardArooaParser(Object root) {
		this(root, (ArooaDescriptor) null);
	}
	
	/**
	 * Set the expected document element. If present the document
	 * element of the configuration will be check against this. Otherwise
	 * the document element can be anything.
	 * 
	 * @param exepectedDocumentElement
	 */
	public void setExpectedDocumentElement(ArooaElement exepectedDocumentElement) {
		this.expectedDocumentElement = exepectedDocumentElement;
	}
	
	/**
	 * Getter for the expected document element.
	 * 
	 * @return
	 */
	public ArooaElement getExpectedDocumentElement() {
		return expectedDocumentElement;
	}

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.ArooaParser#parse(org.oddjob.arooa.ArooaConfiguration)
	 */
	public ConfigurationHandle parse(ArooaConfiguration configuration) 
	throws ArooaParseException {

		ElementAction<InstanceRuntime> elementAction = 
			new ElementAction<InstanceRuntime>() {
			public InstanceRuntime onElement(ArooaElement element, ArooaContext parentContext) {
				return new RootRuntime(
						new RootConfigurationCreator(root, true).onElement(element, parentContext),
						parentContext);
			}
		};
		
		RootHandler rootHandler  = new RootHandler(expectedDocumentElement,
					elementAction);

		ConfigurationHandle handle = configuration.parse(
				new RootContext( 
						ArooaType.COMPONENT, session, rootHandler));
		return handle;
	}
	
	/**
	 * Get the {@link ArooaSession} used. This will either
	 * be as provided or the one created.
	 * 
	 * @return The ArooaSession. Never null.
	 */
	public ArooaSession getSession() {
		return session;
	}
}
