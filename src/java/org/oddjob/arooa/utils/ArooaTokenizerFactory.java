package org.oddjob.arooa.utils;

/**
 * Something capable of providing an {@link ArooaTokenizer}.
 * 
 * @author rob
 *
 */
public interface ArooaTokenizerFactory {

	/**
	 * Provide a tokenizer.
	 * 
	 * @return
	 */
	public ArooaTokenizer newTokenizer();

}
