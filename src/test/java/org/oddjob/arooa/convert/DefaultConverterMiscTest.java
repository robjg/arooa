package org.oddjob.arooa.convert;

import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.junit.Assert;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.deploy.ListDescriptorBean;
import org.oddjob.arooa.types.ValueType;

/**
 * Miscellaneous conversions...
 * 
 * @author rob
 *
 */
public class DefaultConverterMiscTest extends Assert {

   @Test
	public void testValueTypeToString() {
		
		ArooaConverter test = new DefaultConverter();
		
		ConversionPath<ValueType, String> path = test.findConversion(ValueType.class, String.class);
		
		assertEquals("ValueType-String", path.toString());
	}
	
   @Test
	public void testStringToArooaValue() {
		
		ArooaConverter test = new DefaultConverter();
		
		ConversionPath<String, ArooaValue> path = test.findConversion(String.class, ArooaValue.class);
		
		assertEquals("String-Object-ArooaValue", path.toString());
	}
	
   @Test
	public void testStringToInputStream() {
		
		ArooaConverter test = new DefaultConverter();
		
		ConversionPath<String, InputStream> path =
			test.findConversion(String.class, InputStream.class);
		
		assertEquals("String-InputStream", path.toString());
	}
	
   @Test
	public void testURLToString() {
		
		ArooaConverter test = new DefaultConverter();
		
		ConversionPath<URL, String> path =
			test.findConversion(URL.class, String.class);
		
		assertEquals("URL-Object-String", path.toString());
		
	}
	
   @Test
	public void testSomeLoudicrousConversionsDontExist() {

		ArooaConverter test = new DefaultConverter();
		
		assertNull(test.findConversion(
				URL.class, Integer.class));
			
		assertNull(test.findConversion(
				ListDescriptorBean.class, File[].class));
		
		assertNull(test.findConversion(
				Integer.class, File.class));
	}
		
   @Test
	public void testThingsToList() throws ConversionFailedException {
		
		ArooaConverter test = new DefaultConverter();
		
		@SuppressWarnings("rawtypes")
		ConversionPath<String, List> path =
			test.findConversion(String.class, List.class);
		
		
		// This needs to be thought about!!!
		
		assertNull(path);		
	}


}
