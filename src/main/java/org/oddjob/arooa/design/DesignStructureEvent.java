package org.oddjob.arooa.design;

import java.io.Serializable;
import java.util.EventObject;

/**
 * This event is fire by an implementer of the Strucutral interface when its
 * strucuture changes.
 * 
 * @author Rob Gordon
 */
public class DesignStructureEvent extends EventObject 
		implements Serializable {
	private static final long serialVersionUID = 20008102800L;
	
	/** The child that has been added or just removed */
	private final DesignInstance child;
	
	/** The position the child was added or removed (starting at 0). */
	private final int index;
	
	/**
	 * Constructor.
	 * 
	 * @param source The source of the event. Generally the parent.
	 * @param child The child object that has been added or removed.
	 * @param index The position where it was added or removed (starting at 0).
	 */
	public DesignStructureEvent(Object source,
			DesignInstance child, int index) {
		
		super(source);
		this.child = child;
		this.index = index;
	}
	
	/**
	 * Get the child.
	 * 
	 * @return The child.
	 */
	public DesignInstance getChild() {
		
		return this.child;
	}
	
	/**
	 * Get the index.
	 * 
	 * @return The index.
	 */
	public int getIndex() {
		
		return this.index;
	}
}
