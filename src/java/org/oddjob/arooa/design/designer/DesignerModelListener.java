package org.oddjob.arooa.design.designer;

import java.util.EventListener;

public interface DesignerModelListener extends EventListener {

	public void selectionChanged(DesignerModelEvent event);
}
