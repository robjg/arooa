package org.oddjob.arooa.design;

import java.util.HashMap;
import java.util.Map;

import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;

/**
 * Shared implementation for the designs of instances that are components.
 * The common implementation is mainly about notify listeners of 
 * structural changes.
 * 
 * @author rob
 *
 */
abstract public class DesignComponentBase extends DesignInstanceBase
implements DesignComponent {

	/** The child component property if there is one. */
	private DesignElementProperty componentProperty;
	
	/** Track listeners. */
	private final Map<DesignListener, ConfigurationNodeListener> listeners = 
		new HashMap<DesignListener, ConfigurationNodeListener>();
	
	/** Used for lazy initialisation. */
	private boolean initialised;
		
	private String id;
	
	/**
	 * Constructor.
	 * 
	 * @param element
	 * @param parentContext
	 */
	public DesignComponentBase(ArooaElement element, ArooaContext parentContext) {
		this(element, 
				new ClassFinder().forElement(element, parentContext),
			parentContext);		
	}
	
	/**
	 * 
	 * 
	 * @param element
	 * @param classIdentifier
	 * @param parentContext
	 */
	public DesignComponentBase(ArooaElement element,
			ArooaClass classIdentifier, ArooaContext parentContext) {
		super(element, classIdentifier, parentContext);

		ArooaAttributes attributes = element.getAttributes(); 
		id = attributes.get(ArooaConstants.ID_PROPERTY);
	}
	
	/**
	 * Lazily find out if we are the parent of another component or
	 * components. This can't be done at construction because we haven't
	 * had all our child properties set at that point.
	 */
	private void init() {
		if (initialised) {
			return;
		}
				
		for (DesignProperty property: children()) {

			if (! (property instanceof DesignElementProperty)) {
				
				continue;
			}
			
			DesignElementProperty elementProperty = (DesignElementProperty) property;

			if (elementProperty.getArooaContext().getArooaType() 
					== ArooaType.COMPONENT) {
				
				componentProperty = elementProperty;
			}

		}
		
		initialised = true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.DesignComponent#addStructuralListener(org.oddjob.arooa.design.DesignListener)
	 */
	public void addStructuralListener(final DesignListener listener) {
		init();
		
		if (componentProperty == null) {
			return;
		}
		if (listeners.containsKey(listener)) {
			return;
		}
		
		componentProperty.addDesignListener(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.DesignComponent#removeStructuralListener(org.oddjob.arooa.design.DesignListener)
	 */
	public void removeStructuralListener(DesignListener listener) {

		if (componentProperty == null) {
			return;
		}

		componentProperty.removeDesignListener(listener);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
