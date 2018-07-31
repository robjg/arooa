package org.oddjob.arooa.design;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.MockComponentPool;
import org.oddjob.arooa.standard.ExtendedTools;
import org.oddjob.arooa.standard.StandardTools;

public class DesignComponentInstanceTest extends Assert {

	public static class MyComponent {
	
		public void setChildren(int index, Object child) {}
		
	}

	public static class OurDesignFactory implements DesignFactory {
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			
			return new DesignComponentInstance(
					element, new SimpleArooaClass(MyComponent.class), parentContext);
		}
	}
	
	private class OurDescriptor extends MockArooaDescriptor {

		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
							InstantiationContext propertyContext) {
						assertEquals(new ArooaElement("acomp"), element);
						
						return new SimpleArooaClass(MyComponent.class);
					}
					
					@Override
					public ArooaElement[] elementsFor(
							InstantiationContext propertyContext) {
						
						return new ArooaElement[] { new ArooaElement("acomp") };
					}
					
					@Override
					public DesignFactory designFor(ArooaElement element,
							InstantiationContext propertyContext) {
						assertEquals(new ArooaElement("acomp"), element);
						return new OurDesignFactory();
					}
				}, new MockElementMappings());
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {
			
			assertEquals(new SimpleArooaClass(MyComponent.class), 
					classIdentifier);
			
			return new MockArooaBeanDescriptor() {
				@Override
				public String getComponentProperty() {
					return "children";
				}
			};
		}
		
		@Override
		public ClassResolver getClassResolver() {
			return new ClassLoaderClassResolver(getClass().getClassLoader());
		}
	}
	
	private class OurSession extends MockArooaSession {
		
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new OurDescriptor();
		}
		
		@Override
		public ArooaTools getTools() {
			return new ExtendedTools(new StandardTools(), new OurDescriptor());
		}
		
		@Override
		public ComponentPool getComponentPool() {
			return new MockComponentPool() {
			};
		}
	}
	
	private class OurContext extends MockArooaContext {
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return new SimplePrefixMappings();
		}
		
		@Override
		public ArooaSession getSession() {
			return new OurSession();
		}
		
		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				public ArooaContext onStartElement(ArooaElement element,
						ArooaContext parentContext) throws ArooaException {
					assertEquals(new ArooaElement("acomp"), element);
					
					return parentContext;
				}
			};
		}
	}
		

   @Test
	public void testAddComponent() throws ArooaParseException {
		
		ArooaElement element = new ArooaElement("acomp");
		
		DesignComponentInstance test = new DesignComponentInstance(
				element, new SimpleArooaClass(MyComponent.class), new OurContext());
			
		IndexedDesignProperty property = new IndexedDesignProperty(
				"children", Object.class, ArooaType.COMPONENT, test);
		
		test.children(new DesignProperty[] { property });
		
		class OurStructuralListener implements DesignListener {
			DesignInstance child;
			
			public void childAdded(DesignStructureEvent event) {
				assertNull(child);
				this.child = event.getChild();
			}
			
			public void childRemoved(DesignStructureEvent event) {
				fail("unexpected.");
			}
		}

		OurStructuralListener listener = new OurStructuralListener();
		
		test.addStructuralListener(listener);
		
		assertNull(listener.child);
		
		InstanceSupport support = new InstanceSupport(
				property);
		
		support.insertTag(0, new QTag("acomp"));
		
		assertNotNull(listener.child);
	}
	
	
   @Test
	public void testRemoveComponent() throws ArooaParseException {
		
		ArooaElement element = new ArooaElement("acomp");
		
		DesignComponentInstance test = new DesignComponentInstance(
				element, new SimpleArooaClass(MyComponent.class), new OurContext());
			
		IndexedDesignProperty property = new IndexedDesignProperty(
				"children", Object.class, ArooaType.COMPONENT, test);
		
		test.children(new DesignProperty[] { property });

		InstanceSupport support = new InstanceSupport(
				property);
		
		support.insertTag(0, new QTag("acomp"));
		

		
		class OurStructuralListener implements DesignListener {
			DesignInstance added;
			DesignInstance removed;
			
			public void childAdded(DesignStructureEvent event) {
				this.added = event.getChild();
			}
			
			public void childRemoved(DesignStructureEvent event) {
				assertNull(removed);
				this.removed = event.getChild();
			}
		}

		OurStructuralListener listener = new OurStructuralListener();
		
		test.addStructuralListener(listener);
		
		assertNull(listener.removed);

		CutAndPasteSupport cutAndPaste = new CutAndPasteSupport(
				test.getArooaContext());

		cutAndPaste.cut(listener.added.getArooaContext());
		
		assertNotNull(listener.removed);
	}	
}
