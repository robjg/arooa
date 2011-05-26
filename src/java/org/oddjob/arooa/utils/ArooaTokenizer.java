package org.oddjob.arooa.utils;

import java.text.ParseException;

/**
 * Something that can tokenize a string.
 * 
 * @author rob
 *
 */
public interface ArooaTokenizer {

	/**
	 * Parse the given string into it's parts.
	 * 
	 * @param text
	 * @return
	 * @throws ParseException
	 */
	public String[] parse(String text) throws ParseException;
}
