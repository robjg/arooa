package org.oddjob.arooa.utils;

import java.util.regex.Pattern;

/**
 * Provide an {@link ArooaTokenizer} for just a delimiter as a regular 
 * expression. This is just a wrapper around 'split'.
 * 
 * @author rob
 *
 */
public class SimpleTokenizerFactory implements ArooaTokenizerFactory {

	private final Pattern pattern;
	
	public SimpleTokenizerFactory(String delimiterRegexp) {
		
		pattern = Pattern.compile(delimiterRegexp);
	}
	
	@Override
	public ArooaTokenizer newTokenizer() {
		
		return new ArooaTokenizer() {;
		
			@Override
			public String[] parse(String text) {
				
				return pattern.split(text, -1);
			}
		};
	}
}
