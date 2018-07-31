package org.oddjob.arooa.design.designer;

import java.util.EventObject;

public class DesignerErrorEvent extends EventObject {
	private static final long serialVersionUID = 2008121500L;
	
	private final String summary;
	
	private final Exception cause;
	
	
	public DesignerErrorEvent(DesignerModel source, 
			String summary, Exception cause) {
		super(source);
		
		this.summary = summary;
		
		this.cause = cause;
	}
	
	@Override
	public DesignerModel getSource() {
		return (DesignerModel) super.getSource();
	}
	
	public String getSummary() {
		return summary;
	}
	
	public Exception getCause() {
		return cause;
	}
}
