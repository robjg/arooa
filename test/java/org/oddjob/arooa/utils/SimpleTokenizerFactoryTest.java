package org.oddjob.arooa.utils;

import java.text.ParseException;

import junit.framework.TestCase;

public class SimpleTokenizerFactoryTest extends TestCase {

	public void testSimpleCommaExamples() throws ParseException {
		
		ArooaTokenizerFactory test = new SimpleTokenizerFactory(",");
		
		ArooaTokenizer tokenizer = test.newTokenizer();
		
		String[] results;
		
		results = tokenizer.parse("a,b,c");
		
		assertEquals("a", results[0]);
		assertEquals("b", results[1]);
		assertEquals("c", results[2]);
		assertEquals(3, results.length);
		
		results = tokenizer.parse("a");
		assertEquals("a", results[0]);
		assertEquals(1, results.length);
		
		results = tokenizer.parse("");
		assertEquals(1, results.length);
		
		results = tokenizer.parse(",,");
		assertEquals("", results[0]);
		assertEquals("", results[1]);
		assertEquals("", results[2]);
		assertEquals(3, results.length);
				
		results = tokenizer.parse("a,,");
		assertEquals("a", results[0]);
		assertEquals("", results[1]);
		assertEquals("", results[2]);
		assertEquals(3, results.length);
		
		results = tokenizer.parse(",,c");
		assertEquals("", results[0]);
		assertEquals("", results[1]);
		assertEquals("c", results[2]);
		assertEquals(3, results.length);

		results = tokenizer.parse("John Smith,London,32");
		assertEquals("John Smith", results[0]);
		assertEquals("London", results[1]);
		assertEquals("32", results[2]);
		assertEquals(3, results.length);
	}
}
