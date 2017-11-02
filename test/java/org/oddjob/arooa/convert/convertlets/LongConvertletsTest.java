/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Test;

import java.math.BigDecimal;

import org.junit.Assert;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;

public class LongConvertletsTest extends Assert {

   @Test
	public void testNumberToLong() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new LongConvertlets().registerWith(registry);
		
		ConversionPath<Number, Long> path = registry.findConversion(
				Number.class, Long.class);
		
		Long result = path.convert(new BigDecimal(4.2E10), null);
		
		assertEquals(new Long(42000000000L), result);  
	}
	
   @Test
	public void testStringToLong() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new LongConvertlets().registerWith(registry);
		
		ConversionPath<String, Long> path = registry.findConversion(
				String.class, Long.class);

		assertEquals("String-Long", path.toString());
		
		Long result = path.convert("200909091234567890", null);
		
		assertEquals(new Long(200909091234567890L), result);  
	}

   @Test
	public void testLongToString() throws ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Long, String> path = registry.findConversion(
				Long.class, String.class);

		assertEquals("Long-String", path.toString());
		
		String result = path.convert(200909091234567890L, null);
		
		assertEquals("200909091234567890", result);  
	}
	
   @Test
	public void testLongToObject() throws ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Long, Object> path = registry.findConversion(
				Long.class, Object.class);

		assertEquals(0, path.length());
		
		Object result = path.convert(200909091234567890L, null);
		
		assertEquals(new Long(200909091234567890L), result);  
	}
	
   @Test
	public void testLongToArooaValue() throws ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Long, ArooaValue> path = registry.findConversion(
				Long.class, ArooaValue.class);

		assertEquals("Long-Number-Object-ArooaValue", path.toString());
				
		ArooaValue result = path.convert(200909091234567890L, null);
		
		assertEquals("200909091234567890", result.toString());  
	}
}
