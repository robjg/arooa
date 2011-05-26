package org.oddjob.arooa.design.designer;

import org.oddjob.arooa.deploy.ArooaDescriptorDescriptor;
import org.oddjob.arooa.standard.StandardArooaSession;

public class ArooaDesignerMain {

	public static void main(String args[]) throws Exception {
				
		ArooaDesigner designer = new ArooaDesigner();
		designer.setArooaSession(
				new StandardArooaSession(
						new ArooaDescriptorDescriptor()));
		
		designer.run();
	}
}
