package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.SimpleTextAttribute;
import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.design.screem.BorderedGroup;
import org.oddjob.arooa.parsing.ArooaElement;

public class FieldGroupViewTest extends TestCase {

	private Component view;
	
	class OurInstance extends MockDesignInstance {
		ArooaElement element = new ArooaElement("apple");
		{
			element = element.addAttribute("one", "Partridge");
			element = element.addAttribute("two", "Turtle Doves");
		}
		@Override
		public ArooaElement element() {
			return element;
		}
	}
	
	public void testTwoSimpleFields() {
		
		BorderedGroup fieldGroup = new BorderedGroup("2 Fields");
		
		OurInstance design = new OurInstance();
		
		SimpleTextAttribute attribute1 = new SimpleTextAttribute("one", design);
		SimpleTextAttribute attribute2 = new SimpleTextAttribute("two", design);
		
		fieldGroup.add(attribute1.view()).add(attribute2.view());
	
		FieldGroupView test = new FieldGroupView(fieldGroup);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		int nextRow = test.inline(panel, 0, 0, false);		
	
		assertEquals(1, nextRow);
		
//		assertEquals(1, panel.getComponentCount());
//		
//		Component component = panel.getComponent(0);
//		
//		assertTrue(component instanceof JPanel);
		
		this.view = panel;
	}
	
	public static void main(String args[]) throws ArooaParseException {
		FieldGroupViewTest test = new FieldGroupViewTest();
		test.testTwoSimpleFields();
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(test.view);
		frame.pack();
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}	

	
}
