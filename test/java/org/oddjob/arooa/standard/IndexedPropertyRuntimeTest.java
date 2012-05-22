package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.ParsingInterceptor;
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
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

public class IndexedPropertyRuntimeTest extends TestCase {

	
	/**
	 * The thing that will be set.
	 * 
	 * Implements ArooaValue just to check it's only set on configure.
	 */
	public static class Apple implements ArooaValue {
		
		public void setColour(String colour) {}
	}
	
	/**
	 * 
	 */
	private class OurArooaSession extends MockArooaSession {

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
							
						};
						@Override
						public String getComponentProperty() {
							return null;
						}
						@Override
						public boolean isAuto(String property) {
							return false;
						}
						@Override
						public ArooaAnnotations getAnnotations() {
							return new NoAnnotations();
						}
					};
				}
			};
		}
		
		@Override
		public ArooaTools getTools() {
			return new StandardTools();
		}
	}

	private class ParentContext extends MockArooaContext {
		final OurArooaSession session = new OurArooaSession();
		
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

				public void setIndexedProperty(String name, int index, Object value) throws ArooaException {
					assertEquals("fruit", name);
					assertEquals("index", 2, index);
					fruit = (Apple) value;
				}
			};
		}

		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return null;
		}
	}
	
	private class TestContext extends MockArooaContext {
		final OurArooaSession session = new OurArooaSession();
		
		IndexedPropertyRuntime test;
		
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
				public int indexOf(ConfigurationNode child) {
					return 2;
				}
			};
		}		
		
		@Override
		public ArooaHandler getArooaHandler() {
			return test.getHandler();
		}
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return null;
		}
	}
	
	private class DummyNestedAction implements ElementAction<InstanceConfiguration> {
		public InstanceConfiguration onElement(ArooaElement element, ArooaContext context) {
			// check the attribute name is removed but the colour is left.
			ArooaAttributes attributes = element.getAttributes();
			assertEquals("Only one attribute", 1, attributes.getAttributNames().length);
			assertEquals("Colour is", "red", attributes.get("colour"));
			
			return new ObjectConfiguration(
					new SimpleArooaClass(Apple.class), new Apple(), attributes);
		}
	}
	
	public void testConfigure() {
		
		ParentContext parentContext = new ParentContext();		
		
		DummyNestedAction nestedAction = new DummyNestedAction();
	
		IndexedPropertyRuntime test = new IndexedPropertyRuntime(nestedAction,
				new MockPropertyDefinition() {
					@Override
					public String getPropertyName() {
						return "fruit";
					}
					@Override
					public ArooaClass getPropertyType() {
						return new SimpleArooaClass(Apple.class);
					}
				},
				parentContext);
		
		TestContext testContext = new TestContext();
		testContext.test = test;

		test.setContext(testContext);
		
		MutableAttributes attributes = new MutableAttributes();
//		attributes.put("name", "apple");
		attributes.set("colour", "red");
				
		ArooaElement element = new ArooaElement("apple", attributes);
		
		ArooaContext child = 
			testContext.getArooaHandler().onStartElement(element, 
				testContext);
				
		child.getRuntime().init();

		test.init();
		
		assertNotNull(parentContext.listener);
		
		assertNull(parentContext.fruit);

		parentContext.listener.beforeConfigure(
				new RuntimeEvent(
						new MockRuntimeConfiguration()));

		assertNotNull(parentContext.fruit);
	}
}
