package org.oddjob.arooa.design.designer;

import java.util.EventObject;

public class DesignerModelEvent extends EventObject {
	private static final long serialVersionUID = 2008121600;
	
	public DesignerModelEvent(DesignerModel model) {
		super(model);
	}
	
}
