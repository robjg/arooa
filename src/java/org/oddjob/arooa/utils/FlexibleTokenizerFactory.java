package org.oddjob.arooa.utils;

import java.util.regex.Pattern;

/**
 * Provide an {@link ArooaTokenizer} dependent on the properties set. The
 * delimiter can be provided either as a plain string or as a regular
 * expression. The quote and escape characters are optional. It quote is
 * provided but escape isn't, then the quote character will also be used as 
 * the escape character.
 * 
 * @author rob
 *
 */
public class FlexibleTokenizerFactory implements ArooaTokenizerFactory {

	private String delimiter;
	
	private boolean regexp;
	
	private Character quote;
	
	private Character escape; 	
	
	public ArooaTokenizer newTokenizer() {
		
		if (delimiter == null) {
			throw new IllegalStateException("No Delimiter.");
		}
		
		String regexp;
		if (this.regexp) {
			regexp = delimiter;
		}
		else {
			regexp = Pattern.quote(delimiter);
		}
		
		if (quote == null) {
			if (escape == null) {
				return new SimpleTokenizerFactory(
						regexp).newTokenizer();
			}
			else {
				return new EscapeTokenizerFactory(
						regexp, escape).newTokenizer();
			}
		}
		else {
			if (escape == null) {
				return new QuoteTokenizerFactory(
						regexp, quote, quote).newTokenizer();
			}
			else {
				return new QuoteTokenizerFactory(
						regexp, quote, escape).newTokenizer();
			}
		}
	}
	

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public boolean isRegexp() {
		return regexp;
	}

	public void setRegexp(boolean regexp) {
		this.regexp = regexp;
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
}
