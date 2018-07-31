/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.standard;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

/**
 *  
 */
public class RootHandlerFactoryTest extends Assert {

	private class MockRoot { }

	
    private class OurRuntime extends MockInstanceRuntime {
    	final String name;
    	boolean parsetimeInitialised;
    	boolean endIntercept;
    	
    	OurRuntime(String name, ArooaContext parentContext) {
    		super(new MockInstanceConfiguration(
    				new SimpleArooaClass(MockRoot.class),
    				new MockRoot(),
    				new MutableAttributes()) {
    			@Override
    			Object getWrappedObject() {
    				return new Object();
    			}
    			@Override
    			Object getObjectToSet() {
    				return new Object();
    			}
    		}, parentContext);
    		this.name = name;
    	}
    	

		@Override
		public void init() throws ArooaException {
			if (parsetimeInitialised) {
				throw new IllegalStateException("Alrady Initialised.");
			}
			parsetimeInitialised = true;
		}
		
		@Override
		public ArooaClass getClassIdentifier() {
			return new SimpleArooaClass(Object.class);
		}
    }
    
    private class ParentContext extends MockArooaContext {
    	
    	@Override
    	public ArooaType getArooaType() {
    		return ArooaType.VALUE;
    	}
    	
    	@Override
    	public PrefixMappings getPrefixMappings() {
    		return null;
    	}
    	
    	@Override
    	public ArooaSession getSession() {
    		return new StandardArooaSession();
    	}
    	
    	@Override
    	public RuntimeConfiguration getRuntime() {
    		return null;
    	}
    }
    
   @Test
	public void testOnElement() {
				
		RootHandler test = new RootHandler(new ElementAction<InstanceRuntime>() {
			public InstanceRuntime onElement(ArooaElement element, ArooaContext context) {
				return new OurRuntime(element.getTag(), context);
			}
		});
		
		ArooaContext context = test.onStartElement(
				new ArooaElement("snacks"), 
				new ParentContext());
		
		OurRuntime parsetime = (OurRuntime) context.getRuntime();
		
		assertEquals("snacks", parsetime.name);
		
		// check parsetime lifecycle
		assertFalse(parsetime.parsetimeInitialised);
		assertFalse(parsetime.endIntercept);

		parsetime.init();
		
		assertTrue(parsetime.parsetimeInitialised);		
	}

}
