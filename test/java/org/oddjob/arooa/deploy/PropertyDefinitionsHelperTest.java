package org.oddjob.arooa.deploy;

import junit.framework.TestCase;

import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;

public class PropertyDefinitionsHelperTest extends TestCase {

	public void testSameProperties() {

		BeanDefinition def1 = new BeanDefinition();
		def1.setProperties(0,
				new PropertyDefinition("apple", 
						PropertyDefinition.PropertyType.ELEMENT));
		
		BeanDefinition def2 = new BeanDefinition();
		def2.setProperties(0,
				new PropertyDefinition("apple", 
						PropertyDefinition.PropertyType.ATTRIBUTE));
		
		PropertyDefinitionsHelper test = new PropertyDefinitionsHelper();
		test.mergeFromBeanDefinition(def1);
		test.mergeFromBeanDefinition(def2);

		
		assertEquals(ConfiguredHow.ATTRIBUTE, test.getConfiguredHow("apple"));
		
	}
	
	public static class Apple {
		
		public void setRotten(boolean rotten) {
			
		}
	}

	public void testWithBeanDefinition() {
		
		BeanDefinition definition = new BeanDefinition();				
		definition.setClassName(Apple.class.getName());

		PropertyDefinitionsHelper test = 
			new DefaultBeanDescriptorProvider(
					).getBeanDescriptor(
						new SimpleArooaClass(Apple.class), 
						new BeanUtilsPropertyAccessor());

		test.mergeFromBeanDefinition(definition);
		
		BeanDescriptorHelper sort = new BeanDescriptorHelper(test);
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				sort.getConfiguredHow("rotten"));
	}
}
