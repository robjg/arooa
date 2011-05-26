package org.oddjob.arooa.xml;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.AbstractConfigurationNode;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class XMLConfigurationTest2 extends TestCase {

	class ElementCaptureContext extends MockArooaContext {
		ArooaElement element;
		
		final RuntimeConfiguration runtime = new MockRuntimeConfiguration() {
			@Override
			public void init() throws ArooaException {
			}
		};

		final ConfigurationNode configurationNode = new AbstractConfigurationNode() {
			public void addText(String text) {
				throw new RuntimeException("Unexpected.");
			}
			public ArooaContext getContext() {
				return ElementCaptureContext.this;
			}
			public ConfigurationHandle parse(ArooaContext parentContext)
					throws ArooaParseException {
				throw new RuntimeException("Unexpected.");
			}
			
		};
		
		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				public ArooaContext onStartElement(ArooaElement element,
						ArooaContext parentContext) throws ArooaException {
					ElementCaptureContext nextContext = new ElementCaptureContext();
					nextContext.element = element;
					
					return nextContext;
				}
			};
		}

		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return configurationNode;
		}
	}
	
	public void testQuotesInAttributes() throws ArooaParseException {
		
		String xml = "<a x=\"&quot;apples&quot;\"/>";
		
		XMLConfiguration test = new XMLConfiguration("TEST", xml);
		
		ElementCaptureContext context = new ElementCaptureContext();
		
		ConfigurationHandle handle = test.parse(context);
		
		ElementCaptureContext rootContext = (ElementCaptureContext) handle.getDocumentContext();
		
		assertEquals("\"apples\"", rootContext.element.getAttributes().get("x"));
	}
	
	class TextCaptureContext extends MockArooaContext {

		StringBuilder result = new StringBuilder();
		
		final RuntimeConfiguration runtime = new MockRuntimeConfiguration() {
			@Override
			public void init() throws ArooaException {
			}
		};

		final ConfigurationNode configurationNode = new AbstractConfigurationNode() {
			public void addText(String text) {
				result.append(text);
			}
			public ArooaContext getContext() {
				return TextCaptureContext.this;
			}
			public ConfigurationHandle parse(ArooaContext parentContext)
					throws ArooaParseException {
				throw new RuntimeException("Unexpected.");
			}
			
		};
		
		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				public ArooaContext onStartElement(ArooaElement element,
						ArooaContext parentContext) throws ArooaException {
					TextCaptureContext nextContext = new TextCaptureContext();
					
					return nextContext;
				}
			};
		}

		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return configurationNode;
		}
	}
	
	public void testMultiLineText() throws ArooaParseException {
		
		String EOL = System.getProperty("line.separator");
		
		String text = 
			"This" + EOL +
			"is many" + EOL +
			"lines of" + EOL +
			"text.";
		
		String xml = "<a>" +
				text +
				"</a>";
		
		XMLConfiguration test = new XMLConfiguration("TEST", xml);
		
		TextCaptureContext context = new TextCaptureContext();
		
		ConfigurationHandle handle = test.parse(context);
		
		TextCaptureContext rootContext = (TextCaptureContext) handle.getDocumentContext();

		// This is wrong! Need to work out how to fix!
		String expected = 
			"This\n" +
			"is many\n" +
			"lines of\n" +
			"text.";
		
		assertEquals(expected.length(), rootContext.result.toString().length());
		assertEquals(expected, rootContext.result.toString());
	}
}
