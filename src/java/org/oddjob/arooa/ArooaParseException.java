package org.oddjob.arooa;

import org.oddjob.arooa.parsing.Location;

/**
 * An exception used when creation fails.
 */
public class ArooaParseException extends Exception {
	private static final long serialVersionUID = 20061110;

	/** Location in the build file where the exception occurred */
	private final Location location;

	public ArooaParseException(String message, Location location) {
		super(message);
		this.location = location;
	}
	
	public ArooaParseException(String message, Location location, Throwable t) {	
		super(message, t);
		this.location = location;
	}
	
 	
	/**
	 * Returns the location of the error and the error message.
	 *
	 * @return the location of the error and the error message
	 */
	public String toString() {
		return (location == null ? 
				"(Unknown Location): " : location.toString()) + 
		getMessage();
	}


	/**
	 * Returns the file location where the error occurred.
	 *
	 * @return the file location where the error occurred.
	 */
	public Location getLocation() {
		return location;
	}

}
