package org.oddjob.arooa.utils;

import java.text.ParseException;

import junit.framework.TestCase;

public class ArooaTokenizerTest extends TestCase {
	
	
	public void testSimpleTokenizer() throws ParseException {
	
		FlexibleTokenizerFactory test = new FlexibleTokenizerFactory();
		test.setDelimiter(",");

		ArooaTokenizer tokenizer = test.newTokenizer();
		
		String[] results = tokenizer.parse("The,quick,brown,fox");
		
		assertEquals(results.length, 4);
		
		assertEquals("The", results[0]);
		assertEquals("quick", results[1]);
		assertEquals("brown", results[2]);
		assertEquals("fox", results[3]);
	}
	
	public void testEscapedTokenizer() throws ParseException {
		
		FlexibleTokenizerFactory test = new FlexibleTokenizerFactory();
		test.setRegexp("\\s+");
		test.setEscape('~');
		
		ArooaTokenizer tokenizer = test.newTokenizer();
		
		String[] results = tokenizer.parse("~~The ~~quick~~\nbrown~ fox");
		
		assertEquals(3, results.length);
		
		assertEquals("~The", results[0]);
		assertEquals("~quick~", results[1]);
		assertEquals("brown fox", results[2]);
	}
	
	public void testQuoteAndEscapedTokenizer() throws ParseException {
		
		FlexibleTokenizerFactory test = new FlexibleTokenizerFactory();
		test.setRegexp("\\s+");
		test.setEscape('~');
		test.setQuote('\'');
		
		ArooaTokenizer tokenizer = test.newTokenizer();
		
		String[] results = tokenizer.parse("'The' ~'quick~'\n\tbrown~ fox");
		
		assertEquals(4, results.length);
		
		assertEquals("The", results[0]);
		assertEquals("~'quick~'", results[1]);
		assertEquals("brown~", results[2]);		
		assertEquals("fox", results[3]);		
	}
	
	public void testQuoteQuoteTokenizer() throws ParseException {
		
		FlexibleTokenizerFactory test = new FlexibleTokenizerFactory();
		test.setRegexp("\\s+");
		test.setQuote('\'');
		
		ArooaTokenizer tokenizer = test.newTokenizer();
		
		String[] results = tokenizer.parse("'The' '''quick'''\t'brown fox'");
		
		assertEquals(3, results.length);
		
		assertEquals("The", results[0]);
		assertEquals("'quick'", results[1]);
		assertEquals("brown fox", results[2]);
	}
}
