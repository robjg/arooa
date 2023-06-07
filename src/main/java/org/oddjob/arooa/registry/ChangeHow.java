package org.oddjob.arooa.registry;

/**
 * How changes to the {@link ComponentPool} should be coordinated.
 * 
 * @author rob
 *
 */
public enum ChangeHow {

	/**
	 * Create a new transaction for changes to the registry. No
	 * existing transaction is expected to be in progress.
	 */
	FRESH,
	
	/**
	 * An existing transaction is expected to be in progress to
	 * add changes to.
	 */
	AGAIN,
	
	/**
	 * If a transaction is in progress then add changes to it, if
	 * one isn't in progress then create a fresh one.
	 */
	EITHER,
	
	/**
	 * If a transaction is in progress then add changes to it, 
	 * otherwise don't start a new one.
	 */
	MAYBE,
}
