package org.oddjob.arooa.design.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.oddjob.arooa.design.screem.BeanForm;

/**
 * The Swing view for a {@link BeanForm}.
 * 
 * @author rob
 *
 */
public class BeanFormView implements SwingFormView {

	private final BeanForm beanForm;

	private final JComponent form;
	
	private final JComponent subForm;
	
	/**
	 * Constructor.
	 * 
	 * @param beanForm
	 */
	public BeanFormView(final BeanForm beanForm) {
		
		this.beanForm = beanForm;
		
		form = new JPanel();
		form.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;

		c.insets = Looks.DETIAL_FORM_INSETS;

		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridy = 0;
		form.add(Looks.typePanel(
					beanForm.getTitle(),
					beanForm.getDesign()),
				c);


		c.gridy = 1;
		
		form.add(classNamePanel(), c);
		
		subForm = new JPanel(new GridBagLayout());
		subForm.setBorder(Looks.groupBorder(null));
		
		populateSubForm();
		
		c.gridy = 2;
		
		form.add(subForm, c);
		
		// Pad the bottom.
		c.gridy = 3;
		c.weighty = 1.0;
		form.add(new JPanel(), c);
		
		
		beanForm.addPropertyChangeListener(BeanForm.SUBFORM_PROPERTY, 
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						populateSubForm();
					}
				});
	}
	
	/** Panel for Class Name Text Field.
	 * 
	 * @return
	 */
	private JComponent classNamePanel() {
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel panel = new JPanel(new GridBagLayout());		
		panel.setBorder(Looks.groupBorder(null));
		
		Looks.setCommonLabelContraints(c);
		c.gridwidth = 1; 
		c.gridx = 0;
		c.gridy = 0;
		
		panel.add(new JLabel(ViewHelper.padLabel("Class Name"), 
				SwingConstants.LEADING), c);
		
		Looks.setCommonTextFieldContraints(c);
		c.gridx = 1;
		
		final JTextField className = new JTextField(Looks.TEXT_FIELD_SIZE);
		className.setText(beanForm.getDesign().getClassName());
		className.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				beanForm.setClassName(className.getText());
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				beanForm.setClassName(className.getText());
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				beanForm.setClassName(className.getText());
			}
		});
		
		panel.add(className, c);
		
		return panel;
	}
	
	/**
	 * Populate, or re-populate the sub form. Removes previous components
	 * and re draws the form.
	 */
	private void populateSubForm() {
	
		subForm.removeAll();
		
		SwingFormView subFormView = 
				SwingFormFactory.create(beanForm.getSubForm());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		subForm.add(subFormView.dialog(), c);
		
		subForm.revalidate();
	}
	
	@Override
	public Component cell() {
		return ViewHelper.createDetailButton(beanForm);		
	}

	@Override
	public Component dialog() {
		return form;
	}

	/**
	 * View for sub form when class is not found.
	 *
	 */
	public static class ClassNotFoundView implements SwingFormView {
		
		private final JPanel component;
		
		public ClassNotFoundView(BeanForm.ClassNotFoundForm form) {
			component = new JPanel(new BorderLayout());
			component.setBorder(new CompoundBorder(
					new EmptyBorder(10, 10, 10, 10),
					new CompoundBorder(
						new LineBorder(Color.RED),
						new EmptyBorder(50, 0, 50, 0)
					)));
			
			JLabel label = new JLabel(form.getMessage());
			label.setHorizontalAlignment(JLabel.CENTER);
			
			component.add(label, BorderLayout.CENTER);
		}
		
		@Override
		public Component cell() {
			throw new UnsupportedOperationException(
					"This view is only expected to produce a dialogue.");
		}
		
		@Override
		public Component dialog() {
			return component;
		}
	}
	
	/**
	 * View for properties sub form.
	 * 
	 * @author rob
	 *
	 */
	public static class PropertiesView implements SwingFormView {
		
		private final JPanel component;
		
		public PropertiesView(BeanForm.PropertiesForm form) {
			if (form.size() == 0) {
				component = new JPanel(new BorderLayout());
				component.setBorder(new EmptyBorder(61, 61, 61, 61));
				
				JLabel label = new JLabel("Bean Has No Settable Properties");
				label.setHorizontalAlignment(JLabel.CENTER);
				
				component.add(label, BorderLayout.CENTER);
			}
			else {
				component = new JPanel(new GridBagLayout());
							
				int row = 0;
				
				for (int i = 0; i < form.size(); ++i) {
					
					SwingItemView itemView = SwingItemFactory.create(
							form.getFormItem(i));
					
					row = itemView.inline(component, row, 0, false);
				}
			}
		}
		
		@Override
		public Component cell() {
			throw new UnsupportedOperationException(
					"This view is only expected to produce a dialogue.");
		}
		
		@Override
		public Component dialog() {
			return component;
		}
	}
}
