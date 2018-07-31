/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



public class SimpleHierarchy<T> {

	private String name;
	private final Class<T> of;
	
	/** Nodes or leaves. */
	private final List<Object> parts = 
		new ArrayList<Object>();
	
	private final Map<String, SimpleHierarchy<T>> nodes = 
		new HashMap<String, SimpleHierarchy<T>>();
	
	public SimpleHierarchy(String name, Class<T> of) {
		this.name = name;
		this.of = of;
	}
	
	public SimpleHierarchy(Class<T> of) {
		this.of = of;
	}

	public int size() {
		return parts.size();
	}
	
	public String getName() {
		return this.name;
	}
	
	public SimpleHierarchy<T> setName(String name) {
		this.name = name;
		return this;
	}
	
	public SimpleHierarchy<T> addLeaf(T child) {
		if (child == null) {
			throw new IllegalArgumentException("Can not add a null child!");
		}
		if (!of.isInstance(child)) {
			throw new IllegalArgumentException(
					"Can not add a child of type " + child.getClass().getName()
					+ " to a hierarchy of type " + of.getName());
		}
		parts.add(child);
		return this;
	}
	
	public SimpleHierarchy<T> addToHierarchy(String named, T child) {
		if (named == null) {
			throw new IllegalArgumentException("Can not add a null branch!");
		}
		SimpleHierarchy<T> node = nodes.get(named);
		
		if (node == null) {
			node = new SimpleHierarchy<T>(named, of);
			nodes.put(named, node);
			parts.add(node);
		}
		node.addLeaf(child);
		return this;
	}
	
	public SimpleHierarchy<T> addHierarchy(SimpleHierarchy<T> hierarchy) {
		if (hierarchy == null) {
			throw new IllegalArgumentException("Can not add a null branch!");
		}
		SimpleHierarchy<T> node = nodes.get(hierarchy.getName());
		if (node != null) {
			throw new IllegalArgumentException("Hierarchy " + hierarchy.getName()
					+ " already exists!");
		}
		nodes.put(hierarchy.getName(), hierarchy);
		parts.add(hierarchy);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public void iterate(HierarchyVisitor<T> v) {
		for (Iterator<Object> it = parts.iterator(); it.hasNext(); ) {
			Object o = it.next();
			if (o instanceof SimpleHierarchy) {
				v.onHierarchy((SimpleHierarchy<T>) o);
			}
			else {
				v.onLeaf( (T) o);
			}
		}
	}
	
	public <U> SimpleHierarchy<U> convert(final HierarchyConversion<T, U> conversion, 
			final Class<U> of) {

		SimpleHierarchy<U> to = new SimpleHierarchy<U>(name, of);
		class Converter implements HierarchyVisitor<T> {
			final SimpleHierarchy<U> current;
			Converter(SimpleHierarchy<U> current) {
				this.current = current;
			}
			
			public void onLeaf(T leaf) {
				U after = conversion.convert(leaf);
				current.addLeaf(after);
			}
			/* (non-Javadoc)
			 * @see org.oddjob.designer.factory.HierarchyVisitor#onGroup(java.lang.String)
			 */
			public void onHierarchy(SimpleHierarchy<T> hierarchy) {
				current.addHierarchy(hierarchy.convert(conversion, of));
			}
		}
		iterate(new Converter(to));
		return to;
	}
}