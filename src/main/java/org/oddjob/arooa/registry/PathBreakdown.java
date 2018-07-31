/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.registry;


/**
 * Provides a full path lookup which includes nested/indexed/mapped
 * property access.
 */
public class PathBreakdown {

	public static final String PROPERTY_DELIM = ".";
	
	private final String nestedPath;
	private final String id;
	private final String property;
	
	public PathBreakdown(String fullPath) {
		int delimIndex = fullPath.indexOf(PROPERTY_DELIM);
	
		String beanPath;
		if ( delimIndex < 0) {
			beanPath = fullPath;
			property = null;
		}
		else {
			beanPath = fullPath.substring(0, delimIndex);
			property = fullPath.substring(delimIndex + 1);
		}

		Path path = new Path(beanPath);
		id = path.getRoot();
		Path childPath = path.getChildPath();
		
		if (childPath == null || childPath.size() == 0) {
			nestedPath = null;
		} 
		else {
			if (property == null) {
				nestedPath = childPath.toString();
			}
			else {
				nestedPath = childPath.toString() + PROPERTY_DELIM + property; 
			}
		}
	}
	
	public boolean isNested() {
		return nestedPath != null;
	}
	
	public String getId() {
		return id;
	}
	
	public String getNestedPath() {
		return nestedPath;
	}

	public boolean isProperty() {
		return property != null;
	}
	
	public String getProperty() {
		return property;
	}
}
