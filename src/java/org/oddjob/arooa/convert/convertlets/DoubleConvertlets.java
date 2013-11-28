/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.FinalConvertlet;

public class DoubleConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Number.class, Double.class, 
				new Convertlet<Number, Double>() {
			public Double convert(Number from) {
				return new Double(from.doubleValue());
			};
		});
		
		registry.register(String.class, Double.class, 
				new Convertlet<String, Double>() {
			public Double convert(String from) {
				String stringValue = from.trim();
				if (stringValue.length() == 0) {
					return null;
				}
				else {
					return new Double(from);
				}
			}
		});
		
		registry.register(Double.class, String.class, 
				new FinalConvertlet<Double, String>() {
			public String convert(Double from) {
				return from.toString();
			}
		});
	}
}
