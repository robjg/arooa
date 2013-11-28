/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.FinalConvertlet;

public class IntegerConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Number.class, Integer.class, 
				new Convertlet<Number, Integer>() {
			public Integer convert(Number from) {
				return new Integer(from.intValue());
			};
		});
		
		registry.register(String.class, Integer.class, 
				new Convertlet<String, Integer>() {
			public Integer convert(String from) {
				String stringValue = from.trim();
				if (stringValue.length() == 0) {
					return null;
				}
				else {
					return new Integer(stringValue);
				}
			}
		});
		
		registry.register(Integer.class, String.class, 
				new FinalConvertlet<Integer, String>() {
			public String convert(Integer from) {
				return from.toString();
			}
		});

	}		
}
