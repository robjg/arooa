package org.oddjob.arooa.registry;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.life.ComponentPersistException;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 * An simple implementation of a {@link ComponentPool}.
 * <p>
 * This class is thread safe for the addition an removal of components. It is
 * not thread safe for configuration an saving. This is left to the
 * calling code. Oddjob for instance achieves thread safety for for these
 * operations by locking on State.
 * <p>
 *
 * @author rob
 *
 */
public class SimpleComponentPool implements ComponentPool {
	
	private static final Logger logger = Logger.getLogger(SimpleComponentPool.class);

	private final AllWayIndex index = new AllWayIndex();
	
	@Override
	public void configure(Object component) 
	throws ArooaConfigurationException {
		
		if (component == null) {
			throw new NullPointerException("No component.");
		}
		
		ArooaContext context = contextFor(component);
		if (context == null) {
			// quietly return. Component might not have been registered.
			return;
		}
		
		RuntimeConfiguration runtime = context.getRuntime();

		runtime.configure();
	}

	@Override
	public void save(Object component) throws ComponentPersistException {
		
		if (component == null) {
			throw new NullPointerException("No component.");
		}
		
		ComponentTrinity trinity = index.trinityFor(component);
		if (trinity == null) {
			// quietly return. Component might not have been registered.
			return;
		}
		
		ArooaSession session = trinity.getTheContext().getSession();
		
		ComponentPersister persister = session.getComponentPersister();
		
		if (persister == null) {
			return;
		}

		String id = index.idFor(trinity);
		
		if (id == null) {
			return;
		}
		
		persister.persist(id, trinity.getTheProxy(), session);
	}

	@Override
	public void remove(Object either) throws ComponentPersistException {
		if (either == null) {
			throw new NullPointerException("No component.");
		}

		ComponentTrinity trinity = null;
		synchronized (index) {
			trinity = index.trinityFor(either);
		}
		

		if (trinity == null) {
			// Used to throw an Exception here but a component might
			// not have fully initialised before being destroyed. 
			// I.e. when a cut and paste fails.
			return;
		}
			
		ArooaSession session = trinity.getTheContext().getSession();
		
		ComponentPersister persister = session.getComponentPersister();
		
		if (persister != null) {
			String id = index.idFor(trinity);
			
			if (id != null) {
				persister.remove(id, session);
			}
		}

		index.remove(trinity);
		
		trinity.getTheContext().getSession().getBeanRegistry().remove(
				trinity.getTheProxy());
	}
	
	@Override
	public ArooaContext contextFor(Object either) {
		ComponentTrinity trinity = index.trinityFor(either);
		
		if (trinity == null) {
			return null;
		}
		
		return trinity.getTheContext();
	}

	@Override
	public ComponentTrinity trinityForContext(ArooaContext context) {
		return index.trinityForContext(context);
	}
	
	@Override
	public ComponentTrinity trinityFor(Object either) {
		return index.trinityFor(either);
	}
	
	@Override
	public ComponentTrinity trinityForId(String id) {
		synchronized (index) {
    		return index.trinityForId(id);
		}    	
	}
	
	@Override
	public String getIdFor(Object either) {
		synchronized(index) {
    		return index.idFor(
    				index.trinityFor(either));
    	}
 	}

	@Override
	public Iterable<ComponentTrinity> allTrinities() {
		synchronized (index) {
			return index.trinities();
		}
	}
	
	@Override
	public void registerComponent(ComponentTrinity componentTrinity, String id) {
		
		if (id != null) {
			validateId(id);
		}
		
		String newId = index.add(componentTrinity, id);
		
		if (newId == null) {
			return;
		}

		if (!newId.equals(id)) {
			logger.info("Duplicate ID [" + id + "] for ["
					+ componentTrinity.getTheComponent() + 
					"], registering with[" + newId + "].");
		}
		
		BeanRegistry registry = 
			componentTrinity.getTheContext().getSession().getBeanRegistry();
		
		Object existing = registry.lookup(newId);
		if (existing != null) {
			logger.info("Id [" + newId + "] is already registered to bean ["
					+ existing + "].");
		}
			
		registry.register(
			newId, componentTrinity.getTheProxy());
		
	}

	private void validateId(String id) {
		if (id.length() == 0) {
			throw new IllegalArgumentException("Blank id is invalid.");
		}
	}
	
	static class AllWayIndex {
		
		/** ComponentTrinities mapped by proxy object. */
	    private final Map<Object, ComponentTrinity> proxiesTo = 
	    	new HashMap<Object, ComponentTrinity>();
	    
	    /** ComponentTrinities mapped by the component. 
	     * Note this map maintains order added for iteration. */
	    private final Map<Object, ComponentTrinity> componentsTo = 
	    	new LinkedHashMap<Object, ComponentTrinity>();
	        
		/** ComponentTrinities mapped by contexts. */
	    private final Map<ArooaContext, ComponentTrinity> contextsTo = 
	    	new HashMap<ArooaContext, ComponentTrinity>();
	    
	    /** Id's mapped by ComponentTrinity. */
	    private final Map<ComponentTrinity, String> ids= 
	    	new HashMap<ComponentTrinity, String>();
	        
	    /** ComponentTrinities mapped by id. */
	    private final Map<String, ComponentTrinity> trinities = 
	    	new HashMap<String, ComponentTrinity>();
	        
	    synchronized String add(ComponentTrinity trinity, String id) {
	    	id = uniqueId(id);
	    	
			componentsTo.put(trinity.getTheComponent(), trinity);
			proxiesTo.put(trinity.getTheProxy(), trinity);
			contextsTo.put(trinity.getTheContext(), trinity);
			
			if (id != null) {
				ids.put(trinity, id);
				trinities.put(id, trinity);
			}
			return id;
	    }

	    synchronized boolean contains(String id) {
	    	return trinities.containsKey(id);
	    }
	    
	    synchronized Iterable<ComponentTrinity> trinities() {
	    	return componentsTo.values();
	    }
	    
	    
	    synchronized ComponentTrinity trinityFor(Object either) {
			if (proxiesTo.containsKey(either)) {
				return proxiesTo.get(either);
			}
			if (componentsTo.containsKey(either)) {
				return componentsTo.get(either); 
			}
			return null;
		}
	    
	    synchronized ComponentTrinity trinityForContext(
	    		ArooaContext arooaContext) {
			return contextsTo.get(arooaContext); 
		}
		
	    synchronized void remove(ComponentTrinity trinity) {

	    	// When is this not real?
			ComponentTrinity real = 
					componentsTo.remove(trinity.getTheComponent());
			
			proxiesTo.remove(real.getTheProxy());
			contextsTo.remove(real.getTheContext());
			
			String id = ids.remove(real);
			if (id != null) {
				trinities.remove(id);
			}
		}
		
	    synchronized String idFor(ComponentTrinity trinity) {
			if (trinity == null) {
				return null;
			}
			
			
			return ids.get(trinity);
		}
		
	    synchronized ComponentTrinity trinityForId(String id) {
			return trinities.get(id);
		}
		
	    synchronized String uniqueId(String id) {

			if (id == null) {
				return null;
			}
			
			if (!contains(id)) {
				return id;
			}
			
			return uniqueId(id, 2);
	    }			
		    
	    synchronized String uniqueId(String id, int i) {
			
			String maybeId = id + i;
			if (!contains(maybeId)) {
				return maybeId;
			}
			
			return uniqueId(id, ++i);
		}
			
	}
}
