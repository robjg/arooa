package org.oddjob.arooa.standard;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.MockConfigurationHandle;
import org.oddjob.arooa.parsing.AbstractConfigurationNode;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ArooaHandler;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardConfigurationNodeTest extends TestCase {

	public static class Apple {
		
		String colour;
		
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	class ParentContext extends MockArooaContext {
		
		AppleContext appleContext = new AppleContext();
		
		ConfigurationNode configerationNode = new AbstractConfigurationNode() {

			public void addText(String text) {
				throw new RuntimeException("Unexpected.");
			}

			public ArooaContext getContext() {
				throw new RuntimeException("Unexpected.");
			}

			public ConfigurationHandle parse(ArooaContext parentContext)
					throws ArooaParseException {
				throw new RuntimeException("Unexpected.");
			}
			
		};
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return new SimplePrefixMappings();
		}
		
		@Override
		public ArooaHandler getArooaHandler() {
			return new ArooaHandler() {
				public ArooaContext onStartElement(ArooaElement element,
						ArooaContext parentContext) throws ArooaException {
					return appleContext;
				}
			};
		}
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return configerationNode;
		}
	}
	
	class AppleContext extends MockArooaContext {

		ConfigurationNode configurationNode =
			new MockConfigurationNode() {
			@Override
			public ConfigurationHandle parse(ArooaContext parentContext)
					throws ArooaParseException {
				ArooaElement newElement = new ArooaElement("apple");
				newElement = newElement.addAttribute("id", "fruit");
				newElement = newElement.addAttribute("colour", "green");
				
				final ArooaContext newAppleContext =
					parentContext.getArooaHandler().onStartElement(newElement, parentContext);
				
				parentContext.getConfigurationNode().insertChild(
						newAppleContext.getConfigurationNode());
				
				newAppleContext.getRuntime().init();
				
				return new MockConfigurationHandle() {
					@Override
					public ArooaContext getDocumentContext() {
						return newAppleContext;
					}
				};
			}
			
			@Override
			public ArooaContext getContext() {
				return AppleContext.this;
			}
		};
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return configurationNode;
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void init() throws ArooaException {
				}
			};
		}
	}
	
	public void testSave() throws ArooaParseException {

		Apple root = new Apple();

		StandardArooaParser parser = new StandardArooaParser(root);
		
		parser.parse(
				new XMLConfiguration("TEST", "<apple id='fruit' colour='red'/>"));
				
		ArooaContext ourContext = parser.getSession(
				).getComponentPool().contextFor(root);
		
		ParentContext parentContext = new ParentContext();
		
		ConfigurationHandle handle = ourContext.getConfigurationNode(
				).parse(parentContext);
		
		handle.save();
		
		assertEquals("green", root.colour);
		
		// check another save because saving changes the context.
		handle.save();
		
		assertEquals("green", root.colour);
	}
	
}
