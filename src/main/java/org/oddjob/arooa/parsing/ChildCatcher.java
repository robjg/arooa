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
 *
 */
public class ChildCatcher {

	private ArooaContext child;
	
	public ChildCatcher(ArooaContext parentContext, final int index) {
		
		ConfigurationNodeListener listener = new ConfigurationNodeListener() {
			public void childInserted(
					ConfigurationNodeEvent nodeEvent) {
				if (nodeEvent.getIndex() == index) {
					child = nodeEvent.getChild().getContext();
				}
			}

			public void childRemoved(
					ConfigurationNodeEvent nodeEvent) {
				throw new RuntimeException(
						"Unexpected - this listener should listen long enough!");
			}
			
			public void insertRequest(ConfigurationNodeEvent nodeEvent)
					throws ModificationRefusedException {
			}

			public void removalRequest(ConfigurationNodeEvent nodeEvent)
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
	public ArooaContext getChild() {
		return child;
	}


	public static void watchRootContext(ArooaContext rootContext,
										Consumer<ArooaContext> childContextConsumer) {
		Objects.requireNonNull(childContextConsumer);

		rootContext.getConfigurationNode().addNodeListener(new ConfigurationNodeListener() {
			public void childInserted(ConfigurationNodeEvent nodeEvent) {
				childContextConsumer.accept(nodeEvent.getChild().getContext());
			}
			public void childRemoved(ConfigurationNodeEvent nodeEvent) {
				childContextConsumer.accept(null);
			}
			public void insertRequest(ConfigurationNodeEvent nodeEvent)
					throws ModificationRefusedException {
			}
			public void removalRequest(ConfigurationNodeEvent nodeEvent)
					throws ModificationRefusedException {
			}
		});
	}

}
