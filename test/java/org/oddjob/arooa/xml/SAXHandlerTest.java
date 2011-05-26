package org.oddjob.arooa.xml;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.RootContext;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class SAXHandlerTest extends TestCase {

	boolean init;
	
	class OurDocumentContext extends MockArooaContext {
	
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void init() throws ArooaException {
					init = true;
				}
			};
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				@Override
				public ArooaContext getContext() {
					return OurDocumentContext.this;
				}
			};
		}
	}
		
	public void testDocumentContext() throws SAXException {

		RootContext rootContext = new RootContext(ArooaType.COMPONENT, 
				null,
				new ArooaHandler() {

			public ArooaContext onStartElement(ArooaElement element,
					ArooaContext parentContext) throws ArooaException {
				return new OurDocumentContext();
			}
		});
		
		SAXHandler test = new SAXHandler(rootContext);

		test.startDocument();
		
		test.startElement("", "fruit", "fruit", new AttributesImpl());
		
		test.endElement("", "fruit", "fruit");
		
		test.endDocument();
		
		ConfigurationNode result = test.getDocumentContext().getConfigurationNode();
		
		assertNotNull(result);
		
		assertTrue(init);
	}
	
}
