package org.oddjob.arooa.utils;

import junit.framework.TestCase;

public class DelimitedWriterFactoryTest extends TestCase {

	Object[] line = new Object[] { 
			"Apple", "Crisp, 'Sweet' Sometimes", 22.7 };
	
	
	public void testQuotedNumbersAndText() {
		
		DelimitedFormatterFactory test = new DelimitedFormatterFactory();
		test.setDelimiter(",");
		test.setQuote('\'');
		
		DelimitedFormatter formatter = test.newWriter();
		
		String result = formatter.format(line);

		assertEquals("Apple,'Crisp, ''Sweet'' Sometimes',22.7", 
				result);
	}
	
	public void testQuotedAndEscapedNumbersAndText() {
		
		DelimitedFormatterFactory test = new DelimitedFormatterFactory();
		test.setDelimiter(",");
		test.setQuote('\'');
		test.setEscape('\\');
		
		DelimitedFormatter formatter = test.newWriter();
		
		String result = formatter.format(line);

		assertEquals("Apple,'Crisp, \\'Sweet\\' Sometimes',22.7", 
				result);
	}
	
	public void testAlwaysQuotedAndEscapedNumbersAndText() {
		
		DelimitedFormatterFactory test = new DelimitedFormatterFactory();
		test.setDelimiter(",");
		test.setQuote('\'');
		test.setEscape('\\');
		test.setAlwaysQuote(true);
		
		DelimitedFormatter formatter = test.newWriter();
		
		String result = formatter.format(line);

		assertEquals("'Apple','Crisp, \\'Sweet\\' Sometimes',22.7", 
				result);
	}
	
	
	public void testNoQuoteNumbersAndText() {
		
		DelimitedFormatterFactory test = new DelimitedFormatterFactory();
		test.setDelimiter(",");
		
		DelimitedFormatter formatter = test.newWriter();
		
		String result = formatter.format(line);

		assertEquals("Apple,Crisp, 'Sweet' Sometimes,22.7", 
				result);
	}
	
	public void testNulls() {
		
		DelimitedFormatterFactory test = new DelimitedFormatterFactory();
		test.setDelimiter(",");
		
		DelimitedFormatter formatter = test.newWriter();
		
		String result = formatter.format(new Object[] { null, null, null });

		assertEquals(",,", result);
	}
}
