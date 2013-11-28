/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.FinalConvertlet;

public class FloatConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Number.class, Float.class, 
				new Convertlet<Number, Float>() {
			public Float convert(Number from) {
				return new Float(((Number) from).floatValue());
			};
		});
		
		registry.register(String.class, Float.class, 
				new Convertlet<String, Float>() {
			public Float convert(String from) {
				String stringValue = from.trim();
				if (stringValue.length() == 0) {
					return null;
				}
				else {
					return new Float(stringValue);
				}
			}
		});
		
		registry.register(Float.class, String.class, 
				new FinalConvertlet<Float, String>() {
			public String convert(Float from) {
				return from.toString();
			}
		});
	}
	
}
