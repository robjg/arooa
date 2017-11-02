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

public class BooleanConvertletsTest extends Assert {

   @Test
	public void testNumberToBoolean() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new BooleanConvertlets().registerWith(registry);
		
		ConversionPath<Number, Boolean> path = registry.findConversion(
				Number.class, Boolean.class);
		
		assertEquals(new Boolean(true), 
				path.convert(new Double(42.24), null));
		
		assertEquals(new Boolean(false), 
				path.convert(new Short((short) 0), null));
	}
	
   @Test
	public void testBooleanToNumber() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new BooleanConvertlets().registerWith(registry);
		
		ConversionPath<Boolean, Number> path = registry.findConversion(
				Boolean.class, Number.class);

		assertEquals(new Integer(1), 
				path.convert(new Boolean(true), null));
		
		assertEquals(new Integer(0), 
				path.convert(new Boolean(false), null));
	}
	
   @Test
	public void testStringToBoolean() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new BooleanConvertlets().registerWith(registry);
		
		ConversionPath<String, Boolean> path = registry.findConversion(
				String.class, Boolean.class);

		assertEquals(new Boolean(true), 
				path.convert("yes", null));

		assertEquals(new Boolean(false), 
				path.convert("no", null));
	}

   @Test
	public void testBooleanToString() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Boolean, String> path = registry.findConversion(
				Boolean.class, String.class);

		assertEquals("true", 
				path.convert(new Boolean(true), null));
		
		assertEquals("false", 
				path.convert(new Boolean(false), null));
	}
	
}
