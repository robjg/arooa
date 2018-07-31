/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Encapsulate a server identifier as an object. Typically the server id
 * will be the URL of the server.
 * 
 * @author Rob Gordon.
 */
public class ServerId implements Serializable {
	private static final long serialVersionUID= 20051117;
	
	/** An object which represents the local Oddjob. */
	private static final ServerId local = new ServerId("local");
	
	/** The serverId string. */
	private final String serverId;
	
	/**
	 * Create a ServerId for the give server id String.
	 * 
	 * @param serverId The server id String.
	 */
	public ServerId(String serverId) {
		if (serverId == null) {
			throw new NullPointerException("Server Id must not be null.");
		}
		this.serverId = serverId;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return serverId;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ServerId)) {
			return false;
		}
		ServerId other = (ServerId) o;
		return this.serverId.equals(other.serverId);
		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return serverId.hashCode();
	}
	
	/**
	 * Get the local ServerId.
	 * 
	 * @return The local ServerId.
	 */
	public static ServerId local() {
		return local;
	}
	
	private Object readResolve() throws ObjectStreamException {
		if (serverId.equals(local.serverId)) {
			return local;
		}
		else {
			return this;
		}
	}

}
