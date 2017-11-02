/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;

public class DoubleConvertletsTest extends Assert {

   @Test
	public void testNumberToDouble() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DoubleConvertlets().registerWith(registry);
		
		ConversionPath<Number, Double> path = registry.findConversion(
				Number.class, Double.class);
		
		Double result = path.convert(new Float(42.24), null);
		
		assertEquals(42.24, result, 0.001);  
	}
	
   @Test
	public void testStringToDouble() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DoubleConvertlets().registerWith(registry);
		
		ConversionPath<String, Double> path = registry.findConversion(
				String.class, Double.class);
		
		Double result = path.convert("42.24", null);
		
		assertEquals(42.24, result, 0.001);  
	}

   @Test
	public void testDoubleToString() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Double, String> path = registry.findConversion(
				Double.class, String.class);
		
		assertEquals("Double-String", path.toString());
		
		String result = path.convert(new Double(42.24), null);
		
		assertEquals(result, "42.24");  
	}
}
