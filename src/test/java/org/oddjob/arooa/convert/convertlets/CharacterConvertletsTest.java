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

public class CharacterConvertletsTest extends Assert {

   @Test
	public void testStringToCharacter() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new CharacterConvertlets().registerWith(registry);
		
		ConversionPath<String, Character> path = registry.findConversion(
				String.class, Character.class);
		
		Character result = path.convert("A", null);
		
		assertEquals(new Character('A'), result);  
	}
	
   @Test
	public void testCharacterToString() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Character, String> path = registry.findConversion(
				Character.class, String.class);
		
		String result = path.convert(new Character('A'), null);
		
		assertEquals("A", result);  
	}
	
   @Test
	public void testNumberToCharacter() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Number, Character> path = registry.findConversion(
				Number.class, Character.class);
		
		Character result = path.convert(new Integer(65), null);
		
		assertEquals(new Character('A'), result);  
	}
	
   @Test
	public void testCharacterToNumber() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<Character, Number> path = registry.findConversion(
				Character.class, Number.class);
		
		Number result = path.convert(new Character('A'), null);
		
		assertEquals(new Integer(65), result);  
	}
	
}
