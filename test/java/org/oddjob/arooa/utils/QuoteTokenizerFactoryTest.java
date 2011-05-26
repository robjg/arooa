package org.oddjob.arooa.utils;

import java.text.ParseException;

import junit.framework.TestCase;

public class QuoteTokenizerFactoryTest extends TestCase {

	public void testPattern() {
		
		QuoteTokenizerFactory test = new QuoteTokenizerFactory(",", '"', '~');

		assertEquals("(?:" +
				"\\Q\"\\E" +
				"((?>[^\\Q~\\E\\Q\"\\E]|" +
					"\\Q~\\E[^\\Q~\\E\\Q\"\\E]|" +
					"\\Q~\\E\\Q~\\E|" +
					"\\Q~\\E\\Q\"\\E" + ")*?)" +
				"(?:\\Q\"\\E,|\\Q\"\\E?($)))|" + 
				"(?:(.*?)(?:,|($)))",
				test.getPattern());
	}
	
	public void testSimple() throws ParseException {
		
		QuoteTokenizerFactory test = new QuoteTokenizerFactory(",", '"', '~');

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
	
	public void testQuotesAndEscapes() throws ParseException {
		
		QuoteTokenizerFactory test = new QuoteTokenizerFactory(",", '"', '\\');

		ArooaTokenizer tokenizer = test.newTokenizer();
		
		String[] results;
		
		results = tokenizer.parse("\"a,a\",b,c\\,c");
		
		assertEquals("a,a", results[0]);
		assertEquals("b", results[1]);
		assertEquals("c\\", results[2]);
		assertEquals("c", results[3]);
		assertEquals(4, results.length);
		
		results = tokenizer.parse("\\\\\\\"a\\\\\\\"");
		assertEquals(             "\\\\\\\"a\\\\\\\"", results[0]);
		assertEquals(1, results.length);
		
		results = tokenizer.parse("\\,");
		assertEquals(2, results.length);
		assertEquals("\\", results[0]);
		assertEquals("", results[1]);
		
		results = tokenizer.parse("\"\",\"\",\"\"");
		assertEquals("", results[0]);
		assertEquals("", results[1]);
		assertEquals("", results[2]);
		assertEquals(3, results.length);
				
		results = tokenizer.parse("\"a\",\"\",\"\"");
		assertEquals("a", results[0]);
		assertEquals("", results[1]);
		assertEquals("", results[2]);
		assertEquals(3, results.length);
		
		results = tokenizer.parse("\"\",\"\",\"c,c\"");
		assertEquals("", results[0]);
		assertEquals("", results[1]);
		assertEquals("c,c", results[2]);
		assertEquals(3, results.length);

		results = tokenizer.parse("\"John Smith\",\"London\",\"32\"");
		assertEquals("John Smith", results[0]);
		assertEquals("London", results[1]);
		assertEquals("32", results[2]);
		assertEquals(3, results.length);
	}
	
	public void testEscapedEscapesAndEscapedQuote() throws ParseException {
		
		QuoteTokenizerFactory test = new QuoteTokenizerFactory("\\s+", '\'', '~');

		ArooaTokenizer tokenizer = test.newTokenizer();
		
		String[] results;
		
		results = tokenizer.parse("'~~~'Fox~~~''");
		assertEquals("~'Fox~'", results[0]);
		assertEquals(1, results.length);
		

	}
	
	public void testWhilteSpaceDelimiter() throws ParseException {
		
		QuoteTokenizerFactory test = new QuoteTokenizerFactory("\\s+", '"', '\\');

		ArooaTokenizer tokenizer = test.newTokenizer();
		
		String[] results;
		
		results = tokenizer.parse("The quick brown fox");
		
		assertEquals("The", results[0]);
		assertEquals("quick", results[1]);
		assertEquals("brown", results[2]);
		assertEquals("fox", results[3]);
		assertEquals(4, results.length);
		
		results = tokenizer.parse("\\ ");
		assertEquals(2, results.length);
		assertEquals("\\", results[0]);
		assertEquals("", results[1]);
		
		results = tokenizer.parse("\"\"\t\t\"\"\t\t\"\"");
		assertEquals("", results[0]);
		assertEquals("", results[1]);
		assertEquals("", results[2]);
		assertEquals(3, results.length);
				
		results = tokenizer.parse("\"a\"   \"\"    \"\"");
		assertEquals("a", results[0]);
		assertEquals("", results[1]);
		assertEquals("", results[2]);
		assertEquals(3, results.length);
		
		results = tokenizer.parse("\"\"\n\"\"\n\"c  c\"");
		assertEquals("", results[0]);
		assertEquals("", results[1]);
		assertEquals("c  c", results[2]);
		assertEquals(3, results.length);

		results = tokenizer.parse("\"John Smith\"\t\"London\"\t32");
		assertEquals("John Smith", results[0]);
		assertEquals("London", results[1]);
		assertEquals("32", results[2]);
		assertEquals(3, results.length);
		
		results = tokenizer.parse("The quick brown fox\t");
		
		assertEquals("The", results[0]);
		assertEquals("quick", results[1]);
		assertEquals("brown", results[2]);
		assertEquals("fox", results[3]);
		assertEquals("", results[4]);
		assertEquals(5, results.length);
		
		results = tokenizer.parse("\tThe quick brown fox\t");
		
		assertEquals("", results[0]);
		assertEquals("The", results[1]);
		assertEquals("quick", results[2]);
		assertEquals("brown", results[3]);
		assertEquals("fox", results[4]);
		assertEquals("", results[5]);
		assertEquals(6, results.length);
	}
	
	public void testQuotesForEscapes() throws ParseException {
		
		QuoteTokenizerFactory test = new QuoteTokenizerFactory(",", '\'', '\'');

		ArooaTokenizer tokenizer = test.newTokenizer();
		
		String[] results;
		
		results = tokenizer.parse("'a,a',b,'''c,c'''");
		
		assertEquals("a,a", results[0]);
		assertEquals("b", results[1]);
		assertEquals("'c,c'", results[2]);
		assertEquals(3, results.length);
		
		results = tokenizer.parse("'''''a'''''");
		assertEquals("''a''", results[0]);
		assertEquals(1, results.length);
		
		results = tokenizer.parse("',");
		assertEquals(1, results.length);
		assertEquals(",", results[0]);
		
		results = tokenizer.parse("'''','''',''''");
		assertEquals("'", results[0]);
		assertEquals("'", results[1]);
		assertEquals("'", results[2]);
		assertEquals(3, results.length);
				
		results = tokenizer.parse("'a','',''''");
		assertEquals("a", results[0]);
		assertEquals("", results[1]);
		assertEquals("'", results[2]);
		assertEquals(3, results.length);
		
		results = tokenizer.parse(",,'c,c'");
		assertEquals("", results[0]);
		assertEquals("", results[1]);
		assertEquals("c,c", results[2]);
		assertEquals(3, results.length);

		results = tokenizer.parse("'John O''Smith',London,''32");
		assertEquals("John O'Smith", results[0]);
		assertEquals("London", results[1]);
		assertEquals("'32", results[2]);
		assertEquals(3, results.length);
	}
	
}
