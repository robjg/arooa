/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.FinalConvertlet;

public class CharacterConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(String.class, Character.class, 
				new Convertlet<String, Character>() {
			public Character convert(String from) {
				if (from.length() == 0) {
					return null;
				}
				return new Character(from.charAt(0));
			}
		});
				
		registry.register(Number.class, Character.class, 
				new Convertlet<Number, Character>() {
			public Character convert(Number from) {
				return new Character((char) from.intValue());
			};
		});
		
		registry.register(Character.class, Number.class, 
				new Convertlet<Character, Number>() {
			public Number convert(Character from) {
				return new Integer(from.charValue());
			}
		});
		
		registry.register(Character.class, String.class, 
				new FinalConvertlet<Character, String>() {
			public String convert(Character from) {
				return from.toString();
			}
		});
	}
	
}
