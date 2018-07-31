package org.oddjob.arooa.convert.jokers;

import org.junit.Test;

import java.io.File;

import org.junit.Assert;

import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;

public class ArrayConversionsTest extends Assert {

   @Test
	public void testArrayConvert() throws Exception {
		
		DefaultConversionRegistry reg = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(reg);
		
		DefaultConverter test = new DefaultConverter(reg);
		
		String sa[] = { "3", "9" };
		ConversionPath<String[], ?> result = test.findConversion(String[].class, int[].class);
		
		assertEquals(String[].class, result.getFromClass());
		assertEquals(int[].class, result.getToClass());
		
		assertEquals(2, result.length());
		
		int[] resultArray = (int[]) result.convert(sa, test);

		assertEquals(2, resultArray.length);
		
		assertEquals(3, resultArray[0]);
		assertEquals(9, resultArray[1]);
	}
	
   @Test
	public void testConvertSingleToArray() throws ConversionFailedException {

		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Integer, Integer[]> path = registry.findConversion(
				Integer.class, Integer[].class);
		
		assertEquals("Integer-Number-Object-Integer[]", path.toString());
		
		Object[] result = path.convert(new Integer(42), null);

		assertEquals(1, result.length);
		assertEquals(new Integer(42), result[0]);  
		
	}
	
	/**
	 * 42 gets converted to true?
	 */
   @Test
	public void testABigFatBugThatNeedFixing() throws ConversionFailedException {

		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Integer, String[]> path = registry.findConversion(
				Integer.class, String[].class);
		
		assertEquals("Integer-Number-Object-String[]", path.toString());
		
		String[] result = path.convert(new Integer(42), null);

		assertEquals(1, result.length);
		assertEquals("true", result[0]);  
		
	}
	
   @Test
	public void testFilesToStrings() throws ConversionFailedException {

		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<File[], String[]> path = registry.findConversion(
				File[].class, String[].class);
		
		assertEquals("File[]-Object-String[]", path.toString());
		
		Object[] result = path.convert(
				new File[] { new File("a.txt"), new File("b.txt") }, null);

		assertEquals(2, result.length);
		assertEquals("a.txt", result[0]);  
		assertEquals("b.txt", result[1]);  
		
	}
}
