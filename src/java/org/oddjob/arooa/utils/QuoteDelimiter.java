package org.oddjob.arooa.utils;

/**
 * Always quote text delimiter.
 * 
 * @author rob
 *
 */
public class QuoteDelimiter extends SimpleDelimiter {

	private final Character quote;
	
	private final Character escape;
	
	public QuoteDelimiter(String delimiter, 
			Character quote) {
		this(delimiter, quote, quote);
	}
		
	public QuoteDelimiter(String delimiter, 
			Character quote, Character escape) {
		
		super(delimiter);
		
		if (quote == null) {
			throw new NullPointerException("No quote.");
		}
		this.quote = quote;
		
		if (escape == null) {
			this.escape = quote;
		}
		else {
			this.escape = escape;
		}
	}
	
	@Override
	protected String maybeQuote(String text) {
		
		StringBuilder builder = new StringBuilder();
		builder.append(quote);
		builder.append(text.replace(quote.toString(), 
				new String(new char[] { escape.charValue(), quote.charValue() })));
		builder.append(quote);
		
		return builder.toString();
	}
	
	public Character getQuote() {
		return quote;
	}
	
	public Character getEscape() {
		return escape;
	}
}
