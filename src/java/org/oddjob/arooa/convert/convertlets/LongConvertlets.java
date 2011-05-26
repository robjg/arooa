/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.FinalConvertlet;

public class LongConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Number.class, Long.class, 
				new Convertlet<Number, Long>() {
			public Long convert(Number from) {
				return new Long(from.longValue());
			};
		});
				
		registry.register(String.class, Long.class, 
				new Convertlet<String, Long>() {
			public Long convert(String from) {
				return new Long(from);
			}
		});
		
		registry.register(Long.class, String.class, 
				new FinalConvertlet<Long, String>() {
			public String convert(Long from) {
				return from.toString();
			}
		});
	}	
}
