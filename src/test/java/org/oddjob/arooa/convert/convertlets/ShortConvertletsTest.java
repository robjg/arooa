/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionRegistry;

public class ShortConvertletsTest extends Assert {

   @Test
	public void testNumberToShort() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new ShortConvertlets().registerWith(registry);
		
		ConversionPath<Number, Short> path = registry.findConversion(
				Number.class, Short.class);
		
		Short result = path.convert(new Float(42.24), null);
		
		assertEquals(new Short((short) 42), result);  
	}
	
   @Test
	public void testStringToShort() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new ShortConvertlets().registerWith(registry);
		
		ConversionPath<String, Short> path = registry.findConversion(
				String.class, Short.class);

		assertEquals("String-Short", path.toString());
		
		assertEquals(new Short((short) 42), path.convert("42", null));
		
		try {
			path.convert("32769", null);
			fail("ValueOutOfRange excpetion expected.");
			
		} catch (ConversionFailedException e) {
			// expected.
		}
	}

}
