package org.oddjob.arooa.beanutils;

import junit.framework.TestCase;

import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.PropertyAccessor;

public class MagicBeanDefinitionTest extends TestCase {

	public void testCreateMagic() {
		
		MagicBeanDefinition def = new MagicBeanDefinition();
		
		def.setName("SnackBean");
		
		MagicBeanProperty prop1 = new MagicBeanProperty();
		prop1.setName("fruit");
		prop1.setType("java.lang.String");
		
		MagicBeanProperty prop2 = new MagicBeanProperty();
		prop2.setName("quantity");
		prop2.setType("java.lang.Integer");
		
		def.setProperties(0, prop1);
		def.setProperties(1, prop2);
		
		ArooaClass arooaClass = def.createMagic(
				getClass().getClassLoader());
		
		PropertyAccessor accessor = new BeanUtilsPropertyAccessor();
		
		BeanOverview overview = arooaClass.getBeanOverview(accessor);
		
		String[] properties = overview.getProperties();
		
		assertEquals(2, properties.length);
		assertEquals("fruit", properties[0]);
		assertEquals("quantity", properties[1]);
		
		assertEquals(String.class, overview.getPropertyType("fruit"));
		assertEquals(Integer.class, overview.getPropertyType("quantity"));
	}
}
