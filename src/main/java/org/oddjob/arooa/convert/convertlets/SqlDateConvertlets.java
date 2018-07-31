/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.sql.Date;

import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;

public class SqlDateConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(java.util.Date.class, Date.class, 
				new Convertlet<java.util.Date, Date>() {
			public Date convert(java.util.Date from) {
				return new Date(from.getTime());
			};
		});		
	}
}
