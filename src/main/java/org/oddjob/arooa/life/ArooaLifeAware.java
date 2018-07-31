package org.oddjob.arooa.life;

/**
 * Implementers will be informed of framework life cycle events.
 * 
 * @author rob
 *
 */
public interface ArooaLifeAware {

	/**
	 * The configuration for the bean has been initialised. 
	 * Constant properties and
	 * element properties will have been injected.
	 */
	public void initialised();
	
	/**
	 * The configuration for the bean has been fully configured. 
	 * Runtime properties will 
	 * have been injected.
	 */
	public void configured();
	
	/**
	 * The configuration for the bean is about to be destroyed. A
	 * bean can use this method to free resources.
	 */
	public void destroy();	
}
