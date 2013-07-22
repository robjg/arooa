package org.oddjob.arooa.utils;

/**
 * Quote if the text contains the delimiter.
 * 
 * @author rob
 *
 */
public class QuoteIfDelimiter extends QuoteDelimiter {

	public QuoteIfDelimiter(String delimiter, 
			Character quote) {
		super(delimiter, quote);
	}
	
	public QuoteIfDelimiter(String delimiter, 
			Character quote, Character escape) {
		
		super(delimiter, quote, escape);
	}
	
	@Override
	protected String maybeQuote(String text) {
		
		if (text.contains(getQuote().toString())) {
			return super.maybeQuote(text);
		}
		else {
			return text;
		}
	}
}
