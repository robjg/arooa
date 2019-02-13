package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;

/**
 * Something that is able to provide everything required to edit
 * an {@link ArooaConfiguration}.
 * <p>
 * A ConfigurationSession is intended to be a minimal derivation of
 * an {@link ArooaSession} that is able to provide enough to 
 * edit a configuration either locally or on a remote server.
 * 
 * @author rob
 *
 */
public interface ConfigurationSession {

	/**
	 * Provide a {@link DragPoint} for the given component.
	 * 
	 * @param component The component.
	 * @return A DragPoint, or null if the component is not
	 * a member of the session.
	 */
	DragPoint dragPointFor(Object component);

	/**
	 * Has the configuration been modified.
	 * 
	 * @return true/false. True if modified.
	 */
	boolean isModified();
	
	/**
	 * Add a {@link SessionStateListener} listener. 
	 * 
	 * @param listener The listener.
	 * 
	 */
   void addSessionStateListener(
        SessionStateListener listener);

   /**
    * Remove a {@link SessionStateListener} listener.
    * 
    * @param listener The listener.
    * 
    */
   void removeSessionStateListener(
                SessionStateListener listener);
	
	/**
	 * Save the configuration.
	 * 
	 * @throws ArooaParseException If save failed. (Is this the right Exception)?
	 */
	void save()
	throws ArooaParseException;
	
	/**
	 * Get the {@link ArooaDescriptor} for editing the configuration.
	 * 
	 * @return An ArooaDescriptor. Never Null.
	 */
	ArooaDescriptor getArooaDescriptor();

}
