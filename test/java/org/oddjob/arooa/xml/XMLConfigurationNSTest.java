package org.oddjob.arooa.xml;

import java.net.URI;
import java.net.URISyntaxException;
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
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class XMLConfigurationNSTest extends TestCase {

	class ParentContext extends MockArooaContext {
		
	}
	
	class OurRuntime extends MockRuntimeConfiguration {
			
		@Override
		public void init() throws ArooaException {
		}
	}
	
	class OurContext extends MockArooaContext {
		List<ArooaElement> elements = new ArrayList<ArooaElement>();
		
		PrefixMappings prefixMappings = new SimplePrefixMappings();
		
		XMLConfigurationNode runtimeNode;
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return prefixMappings;
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
				}
			};
		}
		
		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				public ArooaContext onStartElement(
						ArooaElement element, ArooaContext parentContext)
						throws ArooaException {
					elements.add(element);
				
					OurContext newContext = new OurContext();
					newContext.elements = elements;
					newContext.runtimeNode = new XMLConfigurationNode(
							element);
					newContext.runtimeNode.setContext(newContext);
					
					return newContext;
				}
			};
			
		}
	}
	
	public void testParse() throws ArooaParseException, URISyntaxException {
		
		String xml = "<f:fruit xmlns:f='http://www.rgordon.co.uk/oddjob/fruit'>" +
				"<a:apple xmlns:f='http://www.rgordon.co.uk/oddjob/fruit'" +
				"			xmlns:a='http://www.rgordon.co.uk/oddjob/apple'" +
				"         colour='green'/>" +
				"</f:fruit>";
		
		XMLConfiguration test = new XMLConfiguration("TEST", xml);
		
		OurContext context = new OurContext();
		context.runtimeNode = new XMLConfigurationNode(null);
		
		test.parse(context);
		
		assertEquals(2, context.elements.size());
		
		assertEquals("fruit", context.elements.get(0).getTag());
		assertEquals(
				new URI("http://www.rgordon.co.uk/oddjob/fruit"), 
				context.elements.get(0).getUri());
		assertEquals("apple", 
				context.elements.get(1).getTag());
		assertEquals(
				new URI("http://www.rgordon.co.uk/oddjob/apple"),
				context.elements.get(1).getUri());

	}
	
	public void testParseDefaultNS() throws ArooaParseException, URISyntaxException {
		
		String xml = "<fruit xmlns='http://www.rgordon.co.uk/oddjob/fruit'>" +
				"<a:apple xmlns:a='http://www.rgordon.co.uk/oddjob/apple'" +
				"         colour='green'/>" +
				"</fruit>";
		
		XMLConfiguration test = new XMLConfiguration("TEST", xml);
		
		OurContext context = new OurContext();
		context.runtimeNode = new XMLConfigurationNode(null);
		
		test.parse(context);
		
		assertEquals(2, context.elements.size());
		
		assertEquals("fruit", context.elements.get(0).getTag());
		assertEquals(
				new URI("http://www.rgordon.co.uk/oddjob/fruit"), 
				context.elements.get(0).getUri());
		assertEquals("apple", 
				context.elements.get(1).getTag());
		assertEquals(
				new URI("http://www.rgordon.co.uk/oddjob/apple"),
				context.elements.get(1).getUri());
	}
}
