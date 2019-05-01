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
		super(message(message, location));
		this.location = location;
	}
	
	public ArooaParseException(String message, Location location, Throwable t) {	
		super(message(message, location), t);
		this.location = location;
	}

	static String message(String message, Location location) {
		return (location == null ?
				"(Unknown Location): " : location.toString()) + " " +
				message;
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
