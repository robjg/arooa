package org.oddjob.arooa.parsing;


/**
 * This class appears to be redundant.
 * 
 * @author rob
 *
 */
public class ContextDestroyer {

	public void destroy(ArooaContext context) {
		
		context.getRuntime().destroy();
		
		ArooaContext parent = context.getParent();
		
		if (parent == null) {
			
			// Must be a document context.
			return;
		}
		
		int index = parent.getConfigurationNode().indexOf(
				context.getConfigurationNode());
		
		if (index < 0) {
			throw new IllegalStateException(
					"Attempting to cut a configuration node that is not a child of it's parent.");
		}
		
		parent.getConfigurationNode().removeChild(
				index);
	}
}
