package org.oddjob.arooa.design.layout;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.deploy.URLDescriptorFactory;
import org.oddjob.arooa.design.designer.ArooaDesigner;
import org.oddjob.arooa.standard.StandardArooaSession;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class DesignerForEverythingMain {

	public static void main(String args[]) throws Exception {

		URL descriptorUrl = Objects.requireNonNull(
				DesignerForEverythingMain.class.getResource("ThingDescriptor.xml"));

		ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
				.createDescriptor(DesignerForEverythingMain.class.getClassLoader());

		File configFile = new File(DesignerForEverythingMain.class.getResource("ThingConfig.xml").getFile());

		ArooaDesigner designer = new ArooaDesigner();
		designer.setFile(configFile);

		designer.setArooaSession(
				new StandardArooaSession(descriptor));

		designer.setArooaType(ArooaType.COMPONENT);
		
		designer.run();
	}
}
