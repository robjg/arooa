package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.parsing.ConfigOwnerEvent.Change;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListenerAdapter;

/**
 * A {@link ConfigurationSession} based on the parsing {@link ArooaContext} 
 * of an element.
 * <p>
 * It is assumed that the component is within the hierarchy of another
 * {@link ConfigurationOwner} that will be the root of the hierarchy and
 * this will provide details of when the hierarchy is modified and saved. 
 * 
 * @author rob
 *
 */
public class ContextConfigurationSession implements ConfigurationSession {

	private final ArooaSession session;
	
	private final ConfigurationSessionSupport propertySupport;
	
	private boolean modified;
		
	private ConfigurationOwner parentOwner;
	
	private ConfigurationSession parentSession;
	
	private final SessionStateListener sessionStateListener =
			new SessionStateListener() {
		
		@Override
		public void sessionSaved(ConfigSessionEvent event) {
			setModified(false);
		}
		
		@Override
		public void sessionModifed(ConfigSessionEvent event) {
			setModified(true);
		}
	};
	
	private final OwnerStateListener ownerStateListener =
			new OwnerStateListener() {
	
		@Override
		public void sessionChanged(ConfigOwnerEvent event) {
			if (Change.SESSION_CREATED == event.getChange()) {

				parentOwner = event.getSource();
				
				parentSession = 
						parentOwner.provideConfigurationSession();
				
				parentSession.addSessionStateListener(
						sessionStateListener);
			}
			else {
				parentSession.removeSessionStateListener(
						sessionStateListener);
				
				parentSession = null;
			}
		}
	};
	
	/**
	 * Construct the session.
	 * 
	 * @param context
	 */
	public ContextConfigurationSession(ArooaContext context) {
		
		this.session = context.getSession();
		
		context.getRuntime().addRuntimeListener(
				new RuntimeListenerAdapter() {
			@Override
			public void beforeDestroy(RuntimeEvent event)
					throws ArooaException {
				
				// Will this every be true becaue destroy will have removed
				// the owner first?
				if (parentSession != null) {
					parentSession.removeSessionStateListener(
							sessionStateListener);
				}
				
				parentOwner.removeOwnerStateListener(
						ownerStateListener);
			}
		});
		
		ArooaContext root = null;
		
		while (true) {
			if (context.getRuntime() != null) {
				root = context;
			}
			
			ArooaContext parent = context.getParent();
			
			if (parent == null) {
				break;
			}
			else {
				context = parent;
			}
		}
		
		final ArooaContext finalRoot = root;
		
		RuntimeConfiguration runtime = root.getRuntime();
		
		runtime.addRuntimeListener(new RuntimeListenerAdapter() {
			@Override
			public void afterInit(RuntimeEvent event)
					throws ArooaConfigurationException {
				
				ComponentPool componentPool =
						finalRoot.getSession().getComponentPool();
						
				ComponentTrinity trinity = componentPool.trinityForContext(
						finalRoot);
				
				Object proxy = trinity.getTheProxy();
				
				ConfigurationOwner owner = (ConfigurationOwner) proxy;
			
				owner.addOwnerStateListener(ownerStateListener);
			}
		});
		
		this.propertySupport = new ConfigurationSessionSupport(this);
	}
		
	@Override
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
		parentSession.save();
	}
	
	public ArooaDescriptor getArooaDescriptor() {
		return session.getArooaDescriptor();
	}
}
