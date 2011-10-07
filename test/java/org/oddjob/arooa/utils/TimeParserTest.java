package org.oddjob.arooa.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

public class TimeParserTest extends TestCase {

	private class Results implements CalendarAdaptor {
		
		int hours;
		int minutes;
		int seconds;
		int milliseconds;
		
		@Override
		public void set(int field, int value) {
			if (field == Calendar.HOUR_OF_DAY) {
				hours = value;
			}
			else if (field == Calendar.MINUTE) {
				minutes = value;
			}
			else if (field == Calendar.SECOND) {
				seconds = value;
			}
			else if (field == Calendar.MILLISECOND) {
				milliseconds = value;
			}
		}
		
		@Override
		public Date getDate() {
			return null;
		}
	}
	
	
	public void testDates() throws ParseException {
		
		Results results = new Results();
		
		TimeParser test;
		
		test = new TimeParser(results);
		
		test.parse("01:02");
		
		assertEquals(1, results.hours);
		assertEquals(2, results.minutes);
		
		test.parse("01:02:03");
		
		assertEquals(1, results.hours);
		assertEquals(2, results.minutes);
		assertEquals(3, results.seconds);
		
		test.parse("01:02:03.4");
		
		assertEquals(1, results.hours);
		assertEquals(2, results.minutes);
		assertEquals(3, results.seconds);
		assertEquals(400, results.milliseconds);
		
		test.parse("01:02:03.456");
		
		assertEquals(1, results.hours);
		assertEquals(2, results.minutes);
		assertEquals(3, results.seconds);
		assertEquals(456, results.milliseconds);
	}
	
	public void testReferenceDateWithMilliseconds() throws ParseException {
		
		Date referenceDate = DateHelper.parseDateTime("2006-03-01 10:59:59.999");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(referenceDate);
		
		TimeParser test = new TimeParser(cal);
		
		Date result = test.parse("11:00");
		
		assertEquals(
				DateHelper.parseDateTime("2006-03-01 11:00"),
				result);
	}
	
	public void testUnusalParsing() throws ParseException {
		
		new TimeParser().parse("1234:56");
		
		new TimeParser().parse("1:01");
		
		try {
			new TimeParser().parse("1:2");
		} 
		catch (ParseException e) {
			// Expected.
		}
		
		try {
			new TimeParser().parse(":01");
		}
		catch (ParseException e) {
			// Expected.
		}
		
		try {
			new TimeParser().parse("01:01:2");
		}
		catch (ParseException e) {
			// Expected.
		}

		try {
			new TimeParser().parse("01:01:02.");
		}
		catch (ParseException e) {
			// Expected.
		}
		
	}
}
