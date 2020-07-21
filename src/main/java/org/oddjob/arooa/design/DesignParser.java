/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design;

import org.oddjob.arooa.*;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.RootContext;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.standard.StandardArooaSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a configuration to create a {@link DesignInstance}.
 * <p>
 * The Design is created using a {@link DesignFactory}. If one
 * is not provided a default factory is used which will first check
 * the {@link ElementMappings} of the sessions {@link ArooaDescriptor}
 * for a one. If none exists it will provide one that will dynamically 
 * create a design from the properties of the class as derived from
 * the element and the mappings.
 * 
 * @author rob
 */
public class DesignParser implements ArooaParser, DesignNotifier {
	
	/** Listeners to notify when the root design instance changes. */
	private final List<DesignListener> listeners =
			new ArrayList<>();
	
	/** The session to use during the parse. */
	private final ArooaSession session;

	/** The factory used to create the design. */
	private final DesignFactory factory;

	/** The design instance created. May change. */
	private DesignInstance design;
	
	/** Is this a Component design. */
	private ArooaType type = ArooaType.VALUE;

	/** The expected document element. */
	private ArooaElement expectedDocumentElement;
	
	/**
	 * Default Constructor. Creates a DesignParser with a 
	 * {@link StandardArooaSession}, and a default factory.
	 */
	public DesignParser() {
		this(null, null);
	}

	/**
	 * Create a DesignParser with the given session.
	 * 
	 * @param existingSession A session. If null the standard 
	 * will be used.
	 */
	public DesignParser(ArooaSession existingSession) {
		this(existingSession, null);
	}
	
	/**
	 * Create a DesignParser for the standard session
	 * and given factory.
	 * 
	 * @param factory The factory.
	 */
	public DesignParser(DesignFactory factory) {
		this(null, factory);
	}
	
	/**
	 * Create a DesignParser with the given session and factory.
	 * 
	 * @param existingSession A Session. If null the standard will be used.
	 * @param factory A factory. If null a default will be used.
	 */
	public DesignParser(ArooaSession existingSession, DesignFactory factory) {
		if (existingSession == null) {
			this.session = new StandardArooaSession();
		}
		else {
			this.session = existingSession;
		}
		
		if (factory == null) {
			this.factory = new DescriptorDesignFactory();
		}
		else {
			this.factory = factory;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.DesignNotifier#addDesignListener(org.oddjob.arooa.design.DesignListener)
	 */
	public void addDesignListener(DesignListener listener) {
		synchronized (listeners) {
			listener.childAdded(
					new DesignStructureEvent(this, design, 0));
			listeners.add(listener);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.DesignNotifier#removeDesignListener(org.oddjob.arooa.design.DesignListener)
	 */
	public void removeDesignListener(DesignListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	/**
	 * Used to change the Design of document element. Notifies listeners
	 * accordingly.
	 * 
	 * @param design
	 */
	private void setDesign(DesignInstance design) {
		if (design == null) {
			// Should be impossible.
			throw new IllegalStateException("Null design.");
		}
		synchronized (listeners) {
			if (this.design != null) {
				
				this.design = null;
				
				for (DesignListener listener : listeners) {
					listener.childRemoved(
							new DesignStructureEvent(this, null, 0));
				}
			}

			this.design = design;
			
			for (DesignListener listener : listeners) {
				listener.childAdded(
						new DesignStructureEvent(this, design, 0));
			}				
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.ArooaParser#parse(org.oddjob.arooa.ArooaConfiguration)
	 */
	public ConfigurationHandle<ArooaContext> parse(ArooaConfiguration configuration) throws ArooaParseException {
		
		if (configuration == null) {
			throw new NullPointerException("No configuration.");
		}
		if (type == null) {
			throw new NullPointerException("No ArooaType.");
		}
		
		ArooaHandler handler = new XMLFirstHandler() {
			
			@Override
			DesignInstance goodDesign(ArooaElement element,
					ArooaContext parentContext)
			throws ArooaPropertyException {
				
				if (expectedDocumentElement != null &&
						!expectedDocumentElement.equals(element)) {
					throw new ArooaException("Wrong document element " + 
							element + ", expected " + expectedDocumentElement);
				}

				DesignInstance design = factory.createDesign(element, parentContext);
				
				if (design == null) {
					throw new NullPointerException("No Design For [" + 
							element + "].");
				}

				return design;
			}
			
			@Override
			void setDesign(int index, DesignInstance design) {
				if (index != 0) {
					throw new IllegalStateException("Only one document element allowed.");
				}
				
				DesignParser.this.setDesign(design);
			}
		};
		
		ArooaContext rootContext = new RootContext(
				type, session, handler);
		
		return configuration.parse(rootContext);
	}

	/**
	 * Getter for the current {@link DesignInstance}.
	 *  
	 * @return
	 */
	public DesignInstance getDesign() {
		return design;
	}
		
	/**
	 * Is this the design of a Value or a Component?
	 * 
	 * @return
	 */
	public ArooaType getArooaType() {
		return type;
	}

	/**
	 * Set if this is the design of a Value or a Component.
	 * 
	 * @param type The type. Component or Value Design.
	 */
	public void setArooaType(ArooaType type) {
		this.type = type;
	}

	/**
	 * Getter for the expected document element.
	 * 
	 * @return The expected ArooaElement. Null if it 
	 * can be anything.
	 */
	public ArooaElement getExpectedDocumentElement() {
		return expectedDocumentElement;
	}

	/**
	 * Setter for expected document element.
	 * 
	 * @param element The expected document element. Can be
	 * null if element can be anything.
	 */
	public void setExpectedDocumentElement(ArooaElement element) {
		this.expectedDocumentElement = element;
	}
	
}
