package org.oddjob.arooa.reflect;

import org.junit.Test;

import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;

import org.junit.Assert;

public class FallbackBeanViewTest extends Assert {

	public static class Fruit {
		
		public void setColour(String colour) {
			
		}
		
		public String getColour() {
			return null;
		}
	}
	
	
   @Test
	public void testProperties() {		
		
		Fruit bean = new Fruit();
		
		FallbackBeanView test = new FallbackBeanView(
				new BeanUtilsPropertyAccessor(), bean);
		
		String[] properties = test.getProperties();
		
		assertEquals(2, properties.length);
	}
}
