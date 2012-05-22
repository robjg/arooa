package org.oddjob.arooa.deploy;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;

public class BeanDefinitionsTest extends TestCase {

	public interface Fruit {
		
	}
	
	public static class Apple implements Fruit {
		
	}
	
	public void testIsBeanDefinition() {
		
		BeanDefinition beanDef = new BeanDefinition();
		beanDef.setClassName(Apple.class.getName());
		beanDef.setElement("apple");
	
		ArooaDescriptorBean factory = new ArooaDescriptorBean();
		factory.setValues(0, beanDef);
		
		ArooaDescriptor descriptor = factory.createDescriptor(
				getClass().getClassLoader());
		
		ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
				new SimpleArooaClass(Apple.class), 
				new BeanUtilsPropertyAccessor());

		assertNotNull(beanDescriptor);
		
		beanDef.setProperties(
				0, new PropertyDefinition(
						"description", 
						PropertyDefinition.PropertyType.TEXT));
		
		descriptor = factory.createDescriptor(
				getClass().getClassLoader());
		
		beanDescriptor = descriptor.getBeanDescriptor(
				new SimpleArooaClass(Apple.class), 
				new BeanUtilsPropertyAccessor());
		
		BeanDescriptorHelper sort = new BeanDescriptorHelper(beanDescriptor);
		
		assertEquals("description", sort.getTextProperty());
	}	
}
