package org.oddjob.arooa.utils;

/**
 * Something capable of providing an {@link ArooaDelimiter}.
 * 
 * @author rob
 *
 */
public interface ArooaDelimiterFactory {

	/**
	 * Provide a delimiter. 
	 * 
	 * @return A delimiter. Never null.
	 */
	public ArooaDelimiter newDelimiter();
}
