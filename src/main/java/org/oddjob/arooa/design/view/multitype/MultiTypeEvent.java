package org.oddjob.arooa.design.view.multitype;

import java.util.EventObject;

/**
 * An event for a {@link MultiTypeListener}.
 * 
 * @author rob
 *
 */
public class MultiTypeEvent extends EventObject {
	private static final long serialVersionUID = 2012042600L;
	
	private final int row;
	
	/**
	 * Constructor.
	 * 
	 * @param source The source.
	 * @param row The row number for the event.
	 */
	public MultiTypeEvent(MultiTypeModel source, int row) {
		super(source);
		this.row = row;
	}

	/**
	 * The row number for the event.
	 * 
	 * @return The row number.
	 */
	public int getRow() {
		return row;
	}
	
	@Override
	public MultiTypeModel getSource() {
		return (MultiTypeModel) super.getSource();
	}
}
