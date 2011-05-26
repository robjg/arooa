/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StringConvertletsTest extends TestCase {

	private class Apple {
		@Override
		public String toString() {
			return "apple";
		}
	}
	
	public void testToString() throws ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new StringConvertlets().registerWith(registry);
		
		ConversionPath<Apple, String> path = registry.findConversion(
				Apple.class, String.class);
		
		String result = path.convert(new Apple(), null);
		
		assertEquals("apple", result);
	}

	public void testStringToInputStream() throws ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new StringConvertlets().registerWith(registry);
		
		ConversionPath<String, InputStream> path = registry.findConversion(
				String.class, InputStream.class);
		
		InputStream input = path.convert("Apple", null);
		
		byte[] buffer = new byte[5];
		try {
			input.read(buffer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		assertEquals("Apple", new String(buffer));
		
	}
	
	public void testTokenizedString() throws ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new StringConvertlets().registerWith(registry);
		
		ConversionPath<String, String[]> path = registry.findConversion(
				String.class, String[].class);
		
		String[] results = path.convert("cat, dog, horse", null);
		
		assertEquals(3, results.length);
		
		assertEquals("cat", results[0]);
		assertEquals("dog", results[1]);
		assertEquals("horse", results[2]);
	}
	
	public void testObjectToString() throws ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new StringConvertlets().registerWith(registry);
		
		ConversionPath<Object, String> path = registry.findConversion(
				Object.class, String.class);
		
		assertEquals("Object-String", path.toString());
		
		String result = path.convert(new Integer(42), null);
		
		assertEquals("42", result);
	}
	
	public static class ThingWithArrayProperty implements Runnable {
		
		private String[] fruit;

		public void setFruit(String[] fruit) {
			this.fruit = fruit;
		}

		public String[] getFruit() {
			return fruit;
		}
		
		@Override
		public void run() {
			for (String item : fruit) {
				System.out.println(item);
			}
		}
	}
	
	public void testTokenizedStringToArray() throws ArooaParseException {
		
		ThingWithArrayProperty root = new ThingWithArrayProperty();
		
		StandardArooaParser parser = new StandardArooaParser(root);

		parser.parse(new XMLConfiguration(
				"org/oddjob/arooa/convert/convertlets/StringTokenizerExample.xml",
				getClass().getClassLoader()));
		
		parser.getSession().getComponentPool().configure(root);
		
		assertEquals("apple", root.getFruit()[0]);
		assertEquals("orange", root.getFruit()[1]);
		assertEquals("pear", root.getFruit()[2]);
		
		assertEquals(3, root.getFruit().length);
		
		root.run();
	}
}
