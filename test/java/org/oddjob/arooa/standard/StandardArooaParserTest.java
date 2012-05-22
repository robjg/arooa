package org.oddjob.arooa.standard;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.registry.BeanRegistry;
import org.oddjob.arooa.types.ValueType;
import org.oddjob.arooa.xml.XMLConfiguration;


/**
 * Tests the Arooa framework can build something.
 */
public class StandardArooaParserTest extends TestCase {
	
	
	private class MockConfig implements ArooaConfiguration {
		public ConfigurationHandle parse(ArooaContext parentContext) throws ArooaParseException {

			MutableAttributes atts = new MutableAttributes();
			atts.set("x", "L");
			ArooaElement element = new ArooaElement("a", atts);
			
			ArooaContext context = parentContext.getArooaHandler(
					).onStartElement(element, parentContext);

			
			
			context.getRuntime().init();
			
			assertEquals("RootRuntime.", new SimpleArooaClass(O.class), 
					context.getRuntime().getClassIdentifier());
			
			return null;
		}
	}
	
	public static class O {
		String x;
		public void setX(String x) {
			this.x = x;
		}
	}
	
	public void testDocElementOnly() throws ArooaParseException {
		O o = new O();
		
		StandardArooaParser test = 
			new StandardArooaParser(o);
		
		test.parse(new MockConfig());
		
		assertEquals("L", o.x);
	}
	
	public static class HealthySnack implements Snack {

		private String description;
		private Fruit fruit;

		public String getDescription() {
			return description;
		}

		public void setDescription(String string) {
			description = string;
		}

		public void setFruit(Fruit b) {
			this.fruit = b;
		}
		
		public Fruit getFruit() {
			return fruit;
		}
	}

	public static class Fruit {

		private String name;
		
		public String getName() {
			return name;
		}

		public void setName(String string) {
			name = string;
		}
	}
	
    public static class RootComponentWithElement {
        HealthySnack snack;
        
        public void setSnack(HealthySnack a) {
            this.snack = a;
        }	        
    };

    private class ElementTestArooaDescriptor extends MockArooaDescriptor {
    	@Override
		public ElementMappings getElementMappings() {
				return null;
		}
    	
