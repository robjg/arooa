package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Implementation of common {@link ConfigurationNode} functionality.
 *
 * @author rob
 */
abstract public class AbstractConfigurationNode implements ConfigurationNode<ArooaContext> {

    private final List<ConfigurationNodeListener<ArooaContext>> listeners =
            new ArrayList<>();

    private final LinkedList<ConfigurationNode<ArooaContext>> children =
            new LinkedList<>();

    private int insertPosition = -1;

    @Override
    public void addNodeListener(ConfigurationNodeListener<ArooaContext> listener) {
        synchronized (listeners) {
            int index = 0;
            for (ConfigurationNode<ArooaContext> node : children) {
                listener.childInserted(
                        new ConfigurationNodeEvent<>(this, index++, node));
            }
            listeners.add(listener);
        }
    }

    @Override
    public void setInsertPosition(int insertAt) {
        this.insertPosition = insertAt;
    }

    @Override
    public void removeNodeListener(ConfigurationNodeListener<ArooaContext> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    synchronized public int insertChild(ConfigurationNode<ArooaContext> child) {
        if (child == null) {
            throw new NullPointerException("Can not insert null ConfigurationNode");
        }

        // properties may be re-parsed several times, for instance during cut and paste.
        if (children.contains(child)) {
            return -1;
        }

        synchronized (listeners) {
            int insertedAt;

            if (insertPosition < 0) {
                insertedAt = children.size();
            } else {
                insertedAt = insertPosition;
            }

            ConfigurationNodeEvent<ArooaContext> event = new ConfigurationNodeEvent<>(
                    this, insertedAt, child);

            for (ConfigurationNodeListener<ArooaContext> listener : listeners) {
                listener.insertRequest(event);
            }

            if (insertPosition < 0) {
                children.add(child);
            } else {
                children.add(insertPosition, child);
            }

            for (ConfigurationNodeListener<ArooaContext> listener : listeners) {
                listener.childInserted(event);
            }
            return insertedAt;
        }
    }

    @Override
    public void removeChild(int index) {
        synchronized (listeners) {
            ConfigurationNode<ArooaContext> oldChild = children.get(index);

            ConfigurationNodeEvent<ArooaContext> nodeEvent
                    = new ConfigurationNodeEvent<>(this, index, oldChild);

            for (ConfigurationNodeListener<ArooaContext> listener : listeners) {
                listener.removalRequest(nodeEvent);
            }

            children.remove(index);

            for (ConfigurationNodeListener<ArooaContext> listener : listeners) {
                listener.childRemoved(nodeEvent);
            }
        }
    }

    @Override
    public int indexOf(ConfigurationNode<ArooaContext> child) {
        return children.indexOf(child);
    }

    @SuppressWarnings("unchecked")
    public ConfigurationNode<ArooaContext>[] children() {
        return children.toArray(new ConfigurationNode[0]);
    }


    /**
     * This ConfigurationHandle survives the replacement of this
     * ConfigurationNode which is what happens in order to save changes.
     */
    static protected class ChainingConfigurationHandle<P extends ParseContext<P>>
            implements ConfigurationHandle<P> {

        /**
         * The context for this configuration node or it's replacements.
         */
        private ArooaContext existingContext;

        /**
         * The parent context of this parse.
         */
        private final P parseParentContext;

        private final int index;

        /**
         * Constructor.
         *
         * @param existingContext The context of our {@code ConfigurationNode} that is being parsed.
         *                        This can be viewed as the input context.
         * @param parseParentContext The context in which the node is being parsed. This can be viewed
         *                           as the output context;
         * @param index The index of the newly created node with respect to its parent.
         */
        public ChainingConfigurationHandle(
                ArooaContext existingContext,
                P parseParentContext,
                int index) {

            if (index < 0) {
                throw new IllegalStateException("Illegal index " + index);
            }

            this.existingContext = existingContext;
            this.parseParentContext = parseParentContext;
            this.index = index;
        }

        public void save() throws ArooaParseException {

            ConfigurationNode<P> replacementConfiguration =
                    new ChildCatcher<>(parseParentContext, index)
                            .getChild()
                            .getConfigurationNode();

            CutAndPasteSupport.ReplaceResult<ArooaContext> replaceResult =
                    CutAndPasteSupport.replace(
                            existingContext.getParent(),
                            existingContext,
                            replacementConfiguration);

            existingContext = replaceResult.getHandle().getDocumentContext();

            if (replaceResult.getException() != null) {
                throw replaceResult.getException();
            }
        }

        public P getDocumentContext() {
            ChildCatcher<P> childCatcher = new ChildCatcher<>(parseParentContext, index);

            return childCatcher.getChild();
        }
    }
}
