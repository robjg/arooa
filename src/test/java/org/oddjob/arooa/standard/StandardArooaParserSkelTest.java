package org.oddjob.arooa.standard;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MutableAttributes;

/**
 * Test the {@link StandardArooaParser} end to end using
 * only mocks.
 * 
 * @author rob
 *
 */
public class StandardArooaParserSkelTest extends Assert {

	
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
	
   @Test
	public void testEndToEnd() throws ArooaParseException {
		
		Root root = new Root();
		
		StandardArooaParser parser = 
			new StandardArooaParser(root);
		
		parser.parse(new AConfiguration());
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(root);
		
		assertEquals(root, session.getBeanRegistry().lookup("fruit"));
		
		assertNotNull(root.getApple());
		
		assertEquals("red", root.getApple().getColour());
		
	}
	
}
