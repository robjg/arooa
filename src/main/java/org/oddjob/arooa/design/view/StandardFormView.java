package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import org.oddjob.arooa.design.screem.StandardForm;

/**
 * Provides most designer dialogs.
 * <p>
 * Provide a panel with the element name, the id if the design
 * is for a component and then allows the form items to 
 * {@link SwingItemView#inline(java.awt.Container, int, int, boolean)}
 * themselves onto a GridBagLayout panel.
 * 
 * @author rob
 *
 */
public class StandardFormView implements SwingFormView {

	private final StandardForm standardForm;
	
	/**
	 * Constructor.
	 * 
	 * @param form
	 */
	public StandardFormView(StandardForm form) {
		this.standardForm = form;
	}
	
		
	public Component cell() {
		return ViewHelper.createDetailButton(standardForm);		
	}
	
	public Component dialog() {
		JPanel form = new JPanel();
		form.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;

		c.insets = Looks.DETIAL_FORM_INSETS;

		c.gridx = 0;
		c.gridy = 0;
		form.add(Looks.typePanel(
					standardForm.getTitle(),
					standardForm.getDesign()),
				c);

		FormPanel panel = new FormPanel();
		c.gridx = 0;
		c.gridy = 1;
		
		int row = 0;
		int items = standardForm.size();
		
		for (int i = 0; i < items ; ++i) {
			
			SwingItemView itemView = SwingItemFactory.create(
					standardForm.getFormItem(i));
			
			row = itemView.inline(panel, row, 0, false);

		}

		if (panel.isVerticallyResizable()) {
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;
		}
		
		form.add(panel, c);
		
		if (!panel.isVerticallyResizable()) {
			// pad the bottom.
			c.gridy = 2;
			c.weighty = 1.0;
			form.add(new JPanel(), c);
		}
		
		return form;
	}
	
}