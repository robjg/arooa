package org.oddjob.arooa.standard;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class RuntimeFactoryContainerTest extends Assert {

	public static class Snack {
		public void setFruit(String fruit) {}
	}
	
	private class ParentContext extends MockArooaContext {
		@Override
		public ArooaSession getSession() {
			
			return new MockArooaSession() {
				@Override
				public ArooaTools getTools() {
					return new StandardTools();
				}
			};
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				
				@Override
				public ArooaClass getClassIdentifier() {
					return new SimpleArooaClass(Snack.class);
				}
			};
		}
	}
	
	
   @Test
	public void testMappedRuntime() {
    	
		ParentContext parentContext = new ParentContext();

		ElementAction<InstanceConfiguration> elementAction = 
			new ElementAction<InstanceConfiguration>() {
			public InstanceConfiguration onElement(ArooaElement element,
					ArooaContext context) {
				return new MockInstanceConfiguration();
			}
		};
		
		ContainerRuntime runtime = new ContainerRuntimeFactory.PropertyTypeSelector(
				).onMappedElement(
					new ArooaElement("fruit"), 
					parentContext,
					elementAction);
		
		assertNotNull(runtime);
		assertTrue(runtime instanceof MappedPropertyRuntime);
	}

	
	/**
	 * As above but for indexes.
	 * @throws ArooaPropertyException 
	 */
   @Test
	public void testIndexedRuntime() {
    	
		ParentContext parentContext = new ParentContext();
		
		ElementAction<InstanceConfiguration> elementAction = 
			new ElementAction<InstanceConfiguration>() {
			public InstanceConfiguration onElement(ArooaElement element,
					ArooaContext context) {
				return new MockInstanceConfiguration();
			}
		};
		
		ContainerRuntime runtime = new ContainerRuntimeFactory.PropertyTypeSelector(
				).onIndexedElement(
					new ArooaElement("fruit"), 
					parentContext, 
					elementAction);
		
		assertNotNull(runtime);
		assertTrue(runtime instanceof IndexedPropertyRuntime);
		
	}

	
}
