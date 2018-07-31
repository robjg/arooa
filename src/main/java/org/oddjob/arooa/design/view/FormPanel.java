package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

/**
 * A panel for Forms. It's main purpose is to detect if the form is
 * resizable, so forms with text boxes will resize the text box
 * but forms with just fields will add padding.
 * 
 * @author rob
 *
 */
public class FormPanel extends JPanel {

	private static final long serialVersionUID = 2013012500L;

	private boolean verticallyResizable;

	public FormPanel() {
		super.setLayout(new GridBagLayout());
	}
		
	@Override
	public void add(Component comp, Object constraints) {
		processConstraints(constraints);
		super.add(comp, constraints);
	}

	@Override
	public Component add(Component comp) {
		throw new UnsupportedOperationException("Must use a constraint");
	}

	@Override
	public void add(Component comp, Object constraints, int index) {
		processConstraints(constraints);
		super.add(comp, constraints, index);
	}
	
	protected void processConstraints(Object constraints) {
		GridBagConstraints c = (GridBagConstraints) constraints;

		if (c.fill == GridBagConstraints.BOTH
				|| c.fill == GridBagConstraints.VERTICAL) {
			verticallyResizable = true;
		}
	}
	
	public boolean isVerticallyResizable() {
		return verticallyResizable;
	}

}
