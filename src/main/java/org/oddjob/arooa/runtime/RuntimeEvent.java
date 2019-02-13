package org.oddjob.arooa.runtime;

import java.util.EventObject;


/**
 * An event passed to a {@link RuntimeListener}.
 */
public class RuntimeEvent extends EventObject {
	private static final long serialVersionUID = 20080121;
	
	public RuntimeEvent(RuntimeConfiguration source) {
		super(source);
	}
	
	@Override
	public RuntimeConfiguration getSource() {
		return (RuntimeConfiguration) super.getSource();
	}
	
}
