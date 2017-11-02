package org.oddjob.arooa.utils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Assert;

public class SpringSafeCalendarTest extends Assert {

	SimpleDateFormat resultFormat;
	
	Calendar cal;
	
	public SpringSafeCalendarTest() {
		resultFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		resultFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));
		cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"));
	}
		
	
   @Test
	public void testDifferentSpringTimes() throws ParseException {
		cal.set(2005, 02, 27);
		
		TimeParser timeFormat; 
		
		timeFormat = new TimeParser(
				new SpringSafeCalendar(cal)); 
		
		assertEquals("2005-03-27 00:55",
				resultFormat.format(
						timeFormat.parse("00:55")));
		
		assertEquals("2005-03-27 02:00",
				resultFormat.format(
						timeFormat.parse("01:00")));
		
		assertEquals("2005-03-27 02:00",
				resultFormat.format(
						timeFormat.parse("01:30")));
		
		assertEquals("2005-03-27 02:00",
				resultFormat.format(
						timeFormat.parse("02:00")));
		
		assertEquals("2005-03-27 02:05",
				resultFormat.format(
						timeFormat.parse("02:05")));
	}
	
   @Test
	public void testDifferentAutumnTimes() throws ParseException {
		cal.set(2005, 9, 30);
		
		TimeParser timeFormat; 
		
		timeFormat = new TimeParser(
				new SpringSafeCalendar(cal)); 
		
		assertEquals("2005-10-30 00:55",
				resultFormat.format(
						timeFormat.parse("00:55")));
		
		assertEquals("2005-10-30 01:00",
				resultFormat.format(
						timeFormat.parse("01:00")));
		
		assertEquals("2005-10-30 01:30",
				resultFormat.format(
						timeFormat.parse("01:30")));
		
		assertEquals("2005-10-30 02:00",
				resultFormat.format(
						timeFormat.parse("02:00")));
		
		assertEquals("2005-10-30 02:05",
				resultFormat.format(
						timeFormat.parse("02:05")));
	}
}
