/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.math.BigDecimal;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;

public class IntegerConvertletsTest extends TestCase {

	public void testNumberToInteger() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new IntegerConvertlets().registerWith(registry);
		
		ConversionPath<Number, Integer> path = registry.findConversion(
				Number.class, Integer.class);
		
		Integer result = path.convert(new BigDecimal(42), null);
		
		assertEquals(new Integer(42), result);  
	}
	
	public void testStringToInteger() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new IntegerConvertlets().registerWith(registry);
		
		ConversionPath<String, Integer> path = registry.findConversion(
				String.class, Integer.class);

		assertEquals("String-Integer", path.toString());
		
		Integer result = path.convert("2009090900", null);
		
		assertEquals(new Integer(2009090900), result);  
	}
	
	public void testIntegerToString() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Integer, String> path = registry.findConversion(
				Integer.class, String.class);

		assertEquals("Integer-String", path.toString());
		
		Object result = path.convert(new Integer(2009090900), null);
		
		assertEquals("2009090900", result);  
	}
}
