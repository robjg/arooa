/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.convert.*;

public class CharacterConvertletsTest extends Assert {

   @Test
	public void testStringToCharacter() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new CharacterConvertlets().registerWith(registry);

	   ConversionLookup lookup = registry.get();

		ConversionPath<String, Character> path = lookup.findConversion(
				String.class, Character.class);
		
		Character result = path.convert("A", null);

	   assertEquals(Character.valueOf('A'), result);
	}
	
   @Test
	public void testCharacterToString() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);

	   ConversionLookup lookup = registry.get();

	   ConversionPath<Character, String> path = lookup.findConversion(
				Character.class, String.class);
		
		String result = path.convert('A', null);
		
		assertEquals("A", result);  
	}
	
   @Test
	public void testNumberToCharacter() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);

	   ConversionLookup lookup = registry.get();


	   ConversionPath<Number, Character> path = lookup.findConversion(
			   Number.class, Character.class);
	   Character result = path.convert(65, null);
		
		assertEquals(Character.valueOf('A'), result);
	}
	
   @Test
	public void testCharacterToNumber() throws ConversionFailedException {
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);

	   ConversionLookup lookup = registry.get();

	   ConversionPath<Character, Number> path = lookup.findConversion(
				Character.class, Number.class);
		
		Number result = path.convert('A', null);
		
		assertEquals(65, result);
	}
	
}
