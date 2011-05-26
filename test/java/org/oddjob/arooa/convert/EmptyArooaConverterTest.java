package org.oddjob.arooa.convert;

import junit.framework.TestCase;

import org.oddjob.arooa.types.ArooaObject;

public class EmptyArooaConverterTest extends TestCase {

	public void testSimple() throws NoConversionAvailableException, ConversionFailedException {
		
		EmptyArooaConverter converter = new EmptyArooaConverter();
		
		ConversionPath<Integer, Number> conversion = converter.findConversion(
				Integer.class, Number.class);
		
		assertEquals(0, conversion.length());
		
		Number number = converter.convert(new Integer(5), Number.class);
		
		assertEquals(new Integer(5), number);
	}
	
	public void testNoConversion() throws ConversionFailedException {
		
		EmptyArooaConverter converter = new EmptyArooaConverter();
		
		ConversionPath<Number, Integer> conversion = converter.findConversion(
				Number.class, Integer.class);
		
		assertEquals(null, conversion);
		
		try {
			converter.convert(new Short((short) 5), Integer.class);
			fail("No conversion expected.");
		} catch (NoConversionAvailableException e) {
			// expected.
		}
	}
	
	public void testPrimitive() throws NoConversionAvailableException, ConversionFailedException {
		
		EmptyArooaConverter converter = new EmptyArooaConverter();
		
		int number = converter.convert(new Integer(5), int.class);
		
		assertEquals(5, number);
	}
	
	public void testArooaValue() throws ConversionFailedException {
		
		EmptyArooaConverter converter = new EmptyArooaConverter();
		
		try {
			converter.convert(new ArooaObject("apple"), Object.class);
			fail("No conversion expected.");
		} catch (NoConversionAvailableException e) {
			// expected.
		}

	}
}
