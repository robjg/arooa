package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

/**
 * Test the {@link StandardArooaParser} end to end using
 * only mocks.
 * 
 * @author rob
 *
 */
public class StandardArooaParserSkelTest extends TestCase {

	private class ADescriptor extends MockArooaDescriptor {
		
		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(ArooaClass forClass,
				PropertyAccessor accessor) {
			if (new SimpleArooaClass(Apple.class).equals(
					forClass)) {
				return new MockArooaBeanDescriptor() {
					@Override
					public String getComponentProperty() {
						return null;
					}
					@Override
					public ConfiguredHow getConfiguredHow(String property) {
						return ConfiguredHow.ATTRIBUTE;
					}
					@Override
					public ParsingInterceptor getParsingInterceptor() {
						return null;
					}
					@Override
					public boolean isAuto(String property) {
						return false;
					}
				};
			}
			if (new SimpleArooaClass(Root.class).equals(forClass)) {
				return null;
			}
			throw new RuntimeException("Unexpected: " + forClass);
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(null, new DefaultValuesMappings());
		}
		
    	@Override
    	public ClassResolver getClassResolver() {
    		return new ClassLoaderClassResolver(
    				getClass().getClassLoader());
    	}
	}
	
	private class AConfiguration implements ArooaConfiguration {
				
		// Simulates
		// 
		// <test id='fruit'>
		//	 <apple>
		//     <is colour='red'/>
		//   </apple>
		// </test>
		
		public ConfigurationHandle parse(final ArooaContext context) throws ArooaParseException {

			MutableAttributes rootAttrs = new MutableAttributes();
			rootAttrs.set("id", "fruit");
			
			ArooaElement element = new ArooaElement(
					"test", rootAttrs);
			
			ArooaContext rootContext = context.getArooaHandler(
					).onStartElement(element, context);

			
			ArooaElement element2 = new ArooaElement(
					"apple");
			
			ArooaContext appleContext = rootContext.getArooaHandler(
					).onStartElement(element2, rootContext);
	
			MutableAttributes attrs = new MutableAttributes();
			attrs.set("colour", "red");
			
			ArooaElement element3 = new ArooaElement(
					"is", attrs);
	
			ArooaContext  isContext = appleContext.getArooaHandler(
				).onStartElement(element3, appleContext);
					
			isContext.getRuntime().init();
			
			appleContext.getRuntime().init();

			rootContext.getRuntime().init();
			
			return null;
		}
	}

	public static class Apple {
		private String colour;

		public String getColour() {
			return colour;
		}

		public void setColour(String colour) {
			this.colour = colour;
		}
				
		
	}
	
	public static class Root {
		private Apple apple;

		public Apple getApple() {
			return apple;
		}

		public void setApple(Apple apple) {
			this.apple = apple;
		}
	}
	
	public void testEndToEnd() throws ArooaParseException {
		
		Root root = new Root();
		
		StandardArooaParser parser = 
			new StandardArooaParser(root, new ADescriptor());
		
		parser.parse(new AConfiguration());
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(root);
		
		assertEquals(root, session.getBeanRegistry().lookup("fruit"));
		
		assertNotNull(root.getApple());
		
		assertEquals("red", root.getApple().getColour());
		
	}
	
}
