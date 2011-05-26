/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design;

/**
 * 
 */
public interface HierarchyVisitor<T> {

	public void onHierarchy(SimpleHierarchy<T> hierarchy);
	
	public void onLeaf(T leaf);
}
