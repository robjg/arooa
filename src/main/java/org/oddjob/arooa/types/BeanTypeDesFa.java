package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaConstants;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignInstanceContext;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DynamicDesignInstance;
import org.oddjob.arooa.design.GenericDesignFactory;
import org.oddjob.arooa.design.InstanceSupport;
import org.oddjob.arooa.design.ParsableDesignInstance;
import org.oddjob.arooa.design.SimpleTextAttribute;
import org.oddjob.arooa.design.screem.BeanForm;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * A {@link DesignFactory} for {@link BeanType}.
 * 
 * @author rob
 *
 */
public class BeanTypeDesFa  implements DesignFactory {
	
	public DesignInstance createDesign(
			ArooaElement element, 
			ArooaContext parentContext) {
		
		if (parentContext.getArooaType() == ArooaType.COMPONENT) {
			return new BeanComponentDesign(element, parentContext);
		}
		else {
			return new BeanDesign(element, parentContext);		
		}
	}
}

/**
 * The design for a bean as a component. Not that this does not support
 * structural beans (beans that have component properties).
 */
class BeanComponentDesign extends BeanDesign implements DesignComponent {

	private String id;
	
	public BeanComponentDesign(ArooaElement element, 
			ArooaContext parentContext) {
		super(element, parentContext);
		ArooaAttributes attributes = element.getAttributes(); 
		id = attributes.get(ArooaConstants.ID_PROPERTY);
	}
	
	@Override
	public void addStructuralListener(DesignListener listener) {
	}

	@Override
	public void removeStructuralListener(DesignListener listener) {
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
}

/**
 * The design for a bean as a value.
 */
class BeanDesign implements ParsableDesignInstance, DynamicDesignInstance {
	
	private final SimpleTextAttribute className;
	
	private final ArooaElement element;
	
	private final ArooaContext parentContext;
	
	private DesignProperty[] beanProperties;
	
	private DesignProperty[] children;
	
	private final ArooaContext arooaContext;
	
	/**
	 * Constructor.
	 * 
	 * @param element
	 * @param parentContext
	 */
	public BeanDesign(final ArooaElement element, final ArooaContext parentContext) {

		this.element = element;
		this.parentContext = parentContext;
		
		className = new SimpleTextAttribute("class", this);
		
		arooaContext = new DesignInstanceContext(this, 
				new SimpleArooaClass(Object.class), parentContext);
		
		changeClassName(className.attribute());
	}

	public String getClassName() {
		return className.attribute();
	}
	
	public void setClassName(String className) {
		if (className == null && this.className.attribute() == null
			|| className.equals(this.className.attribute())) {
			return;
		}
		this.className.attribute(className);
		changeClassName(className);
	}
	
	private void changeClassName(String className) {
		
		if (className == null || className.trim().length() == 0) {
			beanProperties = null;
		}
		else {			
			ClassResolver resolver = parentContext.getSession(
					).getArooaDescriptor().getClassResolver();
			Class<?> cl = resolver.findClass(className);

			if (cl == null) {
				beanProperties = null;
			}
			else {
				ArooaClass arooaClass = new SimpleArooaClass(cl);
				beanProperties = new GenericDesignFactory(
						arooaClass).designProperties(
								this);
				children = 
						new DesignProperty[beanProperties.length + 1];
				children[0] = this.className;
				System.arraycopy(beanProperties, 0, children, 1, 
						beanProperties.length);
			}
		} 
		if (beanProperties == null) {
			children = new DesignProperty[] { this.className };
		}		
	}
	
	@Override
	public Form detail() {
		return new BeanForm(this);
	}
	
	@Override
	public DesignProperty[] getBeanProperties() {
		return beanProperties;
	}
	
	@Override
	public ArooaElement element() {
		return element;
	}
	
	@Override
	public ArooaContext getArooaContext() {
		if (arooaContext == null) {
			throw new NullPointerException();
		}
		return arooaContext;
	}
	
	@Override
	public DesignProperty[] children() {
		return children;
	}
	
	@Override
	public String toString() {
		return InstanceSupport.tagFor(this).toString();
	}
}

