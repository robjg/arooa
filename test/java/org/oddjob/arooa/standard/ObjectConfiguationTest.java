package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
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
import org.oddjob.arooa.reflect.MockArooaClass;
import org.oddjob.arooa.reflect.MockBeanOverview;
import org.oddjob.arooa.reflect.MockPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.registry.MockBeanRegistry;
import org.oddjob.arooa.runtime.Evaluator;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.PropertyFirstEvaluator;
import org.oddjob.arooa.runtime.PropertyManager;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class ObjectConfiguationTest extends TestCase {

	
	private class MockObject {
		// public void setFruit();
	}

	public static class Fruit {
		
	}


	private class OurAttributeTestSession extends MockArooaSession {
		Object propertyValue;

		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new MockArooaDescriptor() {
				@Override
				public ArooaBeanDescriptor getBeanDescriptor(
						ArooaClass forClass, PropertyAccessor accessor) {
					return new MockArooaBeanDescriptor() {
						@Override
						public ConfiguredHow getConfiguredHow(String property) {
							assertEquals("fruit", property);
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
		public ArooaTools getTools() {
			return new MockArooaTools() {
				@Override
				public PropertyAccessor getPropertyAccessor() {
					return new MockPropertyAccessor() {
						@Override
						public void setSimpleProperty(Object bean, String name, Object value) throws ArooaException {
							assertEquals("MockObject bean", MockObject.class, bean.getClass());
							assertEquals("Property name", "fruit", name);
							propertyValue = value;
						}
						
						@Override
						public ArooaClass getClassName(Object bean) {
							Class<?> cl = (Class<?>) bean.getClass();
							return new SimpleArooaClass(cl);
						}
						
						@Override
						public BeanOverview getBeanOverview(
								Class<?> forClass) throws ArooaException {
							return new MockBeanOverview() {
								@Override
								public String[] getProperties() {
									return new String[0];
								}
								@Override
								public Class<?> getPropertyType(String property) {
									assertEquals("fruit", property);
									return String.class;
								}
								
								@Override
								public boolean hasWriteableProperty(
										String property) {
									if ("fruit".equals(property)) {
										return true;
									}
									else {
										throw new ArooaPropertyException(property);
									}
								}
							};
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
				public ArooaConverter getArooaConverter() {
					return new DefaultConverter();
				}
				
				@Override
				public Evaluator getEvaluator() {
					return new PropertyFirstEvaluator();
				}
			};
		}
		
		@Override
		public BeanRegistry getBeanRegistry() {
			return new MockBeanRegistry() {
				@SuppressWarnings("unchecked")
				@Override
				public <T> T lookup(String path, Class<T> required) {
					assertEquals("Apple", path);
					assertEquals(String.class, required);
					return (T) "Orange";
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
	}

	private class AContext extends MockArooaContext {
		OurAttributeTestSession session = new OurAttributeTestSession();

		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public ArooaClass getClassIdentifier() {
					return new SimpleArooaClass(String.class);
				}
			};
		}
		
	}
	

	private class WrappingRuntime extends MockInstanceRuntime {
		Object value;

		public WrappingRuntime(InstanceConfiguration instance, ArooaContext parentContext) {
			super(instance, parentContext);
		}
		
		@Override
		ParentPropertySetter getParentPropertySetter() {
			return new ParentPropertySetter() {
				public void parentSetProperty(Object value) {
					WrappingRuntime.this.value = value;
				}
			};
		}
	}
	
	public void testNonConstAttributes() {
		
		AContext context = new AContext();

		MutableAttributes attrs = new MutableAttributes();
		attrs.set("fruit", "${Apple}");

		ObjectConfiguration test = new ObjectConfiguration(
				new SimpleArooaClass(MockObject.class), 
				new MockObject(), attrs);
		
		WrappingRuntime results = new WrappingRuntime(test, context);
		
		assertNull("No attribute", context.session.propertyValue);
		
		test.init(results, context);
		
		assertNull("No attribute because not constant.", 
				context.session.propertyValue);
		
		test.listenerConfigure(results, context);
		

		assertEquals("Runtime attribute set.", "Orange", 
				context.session.propertyValue);
		
		assertNotNull(results.value);
	}
	
	public void testConstAttributes() {
		
		AContext context = new AContext();

		MutableAttributes attrs = new MutableAttributes();
		attrs.set("fruit", "Apple");

		ObjectConfiguration test = new ObjectConfiguration(
				new SimpleArooaClass(MockObject.class), 
				new MockObject(), attrs);
		
		WrappingRuntime results = new WrappingRuntime(test, context);
		
		test.init(results, context);
		
		assertEquals("Constant attribute set.", "Apple", 
				context.session.propertyValue);
		
		test.listenerConfigure(results, context);
		
		assertEquals("Still Constant attribute set.", "Apple", 
				context.session.propertyValue);
		
	}
	

	private class TestTextContext extends MockArooaContext {
		String textProperty;
		
		OurAttributeTestSession session = new OurAttributeTestSession() {
			@Override
			public ArooaDescriptor getArooaDescriptor() {
				return new MockArooaDescriptor() {
					@Override
					public ArooaBeanDescriptor getBeanDescriptor(
							ArooaClass forClass, PropertyAccessor accessor) {					
						if (forClass instanceof OurArooaClass) {
							return new MockArooaBeanDescriptor() {
								@Override
								public String getTextProperty() {
									return textProperty;
								}
							};
						}
						return null;
					}
				};
			}
		};
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public ArooaClass getClassIdentifier() {
					return new SimpleArooaClass(String.class);
				}
			};
		}
		
	}
	
	public void testAddNonConstText() {

		TestTextContext context = new TestTextContext();
		
		ObjectConfiguration test = new ObjectConfiguration(
				new OurArooaClass(), new MockObject(), new MutableAttributes());

		WrappingRuntime result = new WrappingRuntime(test, context);
		
		test.addText("${Apple}");
		try {
			test.listenerConfigure(result, context);
			fail("No text property set.");
		} catch (ArooaException e) {
			// expected
		}
		
		context.textProperty = "fruit";
		
		assertNull("No attribute", context.session.propertyValue);
		
		test.init(result, context);
		
		assertNull("No attribute because not constant.", context.session.propertyValue);

		test.listenerConfigure(result, context);
		
		assertEquals("Runtime attribute set.", "Orange", context.session.propertyValue);		
	}	
	
	private class OurArooaClass extends MockArooaClass {
		
	}
	
	public void testAddConstText() {

		TestTextContext context = new TestTextContext();
		
		ObjectConfiguration test = new ObjectConfiguration(
				new OurArooaClass(), new MockObject(), new MutableAttributes());

		WrappingRuntime result = new WrappingRuntime(test, context);
		
		context.textProperty = "fruit";
		
		test.addText("Apple");
		
		test.init(result, context);
		
		assertEquals("Constant attribute set.", "Apple", context.session.propertyValue);
				
		test.listenerConfigure(result, context);
		
		assertEquals("Still Constant attribute set.", "Apple", context.session.propertyValue);
		
	}
	
}
