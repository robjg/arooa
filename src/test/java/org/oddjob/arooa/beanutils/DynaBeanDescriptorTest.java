package org.oddjob.arooa.beanutils;

import org.junit.Test;

import org.junit.Assert;

import org.apache.commons.beanutils.LazyDynaMap;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.standard.StandardArooaDescriptor;

public class DynaBeanDescriptorTest extends Assert {

	
   @Test
	public void testDynaBeanInStandardDescriptor() {
		
		MagicBeanDefinition def = new MagicBeanDefinition();
		
		def.setElement("SnackBean");
		
		MagicBeanDescriptorProperty prop1 = new MagicBeanDescriptorProperty();
		prop1.setName("fruit");
		prop1.setType("java.lang.String");
		
		MagicBeanDescriptorProperty prop2 = new MagicBeanDescriptorProperty();
		prop2.setName("quantity");
		prop2.setType("java.lang.Integer");
		
		MagicBeanDescriptorProperty prop3 = new MagicBeanDescriptorProperty();
		prop3.setName("stuff");
		prop3.setType("java.lang.Object");
		
		def.setProperties(0, prop1);
		def.setProperties(1, prop2);
		def.setProperties(2, prop3);
		
		ArooaClass arooaClass = def.createMagic(
				getClass().getClassLoader());
		
		BeanUtilsPropertyAccessor accessor = 
			new BeanUtilsPropertyAccessor();
		
		StandardArooaDescriptor test = new StandardArooaDescriptor();
		
		ArooaBeanDescriptor result = test.getBeanDescriptor(
				arooaClass, accessor);
		
		assertNotNull(result);
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				result.getConfiguredHow("fruit"));
		
		assertEquals(ConfiguredHow.ATTRIBUTE, 
				result.getConfiguredHow("quantity"));
		
		assertEquals(ConfiguredHow.ELEMENT, 
				result.getConfiguredHow("stuff"));
	}
	
   @Test
	public void testMutableDynaBeanInStandardDescriptor() {
		
		
		LazyDynaMap dynaBean = new LazyDynaMap();

		BeanUtilsPropertyAccessor accessor = 
			new BeanUtilsPropertyAccessor();
		
		StandardArooaDescriptor test = new StandardArooaDescriptor();
		
		ArooaClass arooaClass = accessor.getClassName(dynaBean);
		
		ArooaBeanDescriptor result = test.getBeanDescriptor(
				arooaClass, accessor);
		
		assertNotNull(result);
				
		assertEquals(null, 
				result.getConfiguredHow("anything"));
	}
}
