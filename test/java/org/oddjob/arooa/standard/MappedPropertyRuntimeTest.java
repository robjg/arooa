package org.oddjob.arooa.standard;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaAttributes;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.BeanOverview;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.MockBeanOverview;
import org.oddjob.arooa.reflect.MockPropertyAccessor;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.Evaluator;
import org.oddjob.arooa.runtime.ExpressionParser;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.PropertyFirstEvaluator;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

public class MappedPropertyRuntimeTest extends Assert {

	
	public interface Fruit {}
	
	/**
	 * The thing that will be set.
	 */
	public static class Apple implements Fruit {
	}
	
	/**
	 * 
	 */
	private class OurArooaSession extends MockArooaSession {
		boolean checkedNameAttribute;

		@Override
		public ArooaTools getTools() {
			return new MockArooaTools() {
				@Override
				public PropertyAccessor getPropertyAccessor() {
					return new MockPropertyAccessor() {
						@Override
						public BeanOverview getBeanOverview(Class<?> forClass) 
						throws ArooaException {
							return new MockBeanOverview() {
								@Override
								public boolean hasWriteableProperty(String property) {
									if ("colour".equals(property)) {
										return true;
									}
									else if ("key".equals(property)) {
										checkedNameAttribute = true;
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
						public void setSimpleProperty(Object bean, String name,
								Object value) throws ArooaException {
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
				public ArooaConverter getArooaConverter() {
					return new DefaultConverter();
				}
				
				@Override
				public ExpressionParser getExpressionParser() {
					return new StandardPropertyHelper();
				}
				
				@Override
				public Evaluator getEvaluator() {
					return new PropertyFirstEvaluator();
				}
			};
		}
						
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new MockArooaDescriptor() {
				@Override
				public ArooaBeanDescriptor getBeanDescriptor(
						ArooaClass forClass, PropertyAccessor accessor) {
					return new MockArooaBeanDescriptor() {
						@Override
						public ParsingInterceptor getParsingInterceptor() {
							return null;
						}
						@Override
						public ConfiguredHow getConfiguredHow(String property) {
							assertEquals("colour", property);
							return ConfiguredHow.ATTRIBUTE;
						}
						@Override
						public String getComponentProperty() {
							return null;
						}
						@Override
						public ArooaAnnotations getAnnotations() {
							return new NoAnnotations();
						}
					};
				}
			};
		}
	}
	
	private class ParentContext extends MockArooaContext {
		ArooaSession session = new OurArooaSession();

		RuntimeListener listener;
		
		Apple fruit;
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {

				@Override
				public void addRuntimeListener(
						RuntimeListener listener) {
					ParentContext.this.listener = listener;
				}
				
				public void setMappedProperty(String name, String key, Object value) 
				throws ArooaException {
					assertEquals("fruit", name);
					assertEquals("cox", key);
					fruit = (Apple) value;
				}
			};
		}
		
		@Override
		public ArooaSession getSession() {
			return session;
		}		
	}
	
	private class TestContext extends MockArooaContext {
		ArooaSession session = new OurArooaSession();
		
		MappedPropertyRuntime test;
		
		@Override
		public ArooaType getArooaType() {
			return ArooaType.VALUE;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return test;
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				
				@Override
				public void addNodeListener(ConfigurationNodeListener listener) {
				}
			};
		}		
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public ArooaHandler getArooaHandler() {
			return test.getHandler();
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return null;
		}
	}
	
	private class DummyNestedAction implements ElementAction<InstanceConfiguration> {
		
		public InstanceConfiguration onElement(ArooaElement element, ArooaContext context) {
			
			ArooaAttributes attributes = element.getAttributes();
			assertEquals(2, attributes.getAttributNames().length);
			assertEquals("Colour is", "red", attributes.get("colour"));
			assertEquals("Key is", "cox", attributes.get("key"));
			return new ObjectConfiguration(
					new SimpleArooaClass(Apple.class), new Apple(), attributes);
		}
	}

	/**
	 * Test creating and configuring a mapped object.
	 */
   @Test
	public void testConfigure() {
				
		ParentContext parentContext = new ParentContext();		
				
		DummyNestedAction nestedAction = new DummyNestedAction();
		
		
		MappedPropertyRuntime test = new MappedPropertyRuntime(
				nestedAction,
				new MockPropertyDefinition() {
					@Override
					public String getPropertyName() {
						return "fruit";
					}
					@Override
					public ArooaClass getPropertyType() {
						return new SimpleArooaClass(Fruit.class);
					}
				},
				parentContext);
		
		TestContext testContext = new TestContext();
		testContext.test = test;
		
		test.setContext(testContext);
		
		MutableAttributes attributes = new MutableAttributes();
		attributes.set("key", "cox");
		attributes.set("colour", "red");
		
		// Take SUT through same cycle that the parser would
		ArooaContext child = 
			testContext.getArooaHandler().onStartElement(
					new ArooaElement("apple", attributes),
					testContext);
		
		child.getRuntime().init();
		
		assertTrue("Name attribute checked.", 
				((OurArooaSession) testContext.getSession()).checkedNameAttribute);
		
		
		test.init(); 
		
		assertNotNull(parentContext.listener);
				
		// init has no affect.
		assertNull(parentContext.fruit);

		// but configure does.
		parentContext.listener.beforeConfigure(
				new RuntimeEvent(
						new MockRuntimeConfiguration()));

		assertNotNull(parentContext.fruit);
	}

}
