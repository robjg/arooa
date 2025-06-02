package org.oddjob.arooa.reflect;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.oddjob.arooa.*;
import org.oddjob.arooa.handlers.ElementAction;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PropertyIdentifierTest extends Assert {

	private static class Result {
		String element;
		String internal;
	}
	
	private static class StringElementActionFactory implements PropertyIdentifier.ElementActionFactory<String> {
		public ElementAction<String> createComponentElementAction() {
			return (element, context) -> "Component: " + element.getTag();
		}
		public ElementAction<String> createValueElementAction() {
			return (element, context) -> "Value: " + element.getTag();
		}
	}		

	private static class MockAction implements PropertyIdentifier.PropertyTypeActions<Result, String> {
		
		
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

	ArooaTools mockArooaTools() {

		BeanOverview indexedOverview = mock(BeanOverview.class);
		when(indexedOverview.isIndexed(anyString())).thenReturn(true);
		when(indexedOverview.isMapped(anyString())).thenReturn(false);

		BeanOverview mappedOverview = mock(BeanOverview.class);
		when(mappedOverview.isIndexed(anyString())).thenReturn(false);
		when(mappedOverview.isMapped(anyString())).thenReturn(true);

		BeanOverview simpleOverview = mock(BeanOverview.class);
		when(simpleOverview.isIndexed(anyString())).thenReturn(false);
		when(simpleOverview.isMapped(anyString())).thenReturn(false);

		PropertyAccessor propertyAccessor = mock(PropertyAccessor.class);
		doAnswer(invocationOnMock -> {
			final Class<?> forClass = invocationOnMock.getArgument(0);
			if (forClass.getName().contains("Indexed")) {
				return indexedOverview;
			}
			else if (forClass.getName().contains("Mapped")) {
				return mappedOverview;
			}
			else {
				return simpleOverview;
			}
		}).when(propertyAccessor).getBeanOverview(ArgumentMatchers.any(Class.class));

		ArooaTools arooaTools = mock(ArooaTools.class);
		when(arooaTools.getPropertyAccessor()).thenReturn(propertyAccessor);

		return arooaTools;
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
			return mockArooaTools();
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
	
	static class MockObject {
//		public void setMyComponent(Object component) { }
//		public void setMyValue(Object value) { }
	}
	
	static class MockIndexedObject {
//		public void setMyIndexComponent(int i, Object component) { }
//		public void setMyIndexedValue(int i, Object value) { }
	}
	
	static class MockMappedObject {
//		public void setMyMappedComponent(String name, Object component) { }
//		public void setMyMappedValue(String name, Object value) { }
	}

   @Test
	public void testComponent() throws ArooaPropertyException {

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<>(
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

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<>(
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

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<>(
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

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<>(
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

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<>(
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

		PropertyIdentifier<Result, String> test = new PropertyIdentifier<>(
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
