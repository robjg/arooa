package org.oddjob.arooa.deploy;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.life.SimpleArooaClass;

public class BeanDefinitionTest extends Assert {

	public static class Apple {
		
		public void setDescription(String text) {
			
		}
	}
	
   @Test
	public void testIsBeanDescriptor() {
		
		BeanDefinition test = new BeanDefinition();
		test.setClassName(Apple.class.getName());
		test.setElement("apple");
		test.setDesignFactory("AppleDesignFactory");
		
		assertFalse(test.isArooaBeanDescriptor());
	}
	
   @Test
	public void testPropertyDefinition() {
		
		BeanDefinition test = new BeanDefinition();				
		test.setClassName(Apple.class.getName());

		test.setProperties(0, 
				new PropertyDefinition("description", 
						PropertyDefinition.PropertyType.TEXT));
				
		PropertyDefinitionsHelper defs = new PropertyDefinitionsHelper(
				new SimpleArooaClass(Object.class));
		defs.mergeFromBeanDefinition(test);
		
		BeanDescriptorHelper sort = new BeanDescriptorHelper(defs);
		
		assertEquals(ConfiguredHow.TEXT, 
				sort.getConfiguredHow("description"));
	}
	
}
