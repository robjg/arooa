package org.oddjob.arooa.convert.convertlets;

import org.junit.Test;

import java.lang.reflect.Array;

import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;


import org.junit.Assert;

public class ArrayConvertletsTest extends Assert {

   @Test
	public void testArrayStringToInts() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConverter converter = new DefaultConverter();
		
		String[] array = { "1", "2" };
		
		Object results = Array.newInstance(Integer.TYPE, 2);
		
		results = converter.convert(array, results.getClass());
		
		assertEquals(1, ((int[]) results)[0]);
		assertEquals(2, ((int[]) results)[1]);
	}

	
   @Test
	public void testArrayToIterable() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConverter converter = new DefaultConverter();
		
		String[] array = { "a", "b" };
		
		Iterable<?> iterable = converter.convert(array, Iterable.class);

		Object[] results = new Object[2];
		
		int i = 0;
		for (Object r : iterable) {
			results[i++] = r;
		}
		
		assertEquals("a", results[0]);
		assertEquals("b", results[1]);
	}
	
   @Test
	public void testToString() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConverter converter = new DefaultConverter();
		
		String[] array = { "a", "b" };
		
		String result = converter.convert(array, String.class);

		assertEquals("a, b", result);
	}
	
}
