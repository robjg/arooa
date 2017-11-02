/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import org.junit.Assert;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.convertlets.CheckingConvertletRegistry.Check;
import org.oddjob.arooa.utils.DateHelper;

public class DateConvertletsTest extends Assert {

	private static Date parse(String s) {
		try {
			return DateHelper.parseDateTime(s);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	
   @Test
	public void testAll() {
		CheckingConvertletRegistry checking = 
			new CheckingConvertletRegistry(	new Check[] {
					new Check() {
						public <F, T>void check(Class<F> from, Class<T> to, 
								Convertlet<F, T> convertlet) throws ArooaConversionException {
							assertEquals(Date.class, from);
							assertEquals(String.class, to);
							assertEquals("2007-12-25 12:57:00.000", 
									convertlet.convert(from.cast(parse("2007-12-25 12:57"))).toString());
							assertEquals("2007-12-25 00:00:00.000", 
									convertlet.convert(from.cast(parse("2007-12-25"))).toString());
						};
					},
					new Check() {
						public <F, T>void check(Class<F> from, Class<T> to, 
								Convertlet<F, T> convertlet) throws ArooaConversionException {
							assertEquals(String.class, from);
							assertEquals(Date.class, to);
							assertEquals(parse("2007-12-25 12:57"), 
									convertlet.convert(from.cast("2007-12-25 12:57")));
							assertEquals(parse("2007-12-25"), 
									convertlet.convert(from.cast("2007-12-25 00:00")));
						};
					},
					new Check() {
						public <F, T>void check(Class<F> from, Class<T> to, 
								Convertlet<F, T> convertlet) throws ArooaConversionException {
							assertEquals(Long.class, from);
							assertEquals(Date.class, to);
							assertEquals(new Date(100),
									convertlet.convert(from.cast(new Long(100))));
						};
					},
					new Check() {
						public <F, T>void check(Class<F> from, Class<T> to, 
								Convertlet<F, T> convertlet) throws ArooaConversionException {
							assertEquals(Date.class, from);
							assertEquals(Long.class, to);
							assertEquals(new Long(100), 
									convertlet.convert(from.cast(new Date(100))));
						};
					},
//					new Check() {
//						public <F, T>void check(Class<F> from, Class<T> to, 
//								Convertlet<F, T> convertlet) throws ConvertletException{
//							assertEquals(Date.class, from);
//							assertEquals(String.class, to);
//						};
//					},
			});
		
		new DateConvertlets().registerWith(checking);
		
		assertEquals(4, checking.count);
	}
	
}
