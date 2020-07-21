package org.oddjob.arooa.types;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.registry.InvalidIdException;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class IdentifiableValueTypeTest extends Assert {

	public static class Root {
		
		Object thing;

		public Object getThing() {
			return thing;
		}

		public void setThing(Object value) {
			this.thing = value;
		}
	}
	
	public static class MyBean {
		
	}
		
   @Test
	public void testIdentifyNonArooaValue() throws ArooaParseException, InvalidIdException {
		
		Root root = new Root();
		
		String xml = "<whatever>" +
				" <thing>" +
				"  <identify id='my-bean'>" +
				"   <value>" +
				"   <bean class='" + MyBean.class.getName() + "'/>" +
				"   </value>" +
				"  </identify>" +
				" </thing>" +
				"</whatever>";
		
		StandardArooaParser parser = new StandardArooaParser(root);

		ConfigurationHandle<ArooaContext> handle = parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		Object registered = session.getBeanRegistry().lookup("my-bean");
		
		assertEquals(null, registered);
		
		session.getComponentPool().configure(root);
				
		registered = session.getBeanRegistry().lookup("my-bean");
		
		assertEquals(MyBean.class, registered.getClass());
		
		assertEquals(registered, root.thing); 
		
		handle.getDocumentContext().getRuntime().destroy();
		
		registered = session.getBeanRegistry().lookup("my-bean");
		
		assertEquals(null, registered);
	}	
	
   @Test
	public void testIdentifyArooaValue() throws ArooaParseException, InvalidIdException, ArooaPropertyException, ArooaConversionException {
		
		Root root = new Root();
		
		String xml = "<whatever>" +
				" <thing>" +
				"  <identify id='my-bean'>" +
				"   <value>" +
				"   <value value='Apples'/>" +
				"   </value>" +
				"  </identify>" +
				" </thing>" +
				"</whatever>";
		
		StandardArooaParser parser = new StandardArooaParser(root);

		ConfigurationHandle<ArooaContext> handle = parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(root);
				
		Object registered = session.getBeanRegistry().lookup("my-bean");
		
		assertEquals(ValueType.class, registered.getClass());
		
		assertEquals("Apples", root.thing); 
		
		String string = session.getBeanRegistry().lookup("my-bean", String.class);
		
		assertEquals("Apples", string);
		
		handle.getDocumentContext().getRuntime().destroy();
		
		registered = session.getBeanRegistry().lookup("my-bean");
		
		assertEquals(null, registered);
	}	
}
