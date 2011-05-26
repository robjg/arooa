/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.oddjob.arooa.design.DesignAttributeProperty;
import org.oddjob.arooa.design.screem.TextField;

/**
 * <pre>
 * Label Title     *********************
 * </pre>
 * 
 */
public class TextFieldView implements SwingItemView {

	private final DesignAttributeProperty property;

	private final JLabel label;
	private final JTextField textField;
	
	/**
	 * Constructor.
	 * 
	 * @param elementField The ElementField being modelled.
	 */
	public TextFieldView(TextField elementField) {
		this.property = elementField.getAttribute();

		String title = elementField.getTitle();
		label = new JLabel(ViewHelper.padLabel(title), SwingConstants.LEADING);

		textField = new JTextField(Looks.TEXT_FIELD_SIZE);
		textField.setText(property.attribute());
		
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				property.attribute(textField.getText());
			}
			public void removeUpdate(DocumentEvent e) {
				property.attribute(textField.getText());
			}
			public void insertUpdate(DocumentEvent e) {
				property.attribute(textField.getText());
			}
		});		
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#inline(java.awt.Container, int, int, boolean)
	 */
	public int inline(Container container, int row, int column,
			boolean selectionInGroup) {
		int columnCount = column;
		
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 0.3;
		c.weighty = 0.0;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = columnCount++;
		c.gridy = row;
		if (selectionInGroup) {
			c.gridwidth = 2;
			columnCount++;
		}
		
		c.insets = new Insets(3, 3, 3, 20);		 

		container.add(label, c);
		
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = columnCount++;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(3, 0, 3, 0);
		
		container.add(textField, c);
				
		return row + 1;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		label.setEnabled(enabled);
		
		if (!enabled) {
			Document doc = textField.getDocument();
			try {
				doc.remove(0, doc.getLength());
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
		
		textField.setEditable(enabled);		
	}
		
}
