package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.etc.UnknownComponent;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

/**
 * @oddjob.description Create an Object of the given class. The class
 * is specified with the class attribute. If no class is specified a
 * java.lang.Object is created. 
 * <p>
 * The class must be a true Java Bean, and
 * have a no argument public constructor. 
 * <p>
 * Properties of the bean are 
 * attributes for the eight Java primitive types and their associated Objects, 
 * or a String, and elements for all other types, as is the Oddjob standard.
 * 
 * @oddjob.example Creating a bean.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/BeanExample.xml}
 * 
 * Where the bean is:
 * 
 * {@oddjob.java.resource org/oddjob/arooa/types/PersonBean.java}
 * 
 * @author rob
 *
 */
public class BeanType {

	public static final ArooaElement ELEMENT = new ArooaElement("bean"); 
	
	/** 
	 * @oddjob.property class 
	 * @oddjob.description The class to create. Must have a public zero
	 * argument constructor.
	 * @oddjob.required No, defaults to java.lang.Object.
	 */
	public static final String ATTRIBUTE = "class"; 
		
	public static class ClassDesignFactory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, 
				ArooaContext parentContext) {
			
			if (parentContext.getArooaType() == ArooaType.COMPONENT) {
				return new UnknownComponent(element, parentContext);
			}
			else {
				return new UnknownInstance(element, parentContext);		
			}
		}
	};

}
