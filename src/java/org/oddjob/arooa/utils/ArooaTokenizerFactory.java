package org.oddjob.arooa.utils;

/**
 * Something capable of providing a tokenizer.
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
