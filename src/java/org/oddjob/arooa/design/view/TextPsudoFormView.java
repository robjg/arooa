/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.oddjob.arooa.design.DesignAttributeProperty;
import org.oddjob.arooa.design.screem.TextPseudoForm;

/**
 * <pre>
 * Label Title     *********************
 * </pre>
 * 
 */
public class TextPsudoFormView implements SwingFormView {

	private final DesignAttributeProperty element;

	private final JTextField textField;
	
	/**
	 * Constructor.
	 * 
	 * @param elementField The ElementField being modelled.
	 */
	public TextPsudoFormView(TextPseudoForm elementField) {
		this.element = elementField.getAttribute();

		textField = new JTextField(Looks.TEXT_FIELD_SIZE);
		Insets insets = textField.getBorder().getBorderInsets(textField);
		textField.setBorder(BorderFactory.createEmptyBorder(
				insets.top, insets.left, insets.bottom, insets.right));

		updateView();
		
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				element.attribute(textField.getText());
			}
			public void removeUpdate(DocumentEvent e) {
				element.attribute(textField.getText());
			}
			public void insertUpdate(DocumentEvent e) {
				element.attribute(textField.getText());
			}
		});		
	}
	
	/**
	 * Called initially and then after the model has changed.
	 *
	 */
	private void updateView() {
		textField.setText(element.attribute());
	}
		
	public Component cell() {
		return textField;
	}
	
	/**
	 * This should only be called during a test because in the
	 * normal use case the text field is presented instead of the
	 * button which is the standard way of launching a form dialog.
	 */
	public Component dialog() {
		JPanel panel = new JPanel();
		panel.add(textField);
		return panel;
	}
}