    	@Override
    	public ArooaBeanDescriptor getBeanDescriptor(ArooaClass forClass,
    			PropertyAccessor accessor) {
    		if (new SimpleArooaClass(
    				HealthySnack.class).equals(forClass)) {
    			return new MockArooaBeanDescriptor() {
    				@Override
    				public ParsingInterceptor getParsingInterceptor() {
    					return null;
    				}
    				public String getTextProperty() {
    					return "description";
    				}
    				public String getComponentProperty() {
    					return null;
    				}
    				@Override
    				public ConfiguredHow getConfiguredHow(String property) {
    					return ConfiguredHow.ELEMENT;
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
    		return null;
    	}
    	
    	@Override
    	public ConversionProvider getConvertletProvider() {
    		return null;
    	}
    	
    	@Override
    	public ClassResolver getClassResolver() {
    		return new ClassLoaderClassResolver(
    				getClass().getClassLoader());
    	}
    }
    
	public void testValuesAndText() throws ArooaParseException {

	    RootComponentWithElement root = new RootComponentWithElement();

		XMLConfiguration configuration = new XMLConfiguration("Test", 
				"<test>\n" +
				" <snack>\n" +
				"   <is>\n" +
				"<![CDATA[A healthy snack for between meals]]>\n" +
				"     <fruit>\n" +
				"       <is name='${my.fruit}'/>\n" +
				"     </fruit>\n" +
				"   </is>" +
				" </snack>\n" +
				"</test>\n");

		StandardArooaSession session = new StandardArooaSession(
						new ElementTestArooaDescriptor());

		BeanRegistry ids = 
			session.getBeanRegistry();
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("fruit", "Apple");
		ids.register("my", props);
		
		StandardArooaParser parser = new StandardArooaParser(root, 
				session);
		
		parser.parse(configuration);
		
		session.getComponentPool().configure(root);
		
		HealthySnack result = (HealthySnack) root.snack;
		assertNotNull("Result", result);
		assertNotNull("Result Description", result.getDescription());
		assertTrue(result.getDescription().equals("A healthy snack for between meals"));
		assertNotNull("Fruit", result.getFruit());
		assertNotNull("Fruit Name", result.getFruit().getName());
		assertTrue(result.getFruit().getName().equals("Apple"));
	}
	
	interface Snack {
		
	}

	interface Translated {
		public String toString();
	}
	
	public static class InternationalSnack implements Snack {

		private Translated description;
		private Fruit fruit;

		public Translated getDescription() {
			return description;
		}

		public void setDescription(Translated string) {
			description = string;
		}

		public void setFruit(Fruit b) {
			this.fruit = b;
		}
		
		public Fruit getFruit() {
			return fruit;
		}
	}

	
    public static class RootComponent {
        Snack snack;
        
        public void setSnack(Snack a) {
            this.snack = a;
        }	        
    };

    private class OurComponentMappings extends MockElementMappings {
    	
    	@Override
    	public ArooaClass mappingFor(ArooaElement element,
    			InstantiationContext parentContext) {
    		assertEquals("healthy", element.getTag());
    		return new SimpleArooaClass(InternationalSnack.class);
    	}
    	
    }
    
    private class OurValueMappings extends MockElementMappings {

    	@Override
    	public ArooaClass mappingFor(ArooaElement element,
    			InstantiationContext parentContext) {
    		if (element.getTag().equals("translations")) {
    			return new SimpleArooaClass(Translations.class);
    		}
    		return null;
    	}
    	
    }
    
    private class ComponentAndStandinArooaDescriptor extends MockArooaDescriptor {
    	@Override
    	public ArooaBeanDescriptor getBeanDescriptor(
    			ArooaClass classIdentifier, PropertyAccessor accessor) {
    		if (classIdentifier == null) {
    			return null;
    		}
    		if (new SimpleArooaClass(RootComponent.class).equals(
    				classIdentifier)) {
	    		return new MockArooaBeanDescriptor() {
	    			@Override
    				public ParsingInterceptor getParsingInterceptor() {
    					return null;
    				}
	    			@Override
	    			public String getComponentProperty() {
	    				return "snack";
	    			}
	    			@Override
	    			public ConfiguredHow getConfiguredHow(String property) {
	    				return ConfiguredHow.ELEMENT;
	    			}
	    			@Override
	    			public ArooaAnnotations getAnnotations() {
	    				return new NoAnnotations();
	    			}
	    		};
    		}
    		if (new SimpleArooaClass(Translations.class).equals(
    				classIdentifier)) {
    			return null;
    		}
    		if (new SimpleArooaClass(InternationalSnack.class).equals(
    				classIdentifier)) {
        		return null;
    		}
    		if (new SimpleArooaClass(Fruit.class).equals(
    				classIdentifier)) {
        		return null;
    		}
    		if (new SimpleArooaClass(ValueType.class).equals(
    				classIdentifier)) {
        		return null;
    		}
    		throw new RuntimeException("Unexpected " + classIdentifier);
    	}

    	@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(
					new OurComponentMappings(),
					new OurValueMappings());
		}
    	
    	@Override
    	public ConversionProvider getConvertletProvider() {
    		return new Conversions();
    	}
    	
    	@Override
    	public ClassResolver getClassResolver() {
    		return new ClassLoaderClassResolver(
    				getClass().getClassLoader());
    	}
    }

    /** 
     * Standing for Translated.
     */
    public static class Translations implements ArooaValue {
    	
    	Map<String, String> translations = new HashMap<String, String>();
    	// this could be something cleaver like using Local.
    	final String lang = "english";
    	
    	public void setTranslations(String lang, String text) {
    		this.translations.put(lang, text);
    	}
    	
    }

    public static class Conversions implements ConversionProvider {
    	public void registerWith(ConversionRegistry registry) {
    		registry.register(Translations.class, Translated.class, 
    				new Convertlet<Translations, Translated>() {
    			public Translated convert(final Translations from) throws ConvertletException {
   					return new Translated() {
   						@Override
   						public String toString() {
   							return from.translations.get(from.lang);   						}
   					};
    			}
    		});
    	}
    }
    
	public void testComponentsAndStandin() throws ArooaParseException {
		RootComponent root = new RootComponent();
		
		StandardArooaParser af = new StandardArooaParser(
				root, new ComponentAndStandinArooaDescriptor());
	    
	    af.parse(new XMLConfiguration("ComponentAndStandin",
	    		"<test>\n" +
	    		" <snack>\n" +
	    		"  <healthy id='x'>\n" +
	    		"   <fruit>" +
	    		"     <is name='Apple'/>" +
	    		"	</fruit>\n" +
	    		"   <description>\n" +
	    		"     <translations>\n" +
	    		"       <translations>\n" +
	    		"         <value key='english' value='Very Nice'/>\n" +
	    		"         <value key='french' value='Tres Bon'/>\n" +
	    		"       </translations>\n" +
	    		"     </translations>\n" +
	    		"   </description>\n" +
	    		"  </healthy>\n" +
	    		" </snack>\n" +
	    		"</test>\n"));
	    
	    ArooaSession session = af.getSession();
	    
	    Object component = session.getBeanRegistry(
	    		).lookup("x");
	    session.getComponentPool().configure(component);
	    
		InternationalSnack result = (InternationalSnack) root.snack;
		assertNotNull("Result", result);
		assertEquals("Description", "Very Nice", result.getDescription().toString());
		assertEquals("Fruit", "Apple", result.getFruit().getName());
	}
	
}
