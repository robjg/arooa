package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.LinkedDescriptor;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaInstantiationException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.MockArooaClass;
import org.oddjob.arooa.reflect.MockBeanOverview;
import org.oddjob.arooa.reflect.MockPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeListener;
import org.oddjob.arooa.standard.ComponentConfigurationCreatorTest.DummyComponent;

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
    	boolean propertySet;
    }
    
	private class ValueSession extends MockArooaSession {
		ArooaDescriptor arooaDescriptor;
		PropertyAccessor propertyAccessor;
		
		String id;
		DummyComponent component;
				
		@Override
		public ArooaTools getTools() {
			return new MockArooaTools() {
				
				@Override
				public PropertyAccessor getPropertyAccessor() {
					if (propertyAccessor == null) {
						throw new RuntimeException("Unexpected");
					}
					return propertyAccessor;
				}
				
				@Override
				public ArooaConverter getArooaConverter() {
					return new DefaultConverter();
				}
			};
		}
		
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			if (arooaDescriptor == null) {
				throw new RuntimeException("Unexpected");
			}
			return arooaDescriptor;
		}
	}
	
	private class OurArooaClass extends MockArooaClass {
		@Override
		public Object newInstance()
				throws ArooaInstantiationException {
			return new DummyValue();
		}		

		@Override
		public Class<?> forClass() {
			return DummyValue.class;
		}
		
		public BeanOverview getBeanOverview(PropertyAccessor accessor) {
			return new MockBeanOverview() {
				@Override
				public boolean hasWriteableProperty(String property) {
					if ("fruit".equals(property)) {
						return true;
					}
					throw new RuntimeException("Unexpected.");
				}
				@Override
				public String[] getProperties() {
					return new String[] { "fruit" };
				}
				
				@Override
				public boolean isIndexed(String property) {
					return false;
				}
				
				@Override
				public boolean isMapped(String property) {
					return false;
				}
				@Override
				public Class<?> getPropertyType(String property) {
					assertEquals("fruit", property);
					return String.class;
				}
			};
		}
	}
	
	private class ValueArooaDescriptor extends MockArooaDescriptor {
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIentifier, PropertyAccessor accessor) {
			return null;
		}
		@Override
		public ClassResolver getClassResolver() {
			return new ClassLoaderClassResolver(getClass().getClassLoader());
		}
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(null, 
					new MockElementMappings() {
				@Override
				public ArooaClass mappingFor(ArooaElement element,
						InstantiationContext parentContext) {
					return new OurArooaClass();
				}
			});
		}
	}
	
	private class ValuePropertyAccessor extends MockPropertyAccessor {
		
		@Override
		public ArooaClass getClassName(final Object bean) {
			return new SimpleArooaClass(bean.getClass());
		}
		
		@Override
		public void setSimpleProperty(Object bean, String name, Object value) throws ArooaException {
			assertTrue(bean instanceof DummyValue);
			assertEquals("fruit", name);
			assertEquals("Apples", value);
			((DummyValue) bean).propertySet = true;
		}
	};
	
	/**
	 * Test creating a value from an element name.
	 */
	public void testValueFromElementCreate() throws ArooaException {
        
		ArooaElement element = new ArooaElement("snack");
		MutableAttributes attributes = new MutableAttributes();
        attributes.set("fruit", "Apples");

		ValueSession session = new ValueSession();
		
		session.propertyAccessor = new ValuePropertyAccessor();
		session.arooaDescriptor = new LinkedDescriptor(
				new ValueArooaDescriptor(),
				new StandardArooaDescriptor());

			

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
		
		// test no component registered.
		assertEquals(null, session.id);
		assertNull(session.component);
		
		assertNull(parentContext.objectCreated);
		assertNotNull(parentContext.listener);		
	}
	
}
