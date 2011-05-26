/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionRegistry;

public class FloatConvertletsTest extends TestCase {

	public void testNumberToFloat() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new FloatConvertlets().registerWith(registry);
		
		ConversionPath<Number, Float> path = registry.findConversion(
				Number.class, Float.class);
		
		Float result = path.convert(new BigDecimal(42.24), null);
		
		assertEquals(42.24, result, 0.001);  
	}
	
	public void testStringToFloat() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new FloatConvertlets().registerWith(registry);
		
		ConversionPath<String, Float> path = registry.findConversion(
				String.class, Float.class);
		
		Float result = path.convert("42.24", null);
		
		assertEquals(42.24, result, 0.001);  
	}
	
}
