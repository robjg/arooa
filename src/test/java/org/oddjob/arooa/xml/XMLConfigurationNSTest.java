package org.oddjob.arooa.xml;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class XMLConfigurationNSTest extends Assert {

	class ParentContext extends MockArooaContext {
		
	}
	
	class OurRuntime extends MockRuntimeConfiguration {
			
		@Override
		public void init() throws ArooaException {
		}
	}
	
	class OurContext extends MockArooaContext {

		private final OurContext parent;

		List<ArooaElement> elements = new ArrayList<ArooaElement>();
		
		PrefixMappings prefixMappings = new SimplePrefixMappings();
		
		XMLConfigurationNode runtimeNode;

		OurContext() {
			this(null);
		}

		OurContext(OurContext parent) {
			this.parent = parent;
		}

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
				
					OurContext newContext = new OurContext(OurContext.this);
					newContext.elements = elements;
					newContext.runtimeNode = new XMLConfigurationNode(
							element);
					newContext.runtimeNode.setContext(newContext);
					
					return newContext;
				}
			};
		}

		@Override
		public ArooaContext getParent() {
			return parent;
		}
	}
	
   @Test
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
	
   @Test
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
