/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import java.text.ParseException;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.utils.DateHelper;

public class SqlDateConvertletsTest extends TestCase {
	
	
	
	public void testDateToDate() throws ConversionFailedException, ParseException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<java.util.Date, java.sql.Date> path = registry.findConversion(
				java.util.Date.class, java.sql.Date.class);

		assertEquals("Date-Date", path.toString());
				
		java.sql.Date result = path.convert(DateHelper.parseDate("2010-07-02"), null);
		
		assertEquals("2010-07-02", DateHelper.formatDate(result));
	}

	public void testStringToDate() throws ConversionFailedException, ParseException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<String, java.sql.Date> path = registry.findConversion(
				String.class, java.sql.Date.class);

		assertEquals("String-Date-Date", path.toString());
				
		java.sql.Date result = path.convert("2010-07-02", null);
		
		assertEquals(new java.sql.Date(DateHelper.parseDate("2010-07-02").getTime()), result);
	}

	public void testDateToString() throws ConversionFailedException, ParseException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<java.sql.Date, String> path = registry.findConversion(
				java.sql.Date.class, String.class);

		assertEquals("Date-Date-String", path.toString());
				
		String result = path.convert(new java.sql.Date(
				DateHelper.parseDate("2010-07-02").getTime()), null);
		
		assertEquals("2010-07-02 00:00:00.000", result);
	}

	
	public void testDateToArooaValue() throws ConversionFailedException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ConversionPath<java.sql.Date, ArooaValue> path = registry.findConversion(
				java.sql.Date.class, ArooaValue.class);

		assertEquals("Date-Date-Object-ArooaValue", path.toString());
				
		ArooaValue result = path.convert(new java.sql.Date(0), null);
		
		assertEquals(new java.sql.Date(0), ((ArooaObject) result).toValue());
	}

	
}
