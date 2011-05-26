package org.oddjob.arooa.xml;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class XMLConfigurationTest extends TestCase {

	
	class MyHandler implements ArooaHandler {
		
		int i;
		int elementCount;
		String[] elements = new String[] {"a", "b", "c" };
		
		public ArooaContext onStartElement(
				ArooaElement element, ArooaContext context) throws ArooaException {
			
			assertEquals(elements[i], element.getTag());
			++i;
			++elementCount;

			XMLConfigurationNode runtimeNode = new XMLConfigurationNode(
					element);
			
			ArooaContext newContext = new TestParseContext(this, runtimeNode);
			runtimeNode.setContext(newContext);
			
			return newContext;
		}
		
	}
	
	
	class TestParseContext extends MockArooaContext {
		final MyHandler handler;
		final ConfigurationNode runtimeNode;
		
		TestParseContext(MyHandler handler, ConfigurationNode runtimeNode) {
			this.handler = handler;
			this.runtimeNode = runtimeNode;
		}
		
		@Override
		public ArooaHandler getArooaHandler() {
			return handler;
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return new SimplePrefixMappings();
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return runtimeNode;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void init() throws ArooaException {
					--handler.i;
				}
			};
		}
	}
	
	public void testParse() throws ArooaParseException {
		
		String xml = "<a><b x='10'><c>Test</c></b></a>";
		
		XMLConfiguration test = new XMLConfiguration("Test", xml);
		
		MyHandler myHandler = new MyHandler();
		
		TestParseContext testContext = 
			new TestParseContext(myHandler, new XMLConfigurationNode(null));
		
		test.parse(testContext);
		
		assertEquals(3, testContext.handler.elementCount);
		assertEquals(0, testContext.handler.i);
	}
	
	class RootContext extends MockArooaContext {

		List<String> results = new ArrayList<String>();
		
		ConfigurationNode rootNode = new XMLConfigurationNode(null);
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return rootNode;
		}
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return new SimplePrefixMappings();
		}
		
		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				public ArooaContext  onStartElement(ArooaElement element,
						ArooaContext parentContext) {
					
					results.add("onStartElement: "  + element.getTag());

					RootContext newContext = new RootContext();
					newContext.results = results;
					
					return newContext;
				}
			};
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new ResultsRuntime(this);
		}
	}
	
	class ResultsRuntime extends MockRuntimeConfiguration {

		RootContext context;
		
		ResultsRuntime(RootContext context) {
			this.context = context;
		}
		
		@Override
		public ArooaClass getClassIdentifier() {
			return null;
		}
		
		@Override
		public void init() throws ArooaException {

			String text = ((XMLConfigurationNode) context.getConfigurationNode()).getText();
			if (text != null) {
				context.results.add("characters: " + text);
			}
			
			context.results.add("onEndElement: ");
		}
	}

	
	public void testFullParse() throws ArooaParseException {
		XMLConfiguration test = new XMLConfiguration("Test",
				"<a>x<b/>y</a>");

		RootContext context = new RootContext();
		
		test.parse(context);
		
		assertEquals(5, context.results.size());
		
		assertEquals("onStartElement: a", context.results.get(0));
		assertEquals("onStartElement: b", context.results.get(1));
		assertEquals("onEndElement: ", context.results.get(2));
		assertEquals("characters: xy", context.results.get(3));
		assertEquals("onEndElement: ", context.results.get(4));
	}

	public void testEmptyParse() throws ArooaParseException {
		
		XMLConfiguration test = new XMLConfiguration("TEST", "");
		
		RootContext context = new RootContext();
		
		try {
			test.parse(context);
			fail("Premature EOF expected.");
		} catch (ArooaParseException e) {
			// expected.
		}
		
	}
}
