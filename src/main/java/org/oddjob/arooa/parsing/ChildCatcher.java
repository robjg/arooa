package org.oddjob.arooa.parsing;

import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.ModificationRefusedException;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Capture the child context of a current context;
 *
 * @author rob
 */
public class ChildCatcher<P extends ParseContext<P>> {

    private P child;

    public ChildCatcher(P parentContext, final int index) {

        ConfigurationNodeListener<P> listener = new ConfigurationNodeListener<P>() {
            public void childInserted(
                    ConfigurationNodeEvent<P> nodeEvent) {
                if (nodeEvent.getIndex() == index) {
                    child = nodeEvent.getChild().getContext();
                }
            }

            public void childRemoved(
                    ConfigurationNodeEvent<P> nodeEvent) {
                throw new RuntimeException(
                        "Unexpected - this listener should listen long enough!");
            }

            public void insertRequest(ConfigurationNodeEvent<P> nodeEvent)
                    throws ModificationRefusedException {
            }

            public void removalRequest(ConfigurationNodeEvent<P> nodeEvent)
                    throws ModificationRefusedException {
            }
        };

        parentContext.getConfigurationNode().addNodeListener(listener);
        parentContext.getConfigurationNode().removeNodeListener(listener);
    }

    /**
     * The child context.
     *
     * @return The context. Null if no child exists.
     */
    public P getChild() {
        return child;
    }


    public static <P extends ParseContext<P>> void watchRootContext(P rootContext,
                                                                    Consumer<P> childContextConsumer) {
        Objects.requireNonNull(childContextConsumer);

        rootContext.getConfigurationNode().addNodeListener(
                new ConfigurationNodeListener<P>() {
                    @Override
                    public void childInserted(ConfigurationNodeEvent<P> nodeEvent) {
                        childContextConsumer.accept(nodeEvent.getChild().getContext());
                    }

                    @Override
                    public void childRemoved(ConfigurationNodeEvent<P> nodeEvent) {
                        childContextConsumer.accept(null);
                    }

                    @Override
                    public void insertRequest(ConfigurationNodeEvent<P> nodeEvent)
                            throws ModificationRefusedException {
                    }

                    @Override
                    public void removalRequest(ConfigurationNodeEvent<P> nodeEvent)
                            throws ModificationRefusedException {
                    }
                });
    }

}
