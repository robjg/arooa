package org.oddjob.arooa.xml;

import junit.framework.TestCase;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class XMLConfigurationX2Test extends TestCase {

	class ElementCaptureContext extends MockArooaContext {

		final ArooaContext parent;

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
			public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
					throws ArooaParseException {
				throw new RuntimeException("Unexpected.");
			}
			
		};

		ElementCaptureContext(ArooaContext parent) {
			this.parent = parent;
		}

		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				public ArooaContext onStartElement(ArooaElement element,
						ArooaContext parentContext) throws ArooaException {
					ElementCaptureContext nextContext = new ElementCaptureContext(ElementCaptureContext.this);
					nextContext.element = element;
					
					return nextContext;
				}
			};
		}

		@Override
		public ArooaContext getParent() {
			return parent;
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
		
		ElementCaptureContext context = new ElementCaptureContext(null);
		
		ConfigurationHandle handle = test.parse(context);
		
		ElementCaptureContext rootContext = (ElementCaptureContext) handle.getDocumentContext();
		
		assertEquals("\"apples\"", rootContext.element.getAttributes().get("x"));
	}
	
	class TextCaptureContext extends MockArooaContext {

		final ArooaContext parent;

		StringBuilder result = new StringBuilder();
		
		final RuntimeConfiguration runtime = new MockRuntimeConfiguration() {
			@Override
			public void init() throws ArooaException {
			}
		};

		final ConfigurationNode configurationNode = new AbstractConfigurationNode() {
			@Override
			public void addText(String text) {
				result.append(text);
			}
			@Override
			public ArooaContext getContext() {
				return TextCaptureContext.this;
			}
			@Override
			public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
					throws ArooaParseException {
				throw new RuntimeException("Unexpected.");
			}
			
		};

		TextCaptureContext(ArooaContext parent) {
			this.parent = parent;
		}

		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				@Override
				public ArooaContext onStartElement(ArooaElement element,
						ArooaContext parentContext) throws ArooaException {
					TextCaptureContext nextContext = new TextCaptureContext(TextCaptureContext.this);
					
					return nextContext;
				}
			};
		}

		@Override
		public ArooaContext getParent() {
			return parent;
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
		
		TextCaptureContext context = new TextCaptureContext(null);
		
		ConfigurationHandle<ArooaContext> handle = test.parse(context);
		
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
