package org.oddjob.arooa.design.model;

import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;

public class MockDesignInstance implements DesignInstance {

	public Form detail() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public ArooaElement element() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public ArooaContext getArooaContext() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	@Override
	public String getId() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	@Override
	public void setId(String id) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
