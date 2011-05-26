package org.oddjob.arooa.design;

import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;


public class MockDesignComponent extends MockDesignInstance
implements DesignComponent {
	
	public void addStructuralListener(DesignListener listener) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void removeStructuralListener(DesignListener listener) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void deleteChild(DesignComponent child) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void replaceChild(DesignComponent child, DesignComponent replacement) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public Form detail() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public ArooaElement element() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public ArooaContext getArooaContext() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}	
}
