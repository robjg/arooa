package org.oddjob.arooa.standard;

import junit.framework.TestCase;

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
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.SimpleArooaClass;
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
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;

/**
 * Test the ContainerRuntimeFactory creates the simple, indexed and mapped
 * ContainerRuntimes and their 
 * @author rob
 *
 */
public class ContainerRuntimeFactoryTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		Class.forName(SimpleArooaClass.class.getName());
	}
	
	public static class OurElementThing {
		public String toString() {
			return "Element";
		}
	}
	
	private class CurrentArooaRuntime extends MockInstanceRuntime {
		String property;
		String type;
		Object value;
		String key;
		int index;

		CurrentArooaRuntime(InstanceConfiguration instance, ArooaContext parentContext) {
			super(instance, parentContext);
		}
		
		RuntimeListener listener;
				
		@Override
		public void addRuntimeListener(RuntimeListener listener) {
			assertNull(this.listener);
			this.listener = listener;
		}
		
		@Override
		public void setIndexedProperty(String name, int index, Object value)
				throws ArooaException {
			this.property = name;
			this.type = "Indexed";
			this.value = value;
			this.index = index;
		}

		@Override
		public void setMappedProperty(String name, String key, Object value)
				throws ArooaException {
			this.property = name;
			this.type = "Mapped";
			this.value = value;
			this.key = key;
		}
		
		@Override
		public void setProperty(String name, Object value)
				throws ArooaException {
			this.property = name;
			this.type = "Simple";
			this.value = value;
		}
		
		@Override
		public void init() {
			this.listener.beforeInit(new RuntimeEvent(this));
		}
	}
	
	private class OurValueCreator implements ElementAction<InstanceConfiguration> {
		Object toCreate;
		
		OurValueCreator(Object toCreate) {
			this.toCreate = toCreate;
		}
		
		public InstanceConfiguration onElement(final ArooaElement element,
				ArooaContext context) {
						
			return new MockInstanceConfiguration(
					context.getRuntime().getClassIdentifier(),
					toCreate, 
					toCreate,
					element.getAttributes()) {
				
				@Override
				void init(InstanceRuntime ourWrapper, ArooaContext context)
						throws ArooaException {
					ourWrapper.getParentPropertySetter().parentSetProperty(
							element.getTag() + ": Value");
				}
			};
		}
	}
	
	private class OurComponentCreator implements ElementAction<InstanceConfiguration> {
		Object toCreate;
		
		OurComponentCreator(Object toCreate) {
			this.toCreate = toCreate;
		}
		
		public InstanceConfiguration onElement(final ArooaElement element,
				ArooaContext context) {
			
			return new MockInstanceConfiguration(
					context.getRuntime().getClassIdentifier(),
					toCreate, 
					toCreate, 
					element.getAttributes()) {
				@Override
				void init(InstanceRuntime ourWrapper, ArooaContext context)
						throws ArooaException {
					ourWrapper.getParentPropertySetter().parentSetProperty(
							element.getTag() + ": Component");
				}
			};
		}
	}
	
	
	
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
						public String getTextProperty() {
							throw new RuntimeException("Unexpected");
						}
						@Override
						public String getComponentProperty() {
							return "myComponent";
						}
						@Override
						public ConfiguredHow getConfiguredHow(String property) {
							return ConfiguredHow.ELEMENT;
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
	
	private class RootContext extends MockArooaContext {
		ArooaSession session = new OurArooaSession();
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return null;
		}
	}
	
	private class ParentContext extends MockArooaContext {
		ArooaSession session = new OurArooaSession();
		
		CurrentArooaRuntime runtime;
		
		public ParentContext(CurrentArooaRuntime runtime) {
			this.runtime = runtime;
		}
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return null;
		}
		
	}
	
	private class IntermediateContext extends MockArooaContext {
		int index;
		
		final ContainerRuntime runtime;
		final ArooaType type;
		
		public IntermediateContext(ArooaType type, 
				ContainerRuntime runtime) {
			this.type = type;
			this.runtime = runtime;
		}
		
		@Override
		public ArooaType getArooaType() {
			return type;
		}
		
		@Override
		public ArooaSession getSession() {
			return new OurArooaSession();
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}

		@Override
		public ArooaHandler getArooaHandler() {
			return runtime.getHandler();
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				@Override
				public int indexOf(ConfigurationNode child) {
					return index;
				}
			};
			
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return null;
		}
		
	}
		
	
	
	public static class MockSimpleObject {
		public void setMyComponent(Object component) { }
		public void setMyValue(Object value) { }
	}
	
	public static class MockIndexedObject {
		public void setMyComponent(int i, Object component) { }
		public void setMyValue(int i, Object value) { }
	}
	
	public static class MockMappedObject {
		public void setMyComponent(String key, Object component) { }
		public void setMyValue(String key, Object value) { }
	}
	

	public void testComponent() {

		MutableAttributes attrs = new MutableAttributes();
		
		RootContext rootContext = new RootContext();
		
		PropertyAccessor accessor = rootContext.getSession(
				).getTools().getPropertyAccessor();
		
		Object wrapped = new MockSimpleObject();
		
		ArooaClass arooaClass = accessor.getClassName(wrapped);
		
		ParentContext parentContext = new ParentContext(
				new CurrentArooaRuntime(
						new ObjectConfiguration(
								arooaClass,
								wrapped,
								attrs),
						rootContext));
		
		ContainerRuntimeFactory test = new ContainerRuntimeFactory(
				null, 
				new OurComponentCreator(new MockSimpleObject()));
		
		ContainerRuntime returned = 
			test.runtimeForProperty(
					new ArooaElement("myComponent"), 
					parentContext);
		
		ArooaContext c2 = new IntermediateContext(ArooaType.COMPONENT, returned);

		ArooaContext fruitContext = c2.getArooaHandler().onStartElement(
				new ArooaElement("fruit"), c2);

		returned.setContext(fruitContext);
		
		fruitContext.getRuntime().init();
		returned.init();
		
		assertEquals("Simple", parentContext.runtime.type);
		assertEquals("myComponent", parentContext.runtime.property);
		assertEquals("fruit: Component", parentContext.runtime.value);
	}
	
	public void testValue() {

		MutableAttributes attrs = new MutableAttributes();
		
		RootContext rootContext = new RootContext();
		
		PropertyAccessor accessor = rootContext.getSession(
			).getTools().getPropertyAccessor();

		Object wrapped = new MockSimpleObject();
		
		ArooaClass arooaClass = accessor.getClassName(wrapped);
		
		ParentContext context = new ParentContext(
				new CurrentArooaRuntime(
						new ObjectConfiguration(
								arooaClass,
								wrapped,
								attrs),
						rootContext));
		
		ContainerRuntimeFactory test = new ContainerRuntimeFactory(
				new OurValueCreator(new MockSimpleObject()), 
				null);

		ContainerRuntime returned = 
			 test.runtimeForProperty(
					 new ArooaElement("myValue"), 
					 context);
		
		ArooaContext c2 = new IntermediateContext(ArooaType.VALUE, returned);

		ArooaContext fruitContext = c2.getArooaHandler().onStartElement(
				new ArooaElement("fruit"), c2);
	
		returned.setContext(fruitContext);
		
		fruitContext.getRuntime().init();
		returned.init();
		
		assertEquals("Simple", context.runtime.type);
		assertEquals("myValue", context.runtime.property);
		assertEquals("fruit: Value", context.runtime.value);
	}
		
	public void testMappedComponent() {

		RootContext rootContext = new RootContext();
		
		PropertyAccessor accessor = rootContext.getSession(
			).getTools().getPropertyAccessor();

		Object wrapped = new MockMappedObject();
		
		ArooaClass arooaClass = accessor.getClassName(wrapped);

		ParentContext context = new ParentContext(
				new CurrentArooaRuntime(
						new ObjectConfiguration(
								arooaClass,
								wrapped,
								new MutableAttributes()),
						rootContext));

		ArooaElement element = new ArooaElement("myComponent");
		
		ContainerRuntimeFactory test = new ContainerRuntimeFactory(
				null, 
				new OurComponentCreator(new MockMappedObject()));
		
		ContainerRuntime returned = test.runtimeForProperty(
				element, context);
		
		ArooaContext c2 = new IntermediateContext(
				ArooaType.COMPONENT, returned);

		MutableAttributes attrs = new MutableAttributes();
		attrs.set("key", "red");

		ArooaContext fruitContext = c2.getArooaHandler().onStartElement(
				new ArooaElement("fruit", attrs), c2);
	
		returned.setContext(fruitContext);
		
		fruitContext.getRuntime().init();
		returned.init();
		
		assertEquals("Mapped", context.runtime.type);
		assertEquals("myComponent", context.runtime.property);
		assertEquals("fruit: Component", context.runtime.value);
		assertEquals("red", context.runtime.key);
	}

	public void testMappedValue() {

		RootContext rootContext = new RootContext();
		
		PropertyAccessor accessor = rootContext.getSession(
			).getTools().getPropertyAccessor();

		Object wrapped = new MockMappedObject();
		
		ArooaClass arooaClass = accessor.getClassName(wrapped);

		ParentContext context = new ParentContext(
				new CurrentArooaRuntime(
						new ObjectConfiguration(
								arooaClass,
								wrapped,
								new MutableAttributes()),
						rootContext));

		ContainerRuntimeFactory test = new ContainerRuntimeFactory(
				new OurValueCreator(
						new MockMappedObject()), 
						null);
		
		ContainerRuntime returned = 
			test.runtimeForProperty(new ArooaElement("myValue"), 
					context);
		
		ArooaContext c2 = new IntermediateContext(
				ArooaType.VALUE, returned);

		MutableAttributes attrs = new MutableAttributes();
		attrs.set("key", "red");
		
		ArooaContext child = c2.getArooaHandler().onStartElement(
				new ArooaElement("fruit", attrs), c2);
	
		returned.setContext(child);
		
		child.getRuntime().init();
		returned.init();
		
		assertEquals("Mapped", context.runtime.type);
		assertEquals("myValue", context.runtime.property);
		assertEquals("fruit: Value", context.runtime.value);
		assertEquals("red", context.runtime.key);
	}
	
	public void testIndexedComponent() {

		RootContext rootContext = new RootContext();
		
		PropertyAccessor accessor = rootContext.getSession(
			).getTools().getPropertyAccessor();

		Object indexed = new MockIndexedObject();
		
		ArooaClass arooaClass = accessor.getClassName(indexed);
		
		ParentContext context = new ParentContext(
				new CurrentArooaRuntime(
						new ObjectConfiguration(
								arooaClass,
								indexed,
								new MutableAttributes()),
						new RootContext()));

		ContainerRuntimeFactory test = new ContainerRuntimeFactory(
				null, 
				new OurComponentCreator(new MockIndexedObject()));
		
		ContainerRuntime returned = test.runtimeForProperty(
				new ArooaElement("myComponent"), 
				context);
		
		IntermediateContext c2 = new IntermediateContext(
				ArooaType.COMPONENT, returned);
		c2.index = 99;
		
		ArooaContext child = c2.getArooaHandler().onStartElement(
				new ArooaElement("fruit"), c2);
			
		returned.setContext(child);
		
		child.getRuntime().init();
		returned.init();
		
		assertEquals("Indexed", context.runtime.type);
		assertEquals("myComponent", context.runtime.property);
		assertEquals("fruit: Component", context.runtime.value);
		assertEquals(99, context.runtime.index);

	}

	public void testIndexedValue() {
		
		RootContext rootContext = new RootContext();
		
		PropertyAccessor accessor = rootContext.getSession(
			).getTools().getPropertyAccessor();

		Object indexed = new MockIndexedObject();
		
		ArooaClass arooaClass = accessor.getClassName(indexed);
		
		ParentContext context = new ParentContext(
				new CurrentArooaRuntime(
						new ObjectConfiguration(
								arooaClass,
								indexed,
								new MutableAttributes()),
						rootContext));
		
		
		ContainerRuntimeFactory test = new ContainerRuntimeFactory(
				new OurValueCreator(new MockIndexedObject()), 
				null);
		
		ContainerRuntime returned = test.runtimeForProperty(
				new ArooaElement("myValue"), 
				context);
		
		IntermediateContext c2 = new IntermediateContext(
				ArooaType.VALUE, returned);
		c2.index = 99;
		
		ArooaContext child = c2.getArooaHandler().onStartElement(
				new ArooaElement("fruit"), c2);
			
		returned.setContext(child);
		
		child.getRuntime().init();
		returned.init();
		
		assertEquals("Indexed", context.runtime.type);
		assertEquals("myValue", context.runtime.property);
		assertEquals("fruit: Value", context.runtime.value);
		assertEquals(99, context.runtime.index);

	}
}
