package org.oddjob.arooa.parsing;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.parsing.AbstractConfigurationNode.ChainingConfigurationHandle;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.MockComponentPool;
import org.oddjob.arooa.runtime.*;

public class AbstractConfigurationNodeTest extends Assert {

    static class ExpectedException extends RuntimeException {
        private static final long serialVersionUID = 2009012800L;

    }

    static class ChildConfiguration extends MockConfigurationNode {
        ArooaContext newChild = new MockArooaContext() {
            @Override
            public ArooaContext getParent() {
                throw new ExpectedException();
            }
        };

        @Override
        public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
                throws ArooaParseException {

            return new MockConfigurationHandle<P>() {
                @Override
                @SuppressWarnings("unchecked")
                public P getDocumentContext() {
                    return (P) newChild;
                }
            };
        }
    }

    static class ParseParentContext extends MockArooaContext {

        ChildConfiguration childConfig = new ChildConfiguration();

        ArooaContext child = new MockArooaContext() {
            @Override
            public ConfigurationNode<ArooaContext> getConfigurationNode() {
                return childConfig;
            }
        };

        boolean listenerRemoved;


        @Override
        public ConfigurationNode<ArooaContext> getConfigurationNode() {
            return new MockConfigurationNode() {
                @Override
                public void addNodeListener(ConfigurationNodeListener<ArooaContext> listener) {
                    listener.childInserted(new ConfigurationNodeEvent<>(
                            this, 2, new MockConfigurationNode() {
                        @Override
                        public ArooaContext getContext() {
                            return child;
                        }
                    }));
                }

                public void removeNodeListener(ConfigurationNodeListener<ArooaContext> listener) {
                    listenerRemoved = true;
                }

                @Override
                public int indexOf(ConfigurationNode<?> child) {
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

        ChainingConfigurationHandle<ArooaContext, ArooaContext> test = new ChainingConfigurationHandle<>(
                new MockArooaContext(), parseParent, index);

        ArooaContext documentContext = test.getDocumentContext();

        assertEquals(parseParent.child, documentContext);
        assertTrue(parseParent.listenerRemoved);
    }

    static class ExistingParent extends MockArooaContext {

        int index;

        @Override
        public ConfigurationNode<ArooaContext> getConfigurationNode() {
            return new MockConfigurationNode() {
                @Override
                public void setInsertPosition(int insertAt) {
                    index = insertAt;
                }

                @Override
                public int insertChild(ConfigurationNode<ArooaContext> child) {
                    return -1; // shouldn't be used.
                }

                @Override
                public int indexOf(ConfigurationNode<?> child) {
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

		@Override
		public PrefixMappings getPrefixMappings() {
			return new SimplePrefixMappings();
		}
    }

    static class ExistingConfigNode extends MockConfigurationNode {

        @Override
        public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
                throws ArooaParseException {
            // roll back handle - won't be used.
            return new MockConfigurationHandle<>();
        }
    }

    static class ExistingContext extends MockArooaContext {
        boolean runtimeDestroyed;

        ExistingParent parent = new ExistingParent();

        @Override
        public ArooaContext getParent() {
            return parent;
        }

        @Override
        public ConfigurationNode<ArooaContext> getConfigurationNode() {
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

        ChainingConfigurationHandle<ArooaContext, ArooaContext> test
                = new ChainingConfigurationHandle<>(
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
