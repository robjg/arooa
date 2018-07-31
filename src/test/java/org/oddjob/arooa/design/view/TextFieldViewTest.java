package org.oddjob.arooa.design.view;

import org.junit.Test;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.SimpleTextAttribute;
import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.design.screem.TextField;
import org.oddjob.arooa.parsing.ArooaElement;

public class TextFieldViewTest extends Assert {

	private Component view;
	
	class OurInstance extends MockDesignInstance {
		ArooaElement element = new ArooaElement("apple");
		{
			element = element.addAttribute("one", "Partridge");
			element = element.addAttribute("one", "Turtle Doves");
		}
		@Override
		public ArooaElement element() {
			return element;
		}
	}
	
   @Test
	public void testSimple() {
		
		OurInstance design = new OurInstance();
		
		SimpleTextAttribute attribute = new SimpleTextAttribute("test", design);
		
		TextField textField = new TextField(attribute);
		
		TextFieldView test = new TextFieldView(textField);
		
		JPanel panel = new JPanel();
		
		int nextRow = test.inline(panel, 0, 0, false);		
	
		assertEquals(1, nextRow);

		assertEquals(2, panel.getComponentCount());

		Component c1 = panel.getComponent(0);
		Component c2 = panel.getComponent(1);
		
		assertEquals(JLabel.class, c1.getClass());
		assertEquals(JTextField.class, c2.getClass());
		
		JLabel label = (JLabel) c1;
		JTextField text = (JTextField) c2;
		
		assertEquals("test", label.getText().trim());
		assertEquals(Looks.LABEL_SIZE, label.getText().length());
		assertEquals("", text.getText());

		text.setText("apple");
		
		assertEquals("apple", attribute.attribute());
				
		this.view = panel;
	}
	
	public static void main(String args[]) throws ArooaParseException {
		TextFieldViewTest test = new TextFieldViewTest();
		test.testSimple();
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(test.view);
		frame.pack();
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}	

	
}
