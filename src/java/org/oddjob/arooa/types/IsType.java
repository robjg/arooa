package org.oddjob.arooa.types;

import java.lang.reflect.Constructor;

import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

/**
 * @oddjob.description Create an Object that is the class of the
 * property. The properties class must have a public zero argument
 * constructor.
 * 
 * @oddjob.example Using <code>is</code> to set a simple property.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/IsSimple.xml}
 * 
 * Where the <code>snack</code> bean is:
 * 
 * {@oddjob.java.resource org/oddjob/arooa/types/IsTypeTest.java#simpleBean}
 * 
 * and the <code>fruit</code> bean is:
 * 
 * {@oddjob.java.resource org/oddjob/arooa/types/IsTypeTest.java#fruitBean}
 * 
 * @oddjob.example Using <code>is</code> to set an indexed property.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/IsIndexed.xml}
 * 
 * Where the <code>snack</code> bean is:
 * 
 * {@oddjob.java.resource org/oddjob/arooa/types/IsTypeTest.java#indexedBean}
 * 
 * and the <code>fruit</code> bean is as above.
 * 
 * @oddjob.example Using <code>is</code> to set a mapped property.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/IsMapped.xml}
 * 
 * Where the <code>snack</code> bean is:
 * 
 * {@oddjob.java.resource org/oddjob/arooa/types/IsTypeTest.java#mappedBean}
 * 
 * and the <code>fruit</code> bean is as above.
 * 
 * 
 * @author rob
 *
 */
public class IsType {

	public static final ArooaElement ELEMENT = new ArooaElement("is"); 
		
	public static boolean supports(InstantiationContext context) {
		
		ArooaClass classId  = context.getArooaClass();
		
		if (classId == null) {
			return false;
		}
		
		if (! (classId instanceof SimpleArooaClass)) {
			return true;
		}
		
		Class<?> cl = ((SimpleArooaClass) classId).forClass();

		try {
			Constructor<?> beanConstructor = cl.getConstructor(new Class<?>[0]);
			beanConstructor.newInstance();
		} catch (Exception e) {
			return false;
		}

		return true;		
	}
	
	
}
