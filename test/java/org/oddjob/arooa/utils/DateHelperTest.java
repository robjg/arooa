/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.utils;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

/**
 * 
 */
public class DateHelperTest extends TestCase {
	private static final Logger logger = Logger.getLogger(DateHelperTest.class);

	final long SECOND = 1000;
	final long MINUTE = SECOND * 60;
	final long HOUR = MINUTE * 60; 
	
	public void testParseTime() throws ParseException {
		long m; 

		TimeZone.setDefault(null);
		logger.debug(TimeZone.getDefault());
		
		m = DateHelper.parseTime("00:00");
		logger.debug("" + new Date(m));
		assertEquals(0, m);
		
		long expected = 10 * HOUR + 47 * MINUTE;
		
		m = DateHelper.parseTime("10:47");
		logger.debug("" + new Date(m));	
		assertEquals(expected, m);
			
		TimeZone.setDefault(TimeZone.getTimeZone("Australia/Sydney"));
		m = DateHelper.parseTime("10:47");
		logger.debug("" + new Date(m));	
		assertEquals(expected, m);

		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
		
		m = DateHelper.parseTime("10:47");
		logger.debug("" + new Date(m));	
		logger.debug("" + (m - expected / HOUR));	
		
		assertEquals(expected, m);
		
	}
	
	public void testSeconds() throws ParseException {
		long m;
		
		TimeZone.setDefault(null);
		logger.debug(TimeZone.getDefault());
		
		m = DateHelper.parseTime("10:47:56");
		logger.debug("" + new Date(m));	
		assertEquals(10 * HOUR + 47 * MINUTE + 56 * SECOND, m);
		
		TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
		
		m = DateHelper.parseTime("10:47:56");
		logger.debug("" + new Date(m));	
		assertEquals(10 * HOUR + 47 * MINUTE + 56 * SECOND, m);

		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
		
		m = DateHelper.parseTime("10:47:56.123");
		logger.debug("" + new Date(m));	
		assertEquals(10 * HOUR + 47 * MINUTE + 56 * SECOND + 123, m);
		
	}
	
	public void testMoreThan24Hours() throws ParseException {
		long m;
		
		TimeZone.setDefault(null);
		logger.debug(TimeZone.getDefault());
		
		m = DateHelper.parseTime("28:47:56");
		logger.debug("" + new Date(m));	
		assertEquals(28 * HOUR + 47 * MINUTE + 56 * SECOND, m);
		
		
	}

	public void testParseDateTime() throws ParseException {

//		TimeZone.setDefault(null);
//		logger.debug(TimeZone.getDefault());
//
//		Date expected = new Date(DateHelper.parseDate(
//				DateHelper.formatDate(new Date()), TimeZone.getDefault()).getTime()
//			+ DateHelper.parseTime("10:47"));
//		
//		assertEquals(expected, DateHelper.parseDateTime("10:47", null));
//
//		Date d = DateHelper.parseDateTime("25-dec-05", null);
	}
	
	public void testDate() throws ParseException {
		Date d = DateHelper.parseDate("2005-12-25", TimeZone.getDefault());

		String result = DateHelper.formatDate(d);
		assertEquals("2005-12-25", result);
	}
	
	public void testDateWithTimezone() throws ParseException {
		
		Date there = DateHelper.parseDate("2005-12-25", "GMT+02");
		
		Date here = DateHelper.parseDate("2005-12-25");
		
		int offset = TimeZone.getTimeZone("GMT+02").getOffset(
				here.getTime());
		
		logger.debug("Offset is " + offset);
		assertEquals(here.getTime() - offset, there.getTime());
	}
	
	public void testDateTimeWithTimezone() throws ParseException {
		
		Date there = DateHelper.parseDateTime("2005-12-25 12:00", "GMT+02");
		
		Date here = DateHelper.parseDateTime("2005-12-25 12:00");
		
		int offset = TimeZone.getTimeZone("GMT+02").getOffset(
				here.getTime());
		
		logger.debug("Offset is " + offset);
		assertEquals(here.getTime() - offset, there.getTime());
	}
	
	public void testDateOnlyDateTimeWithTimezone() throws ParseException {
		
		Date there = DateHelper.parseDateTime("2005-12-25", "GMT+02");
		
		Date here = DateHelper.parseDateTime("2005-12-25");
		
		int offset = TimeZone.getTimeZone("GMT+02").getOffset(
				here.getTime());
		
		logger.debug("Offset is " + offset);
		assertEquals(here.getTime() - offset, there.getTime());
	}
}
