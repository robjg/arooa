package org.oddjob.arooa.design.model;

import org.oddjob.arooa.design.DesignElementProperty;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.parsing.ArooaContext;

public class MockDesignElementProperty implements DesignElementProperty {

	public boolean isPopulated() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public String property() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public FormItem view() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public ArooaContext getArooaContext() {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}

	public void addDesignListener(DesignListener listener) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
	
	public void removeDesignListener(DesignListener listener) {
		throw new RuntimeException("Unexpected from class: " + 
				this.getClass().getName());
	}
}
