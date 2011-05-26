package org.oddjob.arooa.reflect;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;

import junit.framework.TestCase;

public class FallbackBeanViewTest extends TestCase {

	public static class Fruit {
		
		public void setColour(String colour) {
			
		}
		
		public String getColour() {
			return null;
		}
	}
	
	
	public void testProperties() {		
		
		Fruit bean = new Fruit();
		
		FallbackBeanView test = new FallbackBeanView(
				new BeanUtilsPropertyAccessor(), bean);
		
		String[] properties = test.getProperties();
		
		assertEquals(2, properties.length);
	}
}
