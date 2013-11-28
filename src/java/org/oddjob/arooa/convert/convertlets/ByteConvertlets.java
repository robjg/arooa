/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.FinalConvertlet;

public class ByteConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
				
		registry.register(Number.class, Byte.class, 
				new Convertlet<Number, Byte>() {
			public Byte convert(Number from) {
				return new Byte(from.byteValue());
			};
		});
		
		registry.register(String.class, Byte.class, 
				new Convertlet<String, Byte>() {
			public Byte convert(String from) {
				String stringValue = from.trim();
				if (stringValue.length() == 0) {
					return new Byte((byte) 0);
				}
				else {
					return new Byte(stringValue);
				}
			}
		});
		
		registry.register(Byte.class, String.class, 
				new FinalConvertlet<Byte, String>() {
			public String convert(Byte from) {
				return from.toString();
			}
		});
	}
	
}
