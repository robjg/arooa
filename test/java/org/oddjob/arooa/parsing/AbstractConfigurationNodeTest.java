package org.oddjob.arooa.parsing;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.MockArooaSession;
import org.oddjob.arooa.MockConfigurationHandle;
import org.oddjob.arooa.parsing.AbstractConfigurationNode.ChainingConfigurationHandle;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.MockComponentPool;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;

public class AbstractConfigurationNodeTest extends Assert {

	class ExpectedException extends RuntimeException {
		private static final long serialVersionUID = 2009012800L;
		
	}
	
	class ChildConfiguration extends MockConfigurationNode {
		ArooaContext newChild = new MockArooaContext() {
			@Override
			public ArooaContext getParent() {
				throw new ExpectedException();
			}
		};
		
		@Override
		public ConfigurationHandle parse(ArooaContext parentContext)
				throws ArooaParseException {
			
			return new MockConfigurationHandle() {
				@Override
				public ArooaContext getDocumentContext() {
					return newChild;
				}
			};
		}
	}
	
	class ParseParentContext extends MockArooaContext {
		
		ChildConfiguration childConfig = new ChildConfiguration();
		
		ArooaContext child = new MockArooaContext() {
			@Override
			public ConfigurationNode getConfigurationNode() {
				return childConfig;
			}
		};
		
		boolean listenerRemoved;
		
		
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				@Override
				public void addNodeListener(ConfigurationNodeListener listener) {
					listener.childInserted(new ConfigurationNodeEvent(
							this, 2, new MockConfigurationNode() {
								@Override
								public ArooaContext getContext() {
									return child;
								}
							}));
				}
				
				public void removeNodeListener(ConfigurationNodeListener listener) {
					listenerRemoved = true;
				}
				
				@Override
				public int indexOf(ConfigurationNode child) {
					return 2;
				}
			};
		}
	}
			
   @Test
	public void testChainingConfigurationHandleDoc() {
		
		ParseParentContext parseParent = new ParseParentContext();
		
		int index = parseParent.getConfigurationNode().indexOf(
				parseParent.child.getConfigurationNode());
		
		ChainingConfigurationHandle test = new ChainingConfigurationHandle(
				new MockArooaContext(), parseParent, index);
		
		ArooaContext documentContext = test.getDocumentContext();

		assertEquals(parseParent.child, documentContext);
		assertTrue(parseParent.listenerRemoved);
	}
	
	class ExistingParent extends MockArooaContext {

		int index;
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new MockConfigurationNode() {
				@Override
				public void setInsertPosition(int insertAt) {
					index = insertAt;
				}
				@Override
				public int  insertChild(ConfigurationNode child) {
					return -1; // shouldn't be used.
				}
				@Override
				public int indexOf(ConfigurationNode child) {
					assertTrue(child instanceof ExistingConfigNode);
					return 2;
				}
				@Override
				public void removeChild(int index) {
					assertEquals(index, 2);
				}
			};
		}
		
		@Override
		public ArooaSession getSession() {
			return new MockArooaSession() {
				@Override
				public ComponentPool getComponentPool() {
					return new MockComponentPool() {
					};
				}
			};
		}
	}
	
	class ExistingConfigNode extends MockConfigurationNode {
		
		@Override
		public ConfigurationHandle parse(ArooaContext parentContext)
				throws ArooaParseException {
			// roll back handle - won't be used.
			return new MockConfigurationHandle();
		}
	}
	
	class ExistingContext extends MockArooaContext { 
		boolean runtimeDestroyed; 
		
		ExistingParent parent = new ExistingParent();
		
		@Override
		public ArooaContext getParent() {
			return parent;
		}	
		
		@Override
		public ConfigurationNode getConfigurationNode() {
			return new ExistingConfigNode();
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void destroy() {
					runtimeDestroyed = true;
				}
			};
		}
	}

	
   @Test
	public void testChainingConfigurationHandleSave() throws ArooaParseException {
		
		ParseParentContext parseParent = new ParseParentContext();
	
		final ExistingContext existing = new ExistingContext();
		
		int index = parseParent.getConfigurationNode().indexOf(
				parseParent.child.getConfigurationNode());
		
		ChainingConfigurationHandle test = new ChainingConfigurationHandle(
				existing,
				parseParent, 
				index);
		
		test.save();

		assertEquals(2, existing.parent.index);
		assertTrue(existing.runtimeDestroyed);
		
		try {
			test.save();
			fail("Existing Context should be replaced with one that throws exception.");
		} catch (ExpectedException e) {
			// Expected.
		}
	}
}
