/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaConstants;

/**
 * 
 */
public class DateHelperTest extends TestCase {
	private static final Logger logger = Logger.getLogger(DateHelperTest.class);

	public void testShowWhyWeNeedThreeTimeFormats() {
		
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
		
		try {
			format.parse("12:45");
			fail("Expected to fail.");
		} catch (ParseException e) {
			// Expected.
		}
		
	}
	
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

		Calendar expected = Calendar.getInstance();
		expected.clear();
		expected.set(2005, 11, 25, 10, 47, 0);

		assertEquals(expected.getTime(), 
				DateHelper.parseDateTime("2005-12-25 10:47", (TimeZone) null));
	}
	
	public void testDate() throws ParseException {
		Date d = DateHelper.parseDate("2005-12-25", TimeZone.getDefault());

		String result = DateHelper.formatDate(d);
		assertEquals("2005-12-25", result);
	}
	
	public void testDateWithTimezone() throws ParseException {
		
		Date there = DateHelper.parseDate("2005-12-25", "GMT+02");
		
		Date here = DateHelper.parseDate("2005-12-25", "GMT");
		
		int offset = TimeZone.getTimeZone("GMT+02").getOffset(
				here.getTime());
		
		logger.debug("Offset is " + offset);
		assertEquals(here.getTime() - offset, there.getTime());
	}
	
	public void testDateTimeWithTimezone() throws ParseException {
		
		Date there = DateHelper.parseDateTime("2005-12-25 12:00", "GMT+02");
		
		Date here = DateHelper.parseDateTime("2005-12-25 12:00", "GMT");
		
		int offset = TimeZone.getTimeZone("GMT+02").getOffset(
				here.getTime());
		
		logger.debug("Offset is " + offset);
		assertEquals(here.getTime() - offset, there.getTime());
	}
	
	public void testDateOnlyDateTimeWithTimezone() throws ParseException {
		
		Date there = DateHelper.parseDateTime("2005-12-25", "GMT+02");
		
		Date here = DateHelper.parseDateTime("2005-12-25", "GMT");
		
		int offset = TimeZone.getTimeZone("GMT+02").getOffset(
				here.getTime());
		
		logger.debug("Offset is " + offset);
		assertEquals(here.getTime() - offset, there.getTime());
	}

	public void testFormatMilliseconds() {
		
		assertEquals("0 seconds", 
				DateHelper.formatMilliseconds(999L));
		
		assertEquals("1 second", 
				DateHelper.formatMilliseconds(1050L));
		
		assertEquals("55 seconds", 
				DateHelper.formatMilliseconds(55002L));
		
		long oneMinute = 60 * 1000;
		
		assertEquals("1 minute 0 seconds", 
				DateHelper.formatMilliseconds(oneMinute));
		
		assertEquals("2 minutes 5 seconds",
				DateHelper.formatMilliseconds(
						2 * oneMinute + 5000L));
		
		long oneHour = 60 * oneMinute;
		
		assertEquals("1 hour 0 minutes",
				DateHelper.formatMilliseconds(oneHour));
		
		assertEquals("1 hour 1 minute",
				DateHelper.formatMilliseconds(oneHour + oneMinute));
		
		assertEquals("5 hours 59 minutes",
				DateHelper.formatMilliseconds(
						5 * oneHour + 59 * oneMinute));
		
		long oneDay = 24 * oneHour; 
		
		assertEquals("1 day 0 hours and 0 minutes",
				DateHelper.formatMilliseconds(oneDay));
		
		assertEquals("17 days 4 hours and 35 minutes",
				DateHelper.formatMilliseconds(
						17 * oneDay + 4 * oneHour + 35 * oneMinute));
	}
	
	public void testSesibleErrorMessage() {
		
		try {
			DateHelper.parseDate("20120322");
		} catch (ParseException e) {
			logger.info(e.getMessage());
			assertTrue(e.getMessage().contains(ArooaConstants.DATE_FORMAT));
		}
		
		try {
			DateHelper.parseTime("12-45");
		} catch (ParseException e) {
			logger.info(e.getMessage());
			assertTrue(e.getMessage().contains(ArooaConstants.TIME_FORMAT1));
			assertTrue(e.getMessage().contains(ArooaConstants.TIME_FORMAT2));
			assertTrue(e.getMessage().contains(ArooaConstants.TIME_FORMAT3));
		}
		
		try {
			DateHelper.parseDateTime("20120322 12-45");
		} catch (ParseException e) {
			logger.info(e.getMessage());
			assertTrue(e.getMessage().contains(ArooaConstants.DATE_FORMAT));
			assertTrue(e.getMessage().contains(ArooaConstants.TIME_FORMAT1));
			assertTrue(e.getMessage().contains(ArooaConstants.TIME_FORMAT2));
			assertTrue(e.getMessage().contains(ArooaConstants.TIME_FORMAT3));
		}
	}
}
