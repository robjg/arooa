package org.oddjob.arooa.deploy;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockClassResolver;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.MockConvertletRegistry;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;

public class LinkedDescriptorTest extends Assert {

	public static class Apple {
		
	}
	
	public static class Carrot {
		
	}
	
   @Test
	public void testComponentMapping() {
	
		ArooaDescriptor primary = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return new MappingsSwitch(new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
							InstantiationContext propertyContext) {
						
						if ("apple".equals(element.getTag())) {
							return new SimpleArooaClass(Apple.class);
						}
						return null;
					}
				}, null);
			}
		};
		
		ArooaDescriptor secondary = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return new MappingsSwitch(new MockElementMappings() {
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
				}, null);
			}
		};
		
		LinkedDescriptor test = new LinkedDescriptor(primary, secondary);
		
		ElementMappings mappings = 
			test.getElementMappings();

		ArooaClass result;
		
		result = mappings.mappingFor(
				new ArooaElement("carrot"), 
				new InstantiationContext(ArooaType.COMPONENT, null));
		assertEquals(new SimpleArooaClass(Carrot.class), result);
		
		result = mappings.mappingFor(
				new ArooaElement("apple"), 
				new InstantiationContext(ArooaType.COMPONENT, null));
		assertEquals(new SimpleArooaClass(Apple.class), result);
		
		assertNull(mappings.mappingFor(
				new ArooaElement("pineapple"), 
				new InstantiationContext(ArooaType.COMPONENT, null)));
	}
	
   @Test
	public void testValueMapping() {
		
		ArooaDescriptor primary = new MockArooaDescriptor() {
			@Override
			public ElementMappings getElementMappings() {
				return new MappingsSwitch(null,  new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
							InstantiationContext parentContext) {
						
						if ("apple".equals(element.getTag())) {
							return new SimpleArooaClass(String.class);
						}
						return null;
					}
				});
			}
		};
		
		ArooaDescriptor secondary = new MockArooaDescriptor() {
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
		
		LinkedDescriptor test = new LinkedDescriptor(primary, secondary);
		
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
		assertEquals(String.class, result.forClass());
		
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
		
		LinkedDescriptor test = new LinkedDescriptor(primary, secondary);
		
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
		
		LinkedDescriptor test = new LinkedDescriptor(primary, secondary);
		
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
		
		ArooaDescriptor descriptor = new LinkedDescriptor(
				new OurDescriptor1(), new OurDescriptor2() );

		DefaultConversionRegistry reg = new DefaultConversionRegistry();
		
		descriptor.getConvertletProvider().registerWith(reg);
		
		ArooaConverter converter = new DefaultConverter(reg);
		
		int i = converter.convert("anything", Integer.class);
		
		assertEquals(42, i);		
	}
	
   @Test
	public void testClassFestResources() {
		
		ArooaDescriptor primary = new MockArooaDescriptor() {
			
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
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
		};
		
		ArooaDescriptor secondary = new MockArooaDescriptor() {
			
			@Override
			public ElementMappings getElementMappings() {
				return null;
			}
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
		};
		
		LinkedDescriptor test = new LinkedDescriptor(
				primary, secondary);
						
		URL[] result = test.getClassResolver().getResources("ignored");
		assertEquals(1, result.length);
	}

}
