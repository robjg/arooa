package org.oddjob.arooa.design.etc;

import org.oddjob.arooa.design.DesignComponent;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

public class UnknownComponent extends UnknownInstance 
implements DesignComponent {

	public UnknownComponent(ArooaElement element,
			ArooaContext parentContext) {
		super(element, parentContext);
	}
	
	public void addStructuralListener(DesignListener listener) {
	}
	
	public void removeStructuralListener(DesignListener listener) {
	}
	
}
