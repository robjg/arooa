package org.oddjob.arooa.standard;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardPropertyHelperSysTest extends Assert {

	public static class MyBean {
		
		private String thing;

		public String getThing() {
			return thing;
		}

		public void setThing(String thing) {
			this.thing = thing;
		}
	}
	
   @Test
	public void testSystemProperty() throws ArooaParseException {
		
		MyBean bean = new MyBean();
		
		StandardArooaParser parser = new StandardArooaParser(bean);
		
		ConfigurationHandle<ArooaContext> handle = parser.parse(new XMLConfiguration("XML",
				"<ignored thing='${java.version}'/>"));
		
		handle.getDocumentContext().getRuntime().configure();
		
		assertEquals(System.getProperty("java.version"), bean.getThing());
	}
	
}
