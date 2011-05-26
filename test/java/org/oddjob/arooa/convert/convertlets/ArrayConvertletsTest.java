package org.oddjob.arooa.convert.convertlets;

import java.lang.reflect.Array;

import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;


import junit.framework.TestCase;

public class ArrayConvertletsTest extends TestCase {

	public void testArrayStringToInts() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConverter converter = new DefaultConverter();
		
		String[] array = { "1", "2" };
		
		Object results = Array.newInstance(Integer.TYPE, 2);
		
		results = converter.convert(array, results.getClass());
		
		assertEquals(1, ((int[]) results)[0]);
		assertEquals(2, ((int[]) results)[1]);
	}

	
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
	
	
}
