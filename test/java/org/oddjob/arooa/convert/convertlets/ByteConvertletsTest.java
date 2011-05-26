/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionRegistry;

public class ByteConvertletsTest extends TestCase {

	public void testNumberToByte() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new ByteConvertlets().registerWith(registry);
		
		ConversionPath<Number, Byte> path = registry.findConversion(
				Number.class, Byte.class);
		
		Byte result = path.convert(new Float(42.24), null);
		
		assertEquals(new Byte((byte) 42), result);  
	}
	
	public void testStringToByte() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new ByteConvertlets().registerWith(registry);
		
		ConversionPath<String, Byte> path = registry.findConversion(
				String.class, Byte.class);

		assertEquals("String-Byte", path.toString());
		
		assertEquals(new Byte((byte) 42), path.convert("42", null));  
		
		try {
			path.convert("257", null);
			fail("ValueOutOfRange excpetion expected.");
			
		} catch (ConversionFailedException e) {
			// expected.
		}
	}

}
