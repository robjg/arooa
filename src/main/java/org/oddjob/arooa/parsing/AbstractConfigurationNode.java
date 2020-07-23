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
abstract public class AbstractConfigurationNode<P extends ParseContext<P>> implements ConfigurationNode<P> {

    private final List<ConfigurationNodeListener<P>> listeners =
            new ArrayList<>();

    private final LinkedList<ConfigurationNode<P>> children =
            new LinkedList<>();

    private int insertPosition = -1;

    @Override
    public void addNodeListener(ConfigurationNodeListener<P> listener) {
        synchronized (listeners) {
            int index = 0;
            for (ConfigurationNode<P> node : children) {
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
    public void removeNodeListener(ConfigurationNodeListener<P> listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    synchronized public int insertChild(ConfigurationNode<P> child) {
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

            ConfigurationNodeEvent<P> event = new ConfigurationNodeEvent<>(
                    this, insertedAt, child);

            for (ConfigurationNodeListener<P> listener : listeners) {
                listener.insertRequest(event);
            }

            if (insertPosition < 0) {
                children.add(child);
            } else {
                children.add(insertPosition, child);
            }

            for (ConfigurationNodeListener<P> listener : listeners) {
                listener.childInserted(event);
            }
            return insertedAt;
        }
    }

    @Override
    public void removeChild(int index) {
        synchronized (listeners) {
            ConfigurationNode<P> oldChild = children.get(index);

            ConfigurationNodeEvent<P> nodeEvent
                    = new ConfigurationNodeEvent<>(this, index, oldChild);

            for (ConfigurationNodeListener<P> listener : listeners) {
                listener.removalRequest(nodeEvent);
            }

            children.remove(index);

            for (ConfigurationNodeListener<P> listener : listeners) {
                listener.childRemoved(nodeEvent);
            }
        }
    }

    @Override
    public int indexOf(ConfigurationNode<?> child) {
        return children.indexOf(child);
    }

    @SuppressWarnings("unchecked")
    public ConfigurationNode<P>[] children() {
        return children.toArray(new ConfigurationNode[0]);
    }


    /**
     * This ConfigurationHandle survives the replacement of this
     * ConfigurationNode which is what happens in order to save changes.
     */
    static protected class ChainingConfigurationHandle<P extends ParseContext<P>, Q extends ParseContext<Q>>
            implements ConfigurationHandle<Q> {

        /**
         * The context for this configuration node or it's replacements.
         */
        private P existingContext;

        /**
         * The parent context of this parse.
         */
        private final Q parseParentContext;

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
                P existingContext,
                Q parseParentContext,
                int index) {

            if (index < 0) {
                throw new IllegalStateException("Illegal index " + index);
            }

            this.existingContext = existingContext;
            this.parseParentContext = parseParentContext;
            this.index = index;
        }

        public void save() throws ArooaParseException {

            ConfigurationNode<Q> replacementConfiguration =
                    new ChildCatcher<>(parseParentContext, index)
                            .getChild()
                            .getConfigurationNode();

            CutAndPasteSupport.ReplaceResult<P> replaceResult =
                    CutAndPasteSupport.replace(
                            existingContext.getParent(),
                            existingContext,
                            replacementConfiguration);

            existingContext = replaceResult.getHandle().getDocumentContext();

            if (replaceResult.getException() != null) {
                throw replaceResult.getException();
            }
        }

        public Q getDocumentContext() {
            ChildCatcher<Q> childCatcher = new ChildCatcher<>(parseParentContext, index);

            return childCatcher.getChild();
        }
    }
}
