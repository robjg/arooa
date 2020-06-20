package org.oddjob.arooa.deploy;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.MockArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;

public class ListDescriptorTest extends Assert {

	public static class Fruit {
		
	}
	
	public static class Carrot{
		
	}
	
	public static class Apple {
		
	}
	
   @Test
	public void testComponentMapping() {
	
		ArooaDescriptor second = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return new MappingsSwitch(new MockElementMappings() {
						@Override
						public ArooaClass mappingFor(ArooaElement element,
								InstantiationContext propertyContext) {
							
							if ("apple".equals(element.getTag())) {
								return new SimpleArooaClass(Fruit.class);
							}
							return null;
						}
					}, null);
			}
		};
		
		ArooaDescriptor first = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return new MappingsSwitch(new MockElementMappings() {
						@Override
						public ArooaClass mappingFor(ArooaElement element,
								InstantiationContext propertyContext) {
							
							if ("carrot".equals(element.getTag())) {
								return new SimpleArooaClass(Carrot.class);
							}
							if ("apple".equals(element.getTag())) {
								return new SimpleArooaClass(Apple.class);
							}
							return null;
						}
					}, null);
			}
		};
		
		ListDescriptor test = new ListDescriptor();
		
		test.addDescriptor(first);
		test.addDescriptor(second);
		
		ElementMappings mappings = test.getElementMappings();
		
		ArooaClass result;
		
		result = mappings.mappingFor(
				new ArooaElement("carrot"), 
				new InstantiationContext(ArooaType.COMPONENT, null));		
		assertEquals(Carrot.class, result.forClass());

		result = mappings.mappingFor(
				new ArooaElement("apple"), 
				new InstantiationContext(ArooaType.COMPONENT, null));
		assertEquals(Fruit.class, result.forClass());
		
		assertNull(mappings.mappingFor(
				new ArooaElement("pineapple"), 
				new InstantiationContext(ArooaType.COMPONENT, null)));
	}
	
   @Test
	public void testValueMapping() {
		
		ArooaDescriptor second = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return new MappingsSwitch(null, new MockElementMappings() {
						@Override
						public ArooaClass mappingFor(ArooaElement element,
								InstantiationContext parentContext) {
							
							if ("apple".equals(element.getTag())) {
								return new SimpleArooaClass(Fruit.class);
							}
							return null;
						}
					});
			}
		};
		
		ArooaDescriptor first = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return new MappingsSwitch(null, new MockElementMappings() {
						@Override
						public ArooaClass mappingFor(ArooaElement element,
								InstantiationContext parentContext) {
							
							if ("carrot".equals(element.getTag())) {
								return new SimpleArooaClass(Carrot.class);
							}
							if ("apple".equals(element.getTag())) {
								return new SimpleArooaClass(Apple.class);
							}
							return null;
						}
					});
			}
		};
		
		ListDescriptor test = new ListDescriptor();
		
		test.addDescriptor(first);
		test.addDescriptor(second);
				
		ElementMappings mappings = 
			test.getElementMappings();
		
		ArooaClass result;
		
		result = mappings.mappingFor(
				new ArooaElement("carrot"), 
				new InstantiationContext(ArooaType.VALUE, null));		
		assertEquals(Carrot.class, result.forClass());
		
		result = mappings.mappingFor(
				new ArooaElement("apple"), 
				new InstantiationContext(ArooaType.VALUE, null));
		assertEquals(Fruit.class, result.forClass());
		
		assertNull(mappings.mappingFor(
				new ArooaElement("pineapple"), 
				new InstantiationContext(ArooaType.VALUE, null)));		
	}
			
   @Test
	public void testValueMappingOneNull() {
		
		ArooaDescriptor primary = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
		};
		
		ArooaDescriptor secondary = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return new MappingsSwitch(null, new MockElementMappings() {
						@Override
						public ArooaClass mappingFor(ArooaElement element,
								InstantiationContext parent) {
							
							if ("carrot".equals(element.getTag())) {
								return new SimpleArooaClass(Carrot.class);
							}
							if ("apple".equals(element.getTag())) {
								return new SimpleArooaClass(Apple.class);
							}
							return null;
						}
					});
			}			
		};
		
		ListDescriptor test = new ListDescriptor();
		
		test.addDescriptor(primary);
		test.addDescriptor(secondary);
		
		ElementMappings mappings = 
			test.getElementMappings();
		
		ArooaClass result;
		
		result = mappings.mappingFor(
				new ArooaElement("carrot"), 
				new InstantiationContext(ArooaType.VALUE, null));
		assertEquals(new SimpleArooaClass(Carrot.class), result);
		
		result = mappings.mappingFor(
				new ArooaElement("apple"), 
				new InstantiationContext(ArooaType.VALUE, null));
		assertEquals(new SimpleArooaClass(Apple.class), result);
		
		assertNull(mappings.mappingFor(
				new ArooaElement("pineapple"), 
				new InstantiationContext(ArooaType.VALUE, null)));
	}	
	
	class OurRegistry extends MockConvertletRegistry {
		Convertlet<?, ?> convertlet;

		public <F> void registerJoker(Class<F> from, Joker<F> joker) {
			throw new RuntimeException("Unexpected.");
		}
		
		public <F, T> void register(Class<F> from, Class<T> to,
				Convertlet<F, T> convertlet) {
			this.convertlet = convertlet;
		}
	}
	
   @Test
	public void testConvertletProvider() {
		
		ArooaDescriptor primary = new MockArooaDescriptor() {
			@Override
			public ConversionProvider getConvertletProvider() {
				return null;
			}
			
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
		};
		
		ArooaDescriptor secondary = new MockArooaDescriptor() {
			@Override
			public ConversionProvider getConvertletProvider() {
				return new ConversionProvider() {
					public void registerWith(ConversionRegistry registry) {
						
						registry.register(String.class, Object.class,
								new Convertlet<String, Object>() {
									public Object convert(String from)
											throws ConvertletException {
										return from;
									}
								});
					}
				};
			}
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
		};
		
		ListDescriptor test = new ListDescriptor();
		
		test.addDescriptor(primary);
		test.addDescriptor(secondary);
				
		ConversionProvider provider = test.getConvertletProvider();
		
		OurRegistry registry = new OurRegistry();
		
		provider.registerWith(registry);
		
		assertNotNull(registry.convertlet);
	}

   @Test
	public void testConvertletProviderOrder() 
	throws NoConversionAvailableException, ConversionFailedException {
		
		class OurDescriptor1 extends MockArooaDescriptor {
			
			@Override
			public ConversionProvider getConvertletProvider() {
				return new ConversionProvider() {;
					public void registerWith(ConversionRegistry registry) {
						registry.register(String.class, Integer.class, 
								new Convertlet<String, Integer>() {
							public Integer convert(String from)
									throws ConvertletException {
								return new Integer(42);
							}
						});
					}
				};
			}
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
		}
		
		class OurDescriptor2 extends MockArooaDescriptor {
			
			@Override
			public ConversionProvider getConvertletProvider() {
				return new ConversionProvider() {;
					public void registerWith(ConversionRegistry registry) {
						registry.register(String.class, Integer.class, 
								new Convertlet<String, Integer>() {
							public Integer convert(String from)
									throws ConvertletException {
								return new Integer(110);
							}
						});
					}
				};
			}
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
			
		}
		
		ListDescriptor descriptor = new ListDescriptor(
				new ArooaDescriptor[] { 
						new OurDescriptor1(), new OurDescriptor2() });

		DefaultConversionRegistry reg = new DefaultConversionRegistry();
		
		descriptor.getConvertletProvider().registerWith(reg);
		
		ArooaConverter converter = new DefaultConverter(reg);
		
		int i = converter.convert("anything", Integer.class);
		
		assertEquals(110, i);
		
		descriptor = new ListDescriptor();
		descriptor.addDescriptor(new OurDescriptor1());
		descriptor.addDescriptor(new OurDescriptor2());
		
		reg = new DefaultConversionRegistry();
						
		descriptor.getConvertletProvider().registerWith(reg);
						
		converter = new DefaultConverter(reg);
						
		i = converter.convert("anything", Integer.class);
		
		assertEquals(110, i);
	}
	
   @Test
	public void testClassFestResources() {
		
		ArooaDescriptor primary = new MockArooaDescriptor() {
			
			@Override
			public ClassResolver getClassResolver() {
				return new MockClassResolver() {
					@Override
					public URL[] getResources(String resource) {
						try {
							return new URL[] {
								new URL("file:/a/b/c")
							};
						} catch (MalformedURLException e) {
							throw new RuntimeException(e);
						}
					}
				};
			}
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
		};
		
		ArooaDescriptor secondary = new MockArooaDescriptor() {
			
			@Override
			public ClassResolver getClassResolver() {
				return new MockClassResolver() {
					@Override
					public URL[] getResources(String resource) {
						try {
							return new URL[] {
								new URL("file:/a/b/c")
							};
						} catch (MalformedURLException e) {
							throw new RuntimeException(e);
						}
					}
				};
			}
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
		};
		
		ListDescriptor test = new ListDescriptor();
		
		test.addDescriptor(primary);
		test.addDescriptor(secondary);
				
		URL[] result = test.getClassResolver().getResources("ignored");
		assertEquals(1, result.length);
	}
	
	private static class BeanDescriptorA extends MockArooaBeanDescriptor {
		
	}
	
	private static class BeanDescriptorB extends MockArooaBeanDescriptor {
		
	}
	
   @Test
	public void testBeanDescriptor() {

		ArooaDescriptor first = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
			@Override
			public ArooaBeanDescriptor getBeanDescriptor(
					ArooaClass classIdentifier, PropertyAccessor accessor) {
				return new BeanDescriptorA();
			}
		};
		
		ArooaDescriptor second = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
			@Override
			public ArooaBeanDescriptor getBeanDescriptor(
					ArooaClass classIdentifier, PropertyAccessor accessor) {
				return new BeanDescriptorB();
			}
		};

		ListDescriptor test = new ListDescriptor(
				new ArooaDescriptor[] { first, second });
		
		ArooaBeanDescriptor result = test.getBeanDescriptor(
				new MockArooaClass(), null);
		
		assertEquals(BeanDescriptorB.class, result.getClass());
	}

	@Test
	public void testNamespaceMappings() throws URISyntaxException {

		URI uri1 = new URI("test:red");

		ArooaDescriptorBean df1 = new ArooaDescriptorBean();
		df1.setNamespace(uri1);
		df1.setPrefix("r");

		URI uri2 = new URI("test:green");

		ArooaDescriptorBean df2 = new ArooaDescriptorBean();
		df2.setNamespace(uri2);
		df2.setPrefix("g");

		ClassLoader cl = getClass().getClassLoader();

		ArooaDescriptor test = new ListDescriptor(df1.createDescriptor(cl),
				df2.createDescriptor(cl));

		assertThat(test.getUriFor("r"), is(uri1));
		assertThat(test.getPrefixFor(uri1), is("r"));
		assertThat(test.getUriFor("g"), is(uri2));
		assertThat(test.getPrefixFor(uri2), is("g"));

		assertThat(test.getPrefixFor(new URI("test:blue")), nullValue());

		assertThat(Arrays.asList(test.getPrefixes()), contains("r", "g"));
	}
}
