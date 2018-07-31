package org.oddjob.arooa.design.view;

import org.junit.Test;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.design.screem.StandardForm;

public class StandardFormViewTest extends Assert {

	JComponent view;

	class MyDesign extends MockDesignInstance {
		
		String id = "apple";

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}
	
   @Test
	public void testHeaderLayout() {
		
		MyDesign design = new MyDesign();
		
		StandardForm form = new StandardForm("test", design);
		
		StandardFormView test = new StandardFormView(form);
		
		JComponent component = (JComponent) test.dialog();
		
		view = component;
	}
	
	public static void main(String args[]) throws ArooaParseException {
		StandardFormViewTest test = new StandardFormViewTest();
		test.testHeaderLayout();
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(test.view);
		frame.pack();
		frame.setVisible(true);

		System.out.println(test.view.getPreferredSize());
		
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}	

}
