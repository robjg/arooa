package org.oddjob.arooa.utils;

import org.junit.Test;

import java.text.ParseException;

import org.junit.Assert;

public class DurationFormatterTest extends Assert {

   @Test
	public void testDays() throws ParseException {
		
		DurationFormatter test = new DurationFormatter();
		
		long result;
		
		result = test.parseTime("5d");
		assertEquals(5 * DurationFormatter.ONE_DAY, result);
		result = test.parseTime("25d");
		assertEquals(25 * DurationFormatter.ONE_DAY, result);
		result = test.parseTime(" 025d ");
		assertEquals(25 * DurationFormatter.ONE_DAY, result);
		
		result = test.parseTime("1d4h");
		assertEquals(DurationFormatter.ONE_DAY +
				4 * DurationFormatter.ONE_HOUR, result);
		result = test.parseTime("1d 14h");
		assertEquals(DurationFormatter.ONE_DAY +
				14 * DurationFormatter.ONE_HOUR, result);
		
		result = test.parseTime("1d4m");
		assertEquals(DurationFormatter.ONE_DAY +
				4 * DurationFormatter.ONE_MINUTE, result);
		result = test.parseTime("1d 14m ");
		
		result = test.parseTime("1d4s");
		assertEquals(DurationFormatter.ONE_DAY +
				4 * DurationFormatter.ONE_SECOND, result);
		result = test.parseTime("1d 14s ");
		assertEquals(DurationFormatter.ONE_DAY +
				14 * DurationFormatter.ONE_SECOND, result);
		
		result = test.parseTime("1d4ms");
		assertEquals(DurationFormatter.ONE_DAY + 4 , result);
		result = test.parseTime("1d 14ms ");
		assertEquals(DurationFormatter.ONE_DAY + 14 , result);
		result = test.parseTime("1d4");
		assertEquals(DurationFormatter.ONE_DAY + 4 , result);
		result = test.parseTime("1d 14 ");
		assertEquals(DurationFormatter.ONE_DAY + 14 , result);
		
		result = test.parseTime("1d2h3m4s5ms");
		assertEquals(DurationFormatter.ONE_DAY + 
				2 * DurationFormatter.ONE_HOUR +
				3 * DurationFormatter.ONE_MINUTE +
				4 * DurationFormatter.ONE_SECOND + 
				5, result);
		
		result = test.parseTime(" 1d 2h 3m 4s 5ms ");
		assertEquals(DurationFormatter.ONE_DAY + 
				2 * DurationFormatter.ONE_HOUR +
				3 * DurationFormatter.ONE_MINUTE +
				4 * DurationFormatter.ONE_SECOND + 
				5, result);
	}
	
   @Test
	public void testHours() throws ParseException {
		
		DurationFormatter test = new DurationFormatter();
		
		long result;
		
		result = test.parseTime("5h");
		assertEquals(5 * DurationFormatter.ONE_HOUR, result);
		result = test.parseTime("25h");
		assertEquals(25 * DurationFormatter.ONE_HOUR, result);
		result = test.parseTime(" 025h ");
		assertEquals(25 * DurationFormatter.ONE_HOUR, result);
		
		result = test.parseTime("1h4m");
		assertEquals(DurationFormatter.ONE_HOUR +
				4 * DurationFormatter.ONE_MINUTE, result);
		result = test.parseTime("1h 14m ");
		assertEquals(DurationFormatter.ONE_HOUR +
				14 * DurationFormatter.ONE_MINUTE, result);
				
		result = test.parseTime("2h3m4s5ms");
		assertEquals(
				2 * DurationFormatter.ONE_HOUR +
				3 * DurationFormatter.ONE_MINUTE +
				4 * DurationFormatter.ONE_SECOND + 
				5, result);
	}
	
   @Test
	public void testMilliseconds() throws ParseException {
		
		DurationFormatter test = new DurationFormatter();
		
		long result;
		
		result = test.parseTime("5ms");
		assertEquals(5, result);
		result = test.parseTime("25ms");
		assertEquals(25 , result);
		result = test.parseTime(" 025ms ");
		assertEquals(25, result);
		
		result = test.parseTime("5");
		assertEquals(5, result);
		result = test.parseTime("25");
		assertEquals(25 , result);
		result = test.parseTime(" 025 ");
		assertEquals(25, result);
	}
	
   @Test
	public void testInvalids() {
		
		DurationFormatter test = new DurationFormatter();
		
		try {
			test.parseTime("1 1");
			fail("Should be invalid");
		} catch (ParseException e) {
			// expected
		}
		
		try {
			test.parseTime("1h 5d");
			fail("Should be invalid");
		} catch (ParseException e) {
			// expected
		}
		
		try {
			test.parseTime("1 h");
			fail("Should be invalid");
		} catch (ParseException e) {
			// expected
		}
	}
}
