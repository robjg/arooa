package org.oddjob.arooa.design.designer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.deploy.ArooaDescriptorDescriptor;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ElementConfiguration;
import org.oddjob.arooa.standard.StandardArooaSession;

public class ArooaDesignerFormViewMain {

	ArooaDesignerForm form;
	
	public void testRun() throws ArooaParseException, URISyntaxException {
		
		final ArooaSession session = new StandardArooaSession(
				new ArooaDescriptorDescriptor());
				
		final DesignParser parser = new DesignParser(session);
		parser.setArooaType(ArooaType.VALUE);
		
		parser.parse(new ElementConfiguration(
				new ArooaElement(
						new URI("http://rgordon.co.uk/oddjob/arooa"), 
						"descriptor")));
		
		form = new ArooaDesignerForm(parser);
		
	}
	
	public static void main(String[] args) throws ArooaParseException, URISyntaxException {

		ArooaDesignerFormViewMain test = new ArooaDesignerFormViewMain();
		test.testRun();
		
		ArooaDesignerFormView view = new ArooaDesignerFormView(test.form);
		
		Component component = view.dialog();
		
		JFrame frame = new JFrame();

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(component, BorderLayout.CENTER);	
		frame.pack();
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

	}
	
}
