/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.FinalConvertlet;

public class ShortConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Number.class, Short.class, 
				new Convertlet<Number, Short>() {
			public Short convert(Number from) {
				return new Short(from.shortValue());
			};
		});
		
		registry.register(String.class, Short.class, 
				new Convertlet<String, Short>() {
			public Short convert(String from) {
				return new Short(from);
			}
		});
		
		registry.register(Short.class, String.class, 
				new FinalConvertlet<Short, String>() {
			public String convert(Short from) {
				return from.toString();
			}
		});
	}
	
}
