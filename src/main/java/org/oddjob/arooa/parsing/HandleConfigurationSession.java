package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.ModificationRefusedException;

/**
 * A {@link ConfigurationSession} that works with an {@link ArooaSession}
 * and a {@link ConfigurationHandle} that was the result of a parse
 * that acted on the session.
 * 
 * @author rob
 *
 */
public class HandleConfigurationSession implements ConfigurationSession {

	private final ArooaSession session;
	
	private final ConfigurationHandle<?> handle;

	private final ConfigurationSessionSupport propertySupport;
	
	private boolean modified;
	
	class ChangeListener<P extends ParseContext<P>>
			implements ConfigurationNodeListener<P> {
		
		public void childInserted(ConfigurationNodeEvent<P> nodeEvent) {
			ConfigurationNode<P> node = nodeEvent.getChild();
			node.addNodeListener(new ChangeListener<>());
			setModified(true);
		}
		public void childRemoved(ConfigurationNodeEvent<P> nodeEvent) {
			setModified(true);
		}
		public void insertRequest(ConfigurationNodeEvent<P> nodeEvent)
				throws ModificationRefusedException {
		}
		public void removalRequest(ConfigurationNodeEvent<P> nodeEvent)
				throws ModificationRefusedException {
		}
	}
	
	/**
	 * Create a new instance. The underlying session is taken from the
	 * document context of the handle.
	 * 
	 * @param handle
	 */
	public HandleConfigurationSession(ConfigurationHandle<ArooaContext> handle) {
		this(handle.getDocumentContext().getSession(), handle);
	}
	
	/**
	 * Create a new Instance. This constructor allows a different session
	 * to be specified - not sure now why this was required.
	 * 
	 * @param session The underlying session to use.
	 * @param handle The configuration handle.
	 */
	public HandleConfigurationSession(ArooaSession session, 
			ConfigurationHandle<?> handle) {
		
		this.session = session;
		this.handle = handle;
		
		// Not we need add to the parent because the document context
		// might be replaced.
		handle.getDocumentContext().getParent().getConfigurationNode().addNodeListener(
				new ChangeListener<>());
		
		this.propertySupport = new ConfigurationSessionSupport(this);
	}
	
	public DragPoint dragPointFor(Object component) {

		ArooaContext context = session.getComponentPool().contextFor(
				component);
		
		if (context == null) {
			return null;
		}
		
		return new DragContext(context);
	}
	
	protected void setModified(boolean modified) {
		// are we still in the constructor building the node listeners?
		if (propertySupport == null) {
			return;
		}
		
		if (this.modified == modified) {
			return;
		}
		
		this.modified = modified;
		
		if (modified) {
			propertySupport.modified();
		}
		else {
			propertySupport.saved();
		}
	}
	
	public boolean isModified() {
		return modified;
	}
	
	public void addSessionStateListener(SessionStateListener listener) {
		propertySupport.addSessionStateListener(listener);
	}
	public void removeSessionStateListener(SessionStateListener listener) {
		propertySupport.removeSessionStateListener(listener);
	}
		
	public void save() throws ArooaParseException {
		handle.save();
		setModified(false);
	}
	
	public ArooaDescriptor getArooaDescriptor() {
		return session.getArooaDescriptor();
	}
}
