package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.parsing.ConfigOwnerEvent.Change;
import org.oddjob.arooa.registry.ComponentPool;

/**
 * A {@link ConfigurationSession}.
 * 
 * @author rob
 *
 */
public class ContextConfigurationSession implements ConfigurationSession {

	private final ArooaSession session;
	
	private final ConfigurationSessionSupport propertySupport;
	
	private boolean modified;
		
	private ConfigurationSession parentSession;
	
	public ContextConfigurationSession(ArooaContext context) {
		
		this.session = context.getSession();
		
		while (true) {
			context = context.getParent();
			if (context == null) {
				break;
			}
			
			ComponentPool componentPool =
				context.getSession().getComponentPool();
				
			if (componentPool == null) {
				continue;
			}
			
			ComponentTrinity trinity = componentPool.trinityForContext(
					context);
			
			if (trinity == null) {
				continue;
			}
			
			Object proxy = trinity.getTheProxy();
			
			
			if (proxy instanceof ConfigurationOwner) {
				
				final ConfigurationOwner owner = (ConfigurationOwner) proxy;
						
				owner.addOwnerStateListener(new OwnerStateListener() {
					
					@Override
					public void sessionChanged(ConfigOwnerEvent event) {
						if (Change.SESSION_CREATED == event.getChange()) {
							
							parentSession = owner.provideConfigurationSession();
							
							parentSession.addSessionStateListener(
									new SessionStateListener() {
								
								@Override
								public void sessionSaved(ConfigSessionEvent event) {
									setModified(false);
								}
								
								@Override
								public void sessionModifed(ConfigSessionEvent event) {
									setModified(true);
								}
							});
						}
						else {
							owner.removeOwnerStateListener(this);
						}
					}
				});
			}
		}
		
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
		parentSession.save();
	}
	
	public ArooaDescriptor getArooaDescriptor() {
		return session.getArooaDescriptor();
	}
}
