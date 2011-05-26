package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ValueType;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ValueIdTest extends TestCase {
	
	public static class Root {
		
		private Object value;
		
		public void setValue(Object value) {
			this.value = value;
		}
	}
	
	public void testSimpleValueId() throws ArooaParseException {
		
		String xml = 
			"<root>" +
			" <value>" +
			"  <value id='val' value='apples'/>" +
			" </value>" +
			"</root>";
	
		Root root  = new Root();
		
		StandardArooaParser parser = new StandardArooaParser(root);

		parser.parse(new XMLConfiguration("XML", xml));
	
		ArooaSession session = parser.getSession();
		
		Object val = session.getBeanRegistry().lookup("val");
		
		assertEquals(ValueType.class, val.getClass());
		
		assertEquals(ArooaObject.class, ((ValueType) val).getValue().getClass());
		assertEquals("apples", ((ValueType) val).getValue().toString());
		
		assertEquals(null, root.value);
		
		session.getComponentPool().configure(root);
		
		assertEquals("apples", root.value);

	}
	
	
}
