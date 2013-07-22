package org.oddjob.arooa.utils;


public class FlexibleDelimiterFactory implements ArooaDelimiterFactory {

	private String delimiter;
	
	private Character quote;
	
	private Character escape;
	
	private boolean alwaysQuote;
	
	public ArooaDelimiter newDelimiter() {
		
		if (delimiter == null) {
			throw new IllegalStateException("No Delimiter.");
		}

		if (quote == null) {
			return new SimpleDelimiter(delimiter);
		}
		
		
		if (alwaysQuote) {
			return new QuoteDelimiter(delimiter, quote, escape);	
		}
		
		return new QuoteIfDelimiter(delimiter, quote, escape);		
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Character getQuote() {
		return quote;
	}

	public void setQuote(Character quote) {
		this.quote = quote;
	}

	public Character getEscape() {
		return escape;
	}

	public void setEscape(Character escape) {
		this.escape = escape;
	}

	public boolean isAlwaysQuote() {
		return alwaysQuote;
	}

	public void setAlwaysQuote(boolean alwaysQuote) {
		this.alwaysQuote = alwaysQuote;
	}
}
