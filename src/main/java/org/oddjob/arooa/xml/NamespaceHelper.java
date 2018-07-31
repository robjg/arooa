package org.oddjob.arooa.xml;

/**
 * Utility functions to help with the namespace.
 */
public class NamespaceHelper {

	/**
	 * extract a uri from a component name
	 *
	 * @param componentName  The stringified form for {uri, name}
	 * @return               The uri or "" if not present
	 */
	public static String extractUriFromComponentName(String componentName) {
		if (componentName == null) {
			return "";
		}
		int index = componentName.lastIndexOf(':');
		if (index == -1) {
			return "";
		}
		return componentName.substring(0, index);
	}
    
	/**
	 * extract the element name from a component name
	 *
	 * @param componentName  The stringified form for {uri, name}
	 * @return               The element name of the component
	 */
	public static String extractNameFromComponentName(String componentName) {
		int index = componentName.lastIndexOf(':');
		if (index == -1) {
			return componentName;
		}
		return componentName.substring(index+1);
	}

}
