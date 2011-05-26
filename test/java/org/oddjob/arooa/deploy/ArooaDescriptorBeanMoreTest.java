package org.oddjob.arooa.deploy;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.deploy.PropertyDefinition.PropertyType;
import org.oddjob.arooa.life.SimpleArooaClass;

public class ArooaDescriptorBeanMoreTest extends TestCase {

	public static class OurBean {
		
		public void setFruit(String fruit) {
		}
	}
	
	public void testEmptyBeanDescriptor() {
		
		ArooaDescriptorBean test = new ArooaDescriptorBean();
		
		ArooaDescriptor descriptor = test.createDescriptor(getClass().getClassLoader());
		
		assertNotNull(descriptor.getElementMappings());
		
		ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
				new SimpleArooaClass(OurBean.class), null);
		
		assertNull(beanDescriptor);
	}
	
	public void testNoClassName() {
		ArooaDescriptorBean test = new ArooaDescriptorBean();

		ArooaDescriptor descriptor = test.createDescriptor(
				getClass().getClassLoader());
		
		assertNotNull(descriptor.getElementMappings());
	}
	
	public void testBeanDescriptorQuery() {
		
		ArooaDescriptorBean test = new ArooaDescriptorBean();

		BeanDefinition def = new BeanDefinition();
		def.setClassName(OurBean.class.getName());
		def.setElement("snack");
		
		PropertyDefinition prop = new PropertyDefinition(
				"stuff", PropertyType.COMPONENT);
		
		def.setProperties(0, prop);
		
		test.setComponents(0, def);
		
		ArooaDescriptor descriptor = test.createDescriptor(
				getClass().getClassLoader());
		
		ArooaBeanDescriptor beanDescriptor =
			descriptor.getBeanDescriptor(
					new SimpleArooaClass(OurBean.class),
					new BeanUtilsPropertyAccessor());
		
		assertNotNull(beanDescriptor);
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				beanDescriptor.getConfiguredHow("fruit"));
		
		assertEquals("stuff", beanDescriptor.getComponentProperty());		
	}
}
