package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import org.oddjob.arooa.design.screem.StandardForm;

public class StandardFormView implements SwingFormView {

	private final StandardForm standardForm;
	
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

		c.insets = new Insets(Looks.DETAIL_FORM_BORDER, 
				Looks.DETAIL_FORM_BORDER, 
				Looks.DETAIL_FORM_BORDER, 
				Looks.DETAIL_FORM_BORDER);

		c.gridx = 0;
		c.gridy = 0;
		form.add(Looks.typePanel(
					standardForm.getTitle(),
					standardForm.getDesign()),
				c);

		int items = standardForm.size();
		for (int i = 0; i < items ; ++i) {
			c.gridx = 0;
			c.gridy = i + 1;

			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			
			SwingItemView itemView = SwingItemFactory.create(
					standardForm.getFormItem(i));
			
			itemView.inline(panel, 0, 0, false);

			form.add(panel, c);
		}

		// pad the bottom.
		c.weighty = 1.0;
		form.add(new JPanel(), c);
		
		return form;
	}	
}
