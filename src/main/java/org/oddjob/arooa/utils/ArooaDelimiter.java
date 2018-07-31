package org.oddjob.arooa.utils;

/**
 * Something that is able to format arrays into delimited text.
 * 
 * @author rob
 *
 */
public interface ArooaDelimiter {

	/**
	 * Format the array.
	 * 
	 * @param values The array. Can contain null but not be null.
	 * 
	 * @return The formatted text.
	 */
	public String delimit(Object[] values);
}
