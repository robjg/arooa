package org.oddjob.arooa.parsing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;

/**
 * Implementation of common {@link ConfigurationNode} functionality.
 * 
 * @author rob
 *
 */
abstract public class AbstractConfigurationNode implements ConfigurationNode {

	private List<ConfigurationNodeListener> listeners = 
		new ArrayList<ConfigurationNodeListener>();
	
	private final LinkedList<ConfigurationNode> children = 
		new LinkedList<ConfigurationNode>();

	private int insertPosition = -1;
	
	public void addNodeListener(ConfigurationNodeListener listener) {
		synchronized (listeners) {
			int index = 0;
			for (ConfigurationNode node: children) {
				listener.childInserted(
						new ConfigurationNodeEvent(this, index++, node));
			}
			listeners.add(listener);
		}
	}

	public void setInsertPosition(int insertAt) {
		this.insertPosition = insertAt;
	}
	
	public void removeNodeListener(ConfigurationNodeListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	synchronized public int insertChild(ConfigurationNode child) {
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
			
			ConfigurationNodeEvent event = new ConfigurationNodeEvent(
					this, insertedAt, child);
			
			for (ConfigurationNodeListener listener : listeners) {
				listener.insertRequest(event);
			}
			
			if (insertPosition < 0) {
				children.add(child);
			} else {
				children.add(insertPosition, child);
			}
			
			for (ConfigurationNodeListener listener : listeners) {
				listener.childInserted(event);
			}
			return insertedAt;
		}
	}
	
	public void removeChild(int index) {
		synchronized (listeners) {
			ConfigurationNode oldChild = children.get(index);
			
			ConfigurationNodeEvent nodeEvent = new ConfigurationNodeEvent(
					this, index, oldChild);
			
			for (ConfigurationNodeListener listener : listeners) {
				listener.removalRequest(nodeEvent);
			}
			
			children.remove(index);
			
			for (ConfigurationNodeListener listener : listeners) {
				listener.childRemoved(nodeEvent);
			}
		}
	}

	public int indexOf(ConfigurationNode child) {
		return children.indexOf(child);
	}
	
	public ConfigurationNode[] children() {
		return children.toArray(new ConfigurationNode[0]);
	}
	
	
	/**
	 * This ConfigurationHandle survives the replacement of this 
	 * ConfigurationNode which is what happens in order to save changes.
	 */
	static protected class ChainingConfigurationHandle implements ConfigurationHandle {

		/** The context for this configuration node or it's replacements. */
		private ArooaContext existingContext;
		
		/** The parent context of this parse. */
		private final ArooaContext parseParent;
		
		private final int index;
		
		public ChainingConfigurationHandle (
				ArooaContext existingContext, 
				ArooaContext parentContext,
				int index) {
			this.existingContext = existingContext;
			this.parseParent = parentContext;
			
			if (index < 0) {
				throw new IllegalStateException("Illegal index " + index);
			}
			
			this.index = index;
		}
		
		public void save() throws ArooaParseException {
			
			ChildCatcher childCatcher = new ChildCatcher(parseParent, index);
			
			CutAndPasteSupport.ReplaceResult replaceResult =
					CutAndPasteSupport.replace(
							existingContext.getParent(), 
							existingContext,
							childCatcher.getChild().getConfigurationNode());
			
			existingContext = replaceResult.getHandle().getDocumentContext();
			
			if (replaceResult.getException() != null) {
				throw replaceResult.getException();
			}
		}
		
		public ArooaContext getDocumentContext() {
			ChildCatcher childCatcher = new ChildCatcher(parseParent, index);
			
			return childCatcher.getChild();
		}
	}

	
}
