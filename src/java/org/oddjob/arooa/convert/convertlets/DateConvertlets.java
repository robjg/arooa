/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.text.ParseException;
import java.util.Date;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.FinalConvertlet;
import org.oddjob.arooa.utils.DateHelper;

public class DateConvertlets implements ConversionProvider {

	public void registerWith(ConversionRegistry registry) {
		
		registry.register(Date.class, String.class, 
				new FinalConvertlet<Date, String>() {
			public String convert(Date from) {
				return DateHelper.formatDateTime((Date) from);
			};
		});
		
		registry.register(String.class, Date.class, 
				new Convertlet<String, Date>() {
			public Date convert(String from) {
				try {
					return DateHelper.parseDateTime(from);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			};
		});
		
		registry.register(Long.class, Date.class, 
				new FinalConvertlet<Long, Date>() {
			public Date convert(Long from) {
				return new Date(from.longValue());
			};
		});
		
		registry.register(Date.class, Long.class, 
				new Convertlet<Date, Long>() {
			public Long convert(Date from) {
				return new Long(from.getTime());
			}
		});
		
	}
	
}
