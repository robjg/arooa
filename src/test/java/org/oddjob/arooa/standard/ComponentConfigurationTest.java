package org.oddjob.arooa.standard;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ComponentTrinity;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.MockBeanOverview;
import org.oddjob.arooa.reflect.MockPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.MockBeanRegistry;
import org.oddjob.arooa.registry.MockComponentPool;
import org.oddjob.arooa.runtime.Evaluator;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.PropertyFirstEvaluator;
import org.oddjob.arooa.runtime.PropertyManager;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class ComponentConfigurationTest extends Assert {

	private class ATools extends MockArooaTools {
		String property;
		String value;
		
		@Override
		public ArooaConverter getArooaConverter() {
			return new DefaultConverter();
		}
		
		@Override
		public PropertyAccessor getPropertyAccessor() {
			return new MockPropertyAccessor()  {
				@Override
				public void setSimpleProperty(Object bean, String name, Object value)
						throws ArooaException {
					assertEquals(MockObject.class, bean.getClass());
					
					ATools.this.property = name;
					ATools.this.value = (String) value;
				}
				
				@Override
				public BeanOverview getBeanOverview(Class<?> classId) 
				throws ArooaException {
					return new MockBeanOverview() {
						@Override
						public boolean hasWriteableProperty(String property) {
							if ("colour".equals(property)) {
								return true;
							}
							else if ("id".equals(property)) {
								return false;
							}
							else {
								throw new ArooaPropertyException(property);
							}
						}
						
						@Override
						public String[] getProperties() {
							return new String[0];
						}
						@Override
						public Class<?> getPropertyType(String property) {
							assertEquals("colour", property);
							return String.class;
						}
					};
				}
				
				@Override
				public ArooaClass getClassName(Object bean) {
					Class<?> cl = (Class<?>) bean.getClass();
					return new SimpleArooaClass(cl);
				}
				
				@Override
				public PropertyAccessor accessorWithConversions(
						ArooaConverter converter) {
					return this;
				}
			};
		}
		
		@Override
		public ExpressionParser getExpressionParser() {
			return new StandardPropertyHelper();
		}
		
		@Override
		public Evaluator getEvaluator() {
			return new PropertyFirstEvaluator();
		}
		
	}
	
	private class ASession extends MockArooaSession {
		ATools tools = new ATools();
		
		ComponentTrinity trinity;
		
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new MockArooaDescriptor() {
				@Override
				public ArooaBeanDescriptor getBeanDescriptor(
						ArooaClass forClass, PropertyAccessor accessor) {
					return new MockArooaBeanDescriptor() {
						@Override
						public ConfiguredHow getConfiguredHow(String property) {
							assertEquals("colour", property);
							return ConfiguredHow.ATTRIBUTE;
						}
						@Override
						public String getComponentProperty() {
							return null;
						}
					};
				}
			};
		}
		
		@Override
		public ComponentPool getComponentPool() {
			return new MockComponentPool() {
				@Override
				public void registerComponent(ComponentTrinity trinity, String id) {
					assertEquals("anid", id);
					if (ASession.this.trinity != null) {
						throw new RuntimeException("Registering twice??");
					}
					ASession.this.trinity = trinity;
				}
				
			};
		}
		
		@Override
		public BeanRegistry getBeanRegistry() {
			return new MockBeanRegistry() {
				@SuppressWarnings("unchecked")
				@Override
				public <T> T lookup(String path, Class<T> required) {
					assertEquals("Value to replace.", "To be replaced", path);
					assertEquals(String.class, required);
					return (T) "red";
				}
			};
		}

		@Override
		public PropertyManager getPropertyManager() {
			return new MockPropertyManager() {
				@Override
				public String lookup(String propertyName) {
					return null;
				}
			};
		}
		
		@Override
		public ArooaTools getTools() {
			return tools;
		}
	}
	
	private class AContext extends MockArooaContext {
		ASession session = new ASession();

		@Override
		public ArooaSession getSession() {
			return session;
		}

		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public ArooaClass getClassIdentifier() {
					return new SimpleArooaClass(
							String.class);
				}
			};
		}
	}
	
	private class ProxyObject {
		
	}
	
	private class MockObject {
		
	}
	
	private class AnInstanceRuntime extends MockInstanceRuntime {
		
		Object value;
		
		public AnInstanceRuntime(InstanceConfiguration instance, ArooaContext parentContext) {
			super(instance, parentContext);
		}

		@Override
		ParentPropertySetter getParentPropertySetter() {
			return new ParentPropertySetter() {
				public void parentSetProperty(Object value) {
					
					// Test registered.
					ASession ourSess = (ASession) getParentContext().getSession(); 
					assertNotNull(ourSess.trinity);
					
					AnInstanceRuntime.this.value = value;
				}
			};
		}
		
	}

   @Test
	public void testInit() {
		
		AContext context = new AContext();
		
		MockObject object = new MockObject();
		
		ProxyObject proxy = new ProxyObject();
		
		MutableAttributes attrs = new MutableAttributes();
		attrs.set("id", "anid");
		attrs.set("colour", "${To be replaced}");
		
		ComponentConfiguration test = new ComponentConfiguration(
				new SimpleArooaClass(object.getClass()),
				object, 
				proxy, 
				attrs);
		
		AnInstanceRuntime instanceRuntime = new AnInstanceRuntime(
				test, context);
		
		
		assertNull("Property not set", instanceRuntime.value);
		
		test.init(instanceRuntime, context);
		
		assertNotNull("Property not set", instanceRuntime.value);

		assertEquals("Registered", object, context.session.trinity.getTheComponent());
		assertEquals("Registered", proxy, context.session.trinity.getTheProxy());
		
		assertEquals("Parent property set", proxy, instanceRuntime.value);
		
		assertNull("Property not set", context.session.tools.value);
	}
	
	private class AnInstanceRuntime2 extends MockInstanceRuntime {
		
		public AnInstanceRuntime2(InstanceConfiguration instance, ArooaContext parentContext) {
			super(instance, parentContext);
		}
		
		@Override
		ParentPropertySetter getParentPropertySetter() {
			return null;
		}
	}
	
   @Test
	public void testConfigure() {
		
		AContext context = new AContext();
		
		MutableAttributes attrs = new MutableAttributes();
		attrs.set("colour", "${To be replaced}");
		
		ComponentConfiguration test = new ComponentConfiguration(
				new SimpleArooaClass(MockObject.class),
				new MockObject(), 
				new ProxyObject(), 
				attrs);
		
		assertNull("Property not set", context.session.tools.value);
		
		test.listenerConfigure(
				new AnInstanceRuntime2(test, context), context);
		
		assertNull("Property not set", context.session.tools.value);

		test.configure(new AnInstanceRuntime(test, context), context);

		assertEquals("property", "colour", context.session.tools.property);
		assertEquals("value", "red", context.session.tools.value);
	}
}
