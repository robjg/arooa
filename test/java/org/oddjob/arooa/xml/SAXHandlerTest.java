package org.oddjob.arooa.xml;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class SAXHandlerTest extends Assert {

	private static final Logger logger = LoggerFactory.getLogger(SAXHandlerTest.class);
	
	@Rule public TestName name = new TestName();

	public String getName() {
        return name.getMethodName();
    }

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
	
    @Before
    public void setUp() throws Exception {

		
		logger.info("-------------------  " + getName() + "  --------------------");
	}
	
   @Test
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
