package org.oddjob.arooa.reflect;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.convert.ArooaConversionException;

public class BeanViewBeanTest extends Assert {

	public static class Fruit {
		
		public String getOrange() {
			return null;
		}
		
		public void setBannanas(int index, String banana) {
		}

		public String getApple(String type) {
			return null;
		}
		
		public void setOrange(String orange) {
		}

	}
	
   @Test
	public void testPropertyOrder() throws ArooaConversionException {

		BeanViewBean test = new BeanViewBean();

		test.setProperties("orange, bannanas, apple");

		
		BeanView result = test.toValue();
		
		String[] properties = result.getProperties();
		
		String[] expected = { "orange", "bannanas", "apple" };

		assertEquals(expected[0], properties[0]);
		assertEquals(expected[1], properties[1]);
		assertEquals(expected[2], properties[2]);
		
		assertEquals(expected[0], result.titleFor(properties[0]));
		assertEquals(expected[1], result.titleFor(properties[1]));
		assertEquals(expected[2], result.titleFor(properties[2]));
	}
	
   @Test
	public void testWithTitles() throws ArooaConversionException {
		
		BeanViewBean test = new BeanViewBean();

		test.setProperties("x, y, apple");
		test.setTitles("Orange, Bannanas");
		
		BeanView result = test.toValue();
		
		String[] properties = result.getProperties();
		
		String[] expected = { "Orange", "Bannanas", "apple" };

		assertEquals(expected[0], result.titleFor(properties[0]));
		assertEquals(expected[1], result.titleFor(properties[1]));
		assertEquals(expected[2], result.titleFor(properties[2]));
	}
}
