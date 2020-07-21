package org.oddjob.arooa.standard;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.MockConfigurationHandle;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardConfigurationNodeTest extends Assert {

	public static class Apple {
		
		String colour;
		
		public void setColour(String colour) {
			this.colour = colour;
		}
	}
	
	class ParentContext extends MockArooaContext {
		
		AppleContext appleContext = new AppleContext(this);
		
		ConfigurationNode configerationNode = new AbstractConfigurationNode() {

			public void addText(String text) {
				throw new RuntimeException("Unexpected.");
			}

			public ArooaContext getContext() {
				throw new RuntimeException("Unexpected.");
			}

			public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
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

		final ParentContext parent;

		ConfigurationNode configurationNode =
			new MockConfigurationNode() {
			@Override
			public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
					throws ArooaParseException {
				ArooaElement newElement = new ArooaElement("apple");
				newElement = newElement.addAttribute("id", "fruit");
				newElement = newElement.addAttribute("colour", "green");

				ParseHandle<P> handle =
					parentContext.getElementHandler().onStartElement(newElement, parentContext);

				handle.init();

				return new MockConfigurationHandle<P>() {
					@Override
					public P getDocumentContext() {
						return handle.getContext();
					}
				};
			}
			
			@Override
			public ArooaContext getContext() {
				return AppleContext.this;
			}
		};

		AppleContext(ParentContext parent) {
			this.parent = parent;
		}

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

		@Override
		public ArooaContext getParent() {
			return parent;
		}
	}
	
   @Test
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
