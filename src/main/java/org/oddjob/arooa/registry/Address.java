/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;

import java.io.Serializable;

/**
 * A address uniquely identifies a component by its server
 * and path. 
 */
public class Address implements Serializable {
	private static final long serialVersionUID = 20051117;
	
	/** The server */
	private final ServerId serverId;
	
	/** The path */
	private final Path path;
	
	/**
	 * Create a local address.
	 * 
	 * @param path The path to the component.
	 */
	public Address(Path path) {
		this(ServerId.local(), path);
	}

	/**
	 * Create an address of an object on a server.
	 * 
	 * @param serverId The server id.
	 * @param path The path.
	 */
	public Address(ServerId serverId, Path path) {
		if (path == null) {
			throw new NullPointerException("Path must not be null.");
		}
		if (serverId == null) {
			throw new NullPointerException("URL must not be null.");
		}
		this.path = path;
		this.serverId = serverId;
	}
	
	/**
	 * Get the path.
	 * 
	 * @return The path.
	 */
	public Path getPath() {
		return path;
	}
	
	/**
	 * Get the ServerId.
	 * 
	 * @return The ServerId.
	 */
	public ServerId getServerId() {
		return serverId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (! (obj instanceof Address)) {
			return false;
		}
		
		Address other = (Address) obj;
		
		if (!other.serverId.equals(serverId)) {
			return false;
		}
		
		return other.path.equals(path);
	}
	
	@Override
	public int hashCode() {
		return serverId.hashCode() + path.hashCode();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return serverId.toString() + ":" + path.toString();
	}
	
	/**
	 * Utility function for debugs.
	 * 
	 * @param addresses Array of address.
	 * @return A string.
	 */
	public static String arrayAsString(Address[] addresses) {
		if (addresses.length == 0) {
			return "[No Addresses]";
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < addresses.length; ++i) {
			buf.append("[");
			buf.append(addresses[i].toString());
			buf.append("]");
		}
		return buf.toString();
	}
}
