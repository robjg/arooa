package org.oddjob.arooa.design.screem;

public class NullForm implements Form {	
	
	public String getTitle() {
		throw new IllegalStateException("There is no title!");
	}
	
	
}
