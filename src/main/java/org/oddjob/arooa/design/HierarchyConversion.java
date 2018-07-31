/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design;

/**
 * 
 */
public interface HierarchyConversion<T, U> {

	public U convert(T from);
}
