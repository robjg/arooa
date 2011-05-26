package org.oddjob.arooa.life;


/**
 * An exception to use when persistence fails.
 * 
 * @author Rob Gordon
 */

public class ComponentPersistException extends Exception {
	private static final long serialVersionUID = 20051229;
	
	/**
	 * Constructor.
	 */
	
	public ComponentPersistException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0 An exception message.
	 */
	
	public ComponentPersistException(String arg0) {
		super(arg0);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0 An exception message.
	 * @param arg1 A nested exception.
	 */
	
	public ComponentPersistException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Constructor.
	 * 
	 * @param arg0 A nested exception.
	 */
	
	public ComponentPersistException(Throwable arg0) {
		super(arg0);
	}

}
