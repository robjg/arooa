package org.oddjob.arooa.utils;

import org.junit.Test;

import org.junit.Assert;

public class FlexibleDelimiterFactoryTest extends Assert {

	Object[] line = new Object[] { 
			"Apple", "Crisp, 'Sweet' Sometimes", 22.7 };
	
	
   @Test
	public void testQuotedNumbersAndText() {
		
		FlexibleDelimiterFactory test = new FlexibleDelimiterFactory();
		test.setDelimiter(",");
		test.setQuote('\'');
		
		ArooaDelimiter formatter = test.newDelimiter();
		
		String result = formatter.delimit(line);

		assertEquals("Apple,'Crisp, ''Sweet'' Sometimes',22.7", 
				result);
	}
	
   @Test
	public void testQuotedAndEscapedNumbersAndText() {
		
		FlexibleDelimiterFactory test = new FlexibleDelimiterFactory();
		test.setDelimiter(",");
		test.setQuote('\'');
		test.setEscape('\\');
		
		ArooaDelimiter formatter = test.newDelimiter();
		
		String result = formatter.delimit(line);

		assertEquals("Apple,'Crisp, \\'Sweet\\' Sometimes',22.7", 
				result);
	}
	
   @Test
	public void testAlwaysQuotedAndEscapedNumbersAndText() {
		
		FlexibleDelimiterFactory test = new FlexibleDelimiterFactory();
		test.setDelimiter(",");
		test.setQuote('\'');
		test.setEscape('\\');
		test.setAlwaysQuote(true);
		
		ArooaDelimiter formatter = test.newDelimiter();
		
		String result = formatter.delimit(line);

		assertEquals("'Apple','Crisp, \\'Sweet\\' Sometimes',22.7", 
				result);
	}
	
	
   @Test
	public void testNoQuoteNumbersAndText() {
		
		FlexibleDelimiterFactory test = new FlexibleDelimiterFactory();
		test.setDelimiter(",");
		
		ArooaDelimiter formatter = test.newDelimiter();
		
		String result = formatter.delimit(line);

		assertEquals("Apple,Crisp, 'Sweet' Sometimes,22.7", 
				result);
	}
	
   @Test
	public void testNulls() {
		
		FlexibleDelimiterFactory test = new FlexibleDelimiterFactory();
		test.setDelimiter(",");
		
		ArooaDelimiter formatter = test.newDelimiter();
		
		String result = formatter.delimit(new Object[] { null, null, null });

		assertEquals(",,", result);
	}
}
