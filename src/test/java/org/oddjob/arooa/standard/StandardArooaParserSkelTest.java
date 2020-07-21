package org.oddjob.arooa.standard;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.parsing.ParseContext;
import org.oddjob.arooa.parsing.ParseHandle;

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
		
		public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext) throws ArooaParseException {

			MutableAttributes rootAttrs = new MutableAttributes();
			rootAttrs.set("id", "fruit");
			
			ArooaElement element = new ArooaElement(
					"test", rootAttrs);

			ParseHandle<P> rootHandle = parentContext.getElementHandler()
					.onStartElement(element, parentContext);

			P rootContext = rootHandle.getContext();

			ArooaElement element2 = new ArooaElement(
					"apple");

			ParseHandle<P> appleHandle = rootContext.getElementHandler(
					).onStartElement(element2, rootContext);

			P appleContext = appleHandle.getContext();

			MutableAttributes attrs = new MutableAttributes();
			attrs.set("colour", "red");
			
			ArooaElement element3 = new ArooaElement(
					"is", attrs);

			ParseHandle<P> isHandle =
			 appleContext.getElementHandler(
				).onStartElement(element3, appleContext);

			isHandle.init();
			
			appleHandle.init();

			rootHandle.init();
			
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
