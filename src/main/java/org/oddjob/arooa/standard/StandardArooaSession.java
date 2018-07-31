package org.oddjob.arooa.standard;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.deploy.LinkedDescriptor;
import org.oddjob.arooa.life.ComponentPersister;
import org.oddjob.arooa.life.ComponentProxyResolver;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.SimpleBeanRegistry;
import org.oddjob.arooa.registry.SimpleComponentPool;
import org.oddjob.arooa.runtime.PropertyManager;

public class StandardArooaSession implements ArooaSession {

    private final ComponentPool componentPool;
	
    private final BeanRegistry beanRegistry;
    
    private final ArooaDescriptor descriptor;
        
    private final ArooaTools tools;
    
    private final PropertyManager propertyManger;
    
    public StandardArooaSession() {
    	this(null);
    }
    
    /**
     * Constructor that takes an ArooaDescriptor. If the descriptor
     * is null then a {@link StandardArooaDescriptor} is created
     * and used. Otherwise the provided descriptor is used.
     * 
     * @param descriptor
     */
    public StandardArooaSession(ArooaDescriptor descriptor) {
    	this(descriptor, false);
    }
    /**
     * Constructor that takes an ArooaDescriptor. If the descriptor
     * is null then a {@link StandardArooaDescriptor} is created
     * and used. Otherwise the provided descriptor is used.
     * 
     * @param descriptor
     */
    public StandardArooaSession(ArooaDescriptor descriptor, 
    		boolean noDefaultDescriptor) {
    	
    	ArooaTools tools = new StandardTools();
    	
    	if (descriptor == null) {
    		this.descriptor = new StandardArooaDescriptor();
    	}
    	else {
    		if (noDefaultDescriptor) {
        		this.descriptor = descriptor;
    		}
    		else {
    			this.descriptor = 
    				new LinkedDescriptor(descriptor, 
    						new StandardArooaDescriptor());
    		}
    	}

    	this.tools = new ExtendedTools(tools, this.descriptor);
    	
    	this.componentPool = new SimpleComponentPool();
    	
    	this.beanRegistry = new SimpleBeanRegistry(
    			this.tools.getPropertyAccessor(),
    			this.tools.getArooaConverter());
    	
    	this.propertyManger = new StandardPropertyManager();
	}

    @Override
    public ComponentPool getComponentPool() {
    	return componentPool;
    }
    
    @Override
    public BeanRegistry getBeanRegistry() {
    	return beanRegistry;
    }
    
    @Override
    public PropertyManager getPropertyManager() {
    	return propertyManger;
    }
    
    @Override
	public ArooaDescriptor getArooaDescriptor() {
		return descriptor;
	}

    @Override
	public ArooaTools getTools() {
		return tools;
	}

    @Override
	public ComponentProxyResolver getComponentProxyResolver() {
		return null;
	}
	
    @Override
	public ComponentPersister getComponentPersister() {
		return null;
	}
}
