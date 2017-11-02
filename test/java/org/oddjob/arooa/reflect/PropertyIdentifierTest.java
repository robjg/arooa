package org.oddjob.arooa.reflect;

import org.junit.Test;

import org.junit.Assert;

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
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;

public class PropertyIdentifierTest extends Assert {

	private class Result {
		String element;
		String internal;
	}
	
	private class StringElementActionFactory implements PropertyIdentifier.ElementActionFactory<String> {
		public ElementAction<String> createComponentElementAction() {
			return new ElementAction<String>() {
				public String onElement(ArooaElement element, ArooaContext context) {
					return "Component: " + element.getTag();
				}
			};
		}
		public ElementAction<String> createValueElementAction() {
			return new ElementAction<String>() {
				public String onElement(ArooaElement element, ArooaContext context) {
					return "Value: " + element.getTag();
				}
			};
		}
	}		

	private class MockAction implements PropertyIdentifier.PropertyTypeActions<Result, String> {
		
		
		public Result onIndexedElement(ArooaElement element, ArooaContext context, ElementAction<String> action) {
			Result result = new Result();
			result.element = "Indexed: " + element;
			result.internal = action.onElement(new ArooaElement("fruit"), context);
			return result;
		}
		public Result onMappedElement(ArooaElement element, ArooaContext context, ElementAction<String> action) {
			Result result = new Result();
			result.element = "Mapped: " + element;
			result.internal = action.onElement(new ArooaElement("fruit"), context);
			return result;
		}
		public Result onVariantElement(ArooaElement element, ArooaContext context, ElementAction<String> action) {
			Result result = new Result();
			result.element = "Variant: " + element;
			result.internal = action.onElement(new ArooaElement("fruit"), context);
			return result;
		}
		
	}

	private class OurToolFactory extends MockArooaTools {
		
		@Override
		public PropertyAccessor getPropertyAccessor() {
			return new MockPropertyAccessor() {
			
				@Override
				public BeanOverview getBeanOverview(final Class<?> forClass)
				throws ArooaException {
					
					return new MockBeanOverview() {
						public boolean isIndexed(String property) {
							return forClass.getName().contains("Indexed");
						}
						public boolean isMapped(String property) {
							return forClass.getName().contains("Mapped");
						}
					};
				}
			};
		}		
	}
	
	class OurArooaSession extends MockArooaSession {
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new MockArooaDescriptor() {
				@Override
				public ArooaBeanDescriptor getBeanDescriptor(
						ArooaClass classIdentifier, PropertyAccessor accessor) {
					return new MockArooaBeanDescriptor() {
						@Override
						public ParsingInterceptor getParsingInterceptor() {
							throw new RuntimeException("Unexpected");
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
			return new OurToolFactory();
		}
	}
	
	class OurArooaContext extends MockArooaContext {
		private final ArooaType type;
		
		public OurArooaContext(ArooaType type) {
			this.type = type;
		}
		
		@Override
		public ArooaSession getSession() {
			return new OurArooaSession();
		}
		
		@Override
		public ArooaType getArooaType() {
			return type;
		}
	}
	
	class MockObject {
//		public void setMyComponent(Object component) { }
//		public void setMyValue(Object value) { }
	}
	
	class MockIndexedObject {
//		public void setMyIndexComponent(int i, Object component) { }
//		public void setMyIndexedValue(int i, Object value) { }
	}
	
	class MockMappedObject {
//		public void setMyMappedComponent(String name, Object component) { }
//		public void setMyMappedValue(String name, Object value) { }
	}

   @Test
	public void testComponent() throws ArooaPropertyException {

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<Result, String>(
				new StringElementActionFactory(),
				new MockAction());
		
		OurArooaContext context = new OurArooaContext(ArooaType.COMPONENT);

		ArooaElement element = new ArooaElement("myComponent");
		
		Result result = test.identifyPropertyFor(
				new SimpleArooaClass(MockObject.class), 
				element, 
				context);
		
		assertEquals("Variant: myComponent", result.element);
		assertEquals("Component: fruit", result.internal);
	}
	
   @Test
	public void testValue() {

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<Result, String>(
				new StringElementActionFactory(),
				new MockAction());
		
		OurArooaContext context = new OurArooaContext(ArooaType.VALUE);

		ArooaElement element = new ArooaElement("myValue");
		
		Result result = test.identifyPropertyFor(
				new SimpleArooaClass(MockObject.class), 
				element, 
				context);
		
		assertEquals("Variant: myValue", result.element);
		assertEquals("Value: fruit", result.internal);
	}
	
   @Test
	public void testMappedComponent() {

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<Result, String>(
				new StringElementActionFactory(),
				new MockAction());
		
		OurArooaContext context = new OurArooaContext(ArooaType.COMPONENT);

		ArooaElement element = new ArooaElement("myComponent");
		
		Result result = test.identifyPropertyFor(
				new SimpleArooaClass(MockMappedObject.class), 
				element, 
				context);
		
		assertEquals("Mapped: myComponent", result.element);
		assertEquals("Component: fruit", result.internal);
	}

   @Test
	public void testMappedValue() {

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<Result, String>(
				new StringElementActionFactory(),
				new MockAction());
		
		OurArooaContext context = new OurArooaContext(ArooaType.VALUE);

		ArooaElement element = new ArooaElement("myValue");
		
		Result result = test.identifyPropertyFor(
				new SimpleArooaClass(MockMappedObject.class), 
				element, 
				context);
		
		assertEquals("Mapped: myValue", result.element);
		assertEquals("Value: fruit", result.internal);
	}

   @Test
	public void testIndexedComponent() {

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<Result, String>(
				new StringElementActionFactory(),
				new MockAction());
		
		OurArooaContext context = new OurArooaContext(ArooaType.COMPONENT);

		ArooaElement element = new ArooaElement("myComponent");
		
		Result result = test.identifyPropertyFor(
				new SimpleArooaClass(MockIndexedObject.class), 
				element, 
				context);
		
		assertEquals("Indexed: myComponent", result.element);
		assertEquals("Component: fruit", result.internal);
	}

   @Test
	public void testIndexedValue() {

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<Result, String>(
				new StringElementActionFactory(),
				new MockAction());
		
		OurArooaContext context = new OurArooaContext(ArooaType.VALUE);

		ArooaElement element = new ArooaElement("myValue");
		
		Result result = test.identifyPropertyFor(
				new SimpleArooaClass(MockIndexedObject.class), 
				element, 
				context);
		
		assertEquals("Indexed: myValue", result.element);
		assertEquals("Value: fruit", result.internal);
	}

}
