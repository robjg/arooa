package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.deploy.ConfigurationDescriptorFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeListener;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ValueConfigurationCreatorTest extends TestCase {

	public static class PretendNestedFruit {
		boolean propertySet;
		
	}
	
	private class InterceptContext extends MockArooaContext {
		final RuntimeConfiguration runtime;
		final ArooaContext parent;
		
		InterceptContext(RuntimeConfiguration runtime,
				ArooaContext parent) {
			this.runtime = runtime;
			this.parent = parent;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
		
		@Override
		public ArooaSession getSession() {
			return parent.getSession();
		}
	}
	
	private class ParentContext extends MockArooaContext {
		Object objectCreated;
		
		ArooaSession session;
		
		RuntimeListener listener;
		
		public ParentContext(ArooaSession session) {
			this.session = session;
		}

		@Override
		public ArooaType getArooaType() {
			return ArooaType.VALUE;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void addRuntimeListener(
						RuntimeListener listener) {
					ParentContext.this.listener = listener;
				}
				@Override
				public ArooaClass getClassIdentifier() {
					return new SimpleArooaClass(PretendNestedFruit.class);
				}
				@Override
				public void setProperty(String name, Object value)
						throws ArooaException {
					assertNull(name);
					objectCreated = value;
				}
			};
		}
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
	}
	
    public static class DummyValue {
    	
    	String fruit;
    	
    	public void setFruit(String fruit) {
			this.fruit = fruit;
		}
    }
    			
	/**
	 * Test creating a value from an element name.
	 */
	public void testValueFromElementCreate() throws ArooaException {
    
		String descriptorXML =
				"<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
			    " <values>" +
			    "  <arooa:bean-def element='snack'" +
			    "      className='" + DummyValue.class.getName() + "'>" +
        		"  </arooa:bean-def>" +
        		" </values>" +
        		"</arooa:descriptor>";
		
		ArooaDescriptor descriptor = new ConfigurationDescriptorFactory(
				new XMLConfiguration("XML", descriptorXML)).createDescriptor(
								getClass().getClassLoader());
		
		StandardArooaSession session = new StandardArooaSession(descriptor);
		
		
		ArooaElement element = new ArooaElement("snack");
		MutableAttributes attributes = new MutableAttributes();
        attributes.set("fruit", "Apples");


		ParentContext parentContext = new ParentContext(session);
		
		ValueConfigurationCreator test = new ValueConfigurationCreator();
		
		// Create the runtime.
		InstanceConfiguration result = test.onElement(
						element, 
						parentContext);
		
		// test runtime created with the correct runtime class.
		assertNotNull(result);
		assertEquals(DummyValue.class, result.getWrappedObject().getClass());

		SimpleInstanceRuntime wrapper = new SimpleInstanceRuntime(
				result, parentContext);
	
		wrapper.setContext(new InterceptContext(
				wrapper, parentContext));

		wrapper.init();
		
		
		assertNull(parentContext.objectCreated);
		assertNotNull(parentContext.listener);		
	}
	
}
