package org.oddjob.arooa.design;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaTools;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockArooaTools;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.standard.StandardArooaSession;

public class InstanceSupportTest extends TestCase {

	public static class Fruit {
		
	}
	
	public static class Apple {
	}
	
	public static class Orange {
		
	}

	static final ArooaElement APPLE;
	static final ArooaElement ORANGE;
	
	static {
		try {
			APPLE = new ArooaElement(new URI("http://fruit"), "apple");
			ORANGE = new ArooaElement(new URI("http://fruit"), "orange");
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private class OurMappings extends MockElementMappings {
		
		@Override
		public ArooaElement[] elementsFor(InstantiationContext parentContext) {
			return new ArooaElement[] { APPLE, ORANGE };
		}
		
//		@Override
//		public DesignInstance designFor(ArooaElement element, 
//				ArooaContext parentContext) {
//			return null;
//		}
		
		@Override
		public ArooaClass mappingFor(ArooaElement element,
				InstantiationContext parentContext) {
			if ("apple".equals(element.getTag())) {
				return new SimpleArooaClass(Apple.class);
			}
			if ("orange".equals(element.getTag())) {
				return new SimpleArooaClass(Orange.class);
			}
			throw new RuntimeException(element.toString());
		}
	}
	
	private class ExistingSession extends MockArooaSession {
		@Override
		public ArooaTools getTools() {
			return new MockArooaTools() {
				@Override
				public PropertyAccessor getPropertyAccessor() {
					return new BeanUtilsPropertyAccessor();
				}
				
				@Override
				public ArooaConverter getArooaConverter() {
					return new DefaultConverter();
				}
			};
		}
		@Override
		public ArooaDescriptor getArooaDescriptor() {
			return new MockArooaDescriptor() {
				
				@Override
				public ArooaBeanDescriptor getBeanDescriptor(
						ArooaClass classIdentifier, PropertyAccessor accessor) {
					return null;
				}

				@Override
				public ElementMappings getElementMappings() {
					return new MappingsSwitch(
							new MockElementMappings(),
							new OurMappings());
				}
			};
		}
	}
	
	PrefixMappings prefixMappings = new SimplePrefixMappings();
	
	{
		try {
			prefixMappings.put("fruit", new URI("http://fruit"));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}
	
	private class OurContext extends MockArooaContext {
	
		@Override
		public PrefixMappings getPrefixMappings() {
			return prefixMappings;
		}
			
		@Override
		public ArooaSession getSession() {
			return new ExistingSession();
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				@Override
				public int insertChild(ConfigurationNode child) {
					return -1;
				}
				
				@Override
				public int indexOf(ConfigurationNode child) {
					return 0;
				}
			};
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
			};
		}
	}
	

	private class OurDesign extends MockDesignInstance {
		
		@Override
		public ArooaContext getArooaContext() {
			return new OurContext();
		}
	}
	
	public void testInsert() throws ArooaParseException {
		
		IndexedDesignProperty property = new IndexedDesignProperty(
				"fruit", Fruit.class, ArooaType.VALUE, new OurDesign()); 
		
		InstanceSupport test = new InstanceSupport(property);

		test.insertTag(0, prefixMappings.getQName(APPLE));
		
		assertEquals(1, property.instanceCount());
		
		test.insertTag(0, prefixMappings.getQName(ORANGE));
		
		assertEquals(2, property.instanceCount());

		assertEquals(ORANGE, property.instanceAt(0).element());
		assertEquals(APPLE, property.instanceAt(1).element());
		
	}
	
	public void testRemove() throws ArooaParseException {
		
		IndexedDesignProperty property = new IndexedDesignProperty(
				"fruit", Fruit.class, ArooaType.VALUE, new OurDesign()); 
		
		InstanceSupport test = new InstanceSupport(property);

		test.insertTag(0, prefixMappings.getQName(APPLE));
		
		assertEquals(1, property.instanceCount());
		
		test.removeInstance(property.instanceAt(0));
		
		assertEquals(0, property.instanceCount());
	}
	
	public void testSupportForStandardDescriptor() {

		final ArooaSession session = new StandardArooaSession();
		
		final ArooaContext context = new MockArooaContext() {
			@Override
			public ArooaSession getSession() {
				return session;
			} 
			
			@Override
			public PrefixMappings getPrefixMappings() {
				return new SimplePrefixMappings();
			}
			
			@Override
			public ConfigurationNode getConfigurationNode() {
				return new MockConfigurationNode() {
					@Override
					public int insertChild(ConfigurationNode child) {
						return -1;
					}
				};
			}
		};
		
		DesignInstance instance = new MockDesignInstance() {
			@Override
			public ArooaContext getArooaContext() {
				return context;
			}
		};
		
		SimpleDesignProperty property = new SimpleDesignProperty(
				"fruit", Fruit.class, ArooaType.VALUE, instance);
		
		InstanceSupport test = new InstanceSupport(property);
	
		QTag[] tags = test.getTags();
		
		assertEquals(5, tags.length);
		
		Set<QTag> set = new HashSet<QTag>(Arrays.asList(tags));
		
		assertTrue(set.contains(new QTag("is")));
		assertTrue(set.contains(new QTag("bean")));
		assertTrue(set.contains(new QTag("convert")));
		assertTrue(set.contains(new QTag("import")));
		assertTrue(set.contains(new QTag("value")));
	}
	
	public void testComponentSupportForStandardDescriptor() {

		final ArooaSession session = new StandardArooaSession();
		
		final ArooaContext context = new MockArooaContext() {
			@Override
			public ArooaSession getSession() {
				return session;
			} 
			
			@Override
			public PrefixMappings getPrefixMappings() {
				return new SimplePrefixMappings();
			}
			
			@Override
			public ConfigurationNode getConfigurationNode() {
				return new MockConfigurationNode() {
					@Override
					public int insertChild(ConfigurationNode child) {
						return -1;
					}
				};
			}
		};
		
		DesignInstance instance = new MockDesignInstance() {
			@Override
			public ArooaContext getArooaContext() {
				return context;
			}
		};
		
		SimpleDesignProperty property = new SimpleDesignProperty(
				"fruit", Fruit.class, ArooaType.COMPONENT, instance);
		
		InstanceSupport test = new InstanceSupport(property);
	
		QTag[] tags = test.getTags();
		
		assertEquals(2, tags.length);
		
		Set<QTag> set = new HashSet<QTag>(Arrays.asList(tags));
		
		assertTrue(set.contains(new QTag("is")));
		assertTrue(set.contains(new QTag("bean")));
	}
	
	
	public void testTagFor() {
		
		ArooaSession session = new StandardArooaSession();
		
		DesignSeedContext context = new DesignSeedContext(
				ArooaType.COMPONENT, session);
		
		DesignValueBase instance = new DesignValueBase(
				new ArooaElement("test"), 
				new SimpleArooaClass(Object.class),
				context) {
			
			@Override
			protected DesignProperty[] children() {
				return null;
			}

			public Form detail() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		QTag result = InstanceSupport.tagFor(instance);
		
		assertEquals("", result.getPrefix());
		assertEquals("test", result.getTag());
		
	}
	
	public void testTagForNoURI() throws URISyntaxException {
		
		ArooaSession session = new StandardArooaSession();
		
		DesignSeedContext context = new DesignSeedContext(
				ArooaType.COMPONENT, session);
		
		DesignValueBase instance = new DesignValueBase(
				new ArooaElement(
						new URI("http://fruit"), "test"), 
				new SimpleArooaClass(Object.class),
				context) {
			
			@Override
			protected DesignProperty[] children() {
				return null;
			}

			public Form detail() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		try {
			InstanceSupport.tagFor(instance);
			fail("Should fail");
		} catch (Exception e) {
			assertEquals("No prefix for http://fruit", e.getMessage());
		}
		
	}
	
}
