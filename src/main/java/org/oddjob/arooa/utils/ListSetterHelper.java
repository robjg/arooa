package org.oddjob.arooa.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Helps with Setting a list. Tries to deal with nulls and throws a
 * more helpful exception than the otherwise thrown 
 * {@link IndexOutOfBoundsException}.
 * 
 * @author rob
 *
 */
public class ListSetterHelper<E> {

	private final List<E> elements;

	public ListSetterHelper() {
		this(new LinkedList<>());
	}

	public ListSetterHelper(List<E> list) {
		this.elements = list;
	}
	
	public void set(int index, E element) {
		if (index < elements.size()) {
			if (element == null) {
				elements.remove(index);
			}
			else {
				elements.set(index, element);
			}
		}
		else if (index == elements.size()) {
	    	elements.add(element);
		}
		else {
			throw new IllegalArgumentException("Index " +
					index + " would leave gaps which isn't allowed.");
		}		
	}

	public List<E> getList() {
		return this.elements;
	}
}
