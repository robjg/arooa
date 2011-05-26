/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.oddjob.arooa.design.screem.TextInput;

/**
 *  
 */
public class TextInputView implements SwingItemView, SwingFormView {
		
	private final TextInput textInput;
	private final JTextField text;
	private final JTextArea textArea;
	private final JButton button;
	
	public TextInputView(TextInput ti) {
		this.textInput = ti;
		
		text = new JTextField(Looks.TEXT_FIELD_SIZE);
		Insets insets = text.getBorder().getBorderInsets(text);
		text.setBorder(BorderFactory.createEmptyBorder(
				insets.top, insets.left, insets.bottom, insets.right));
		
		text.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
				textInput.setText(text.getText());
			}
		});
		
		textArea = new JTextArea();
		textArea.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
				textInput.setText(textArea.getText());
			}
		});		
		
		button = new JButton();
		button.setAction(new AbstractAction("...") {
			private static final long serialVersionUID = 2008100100;

			public void actionPerformed(ActionEvent e) {
				Component form = SwingFormFactory.create(textInput).dialog();
	
				ValueDialog valueDialog = new ValueDialog(form);
				valueDialog.showDialog(button);
				
				text.setText(textInput.getText());
			}
		});
		updateView();
	}
	
	public Component dialog() {
		
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(textArea);

		JPanel form = new JPanel(new BorderLayout());
		form.add(scroll, BorderLayout.CENTER);
		
		form.setPreferredSize(new Dimension(
				300, 200));
		
		return form;	
	}
	
	public Component cell() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		button.setMargin(new Insets(0, 0, 0, 0));
		panel.add(text);
		panel.add(button);
		return 	panel;
	}
	
	private void updateView() {
		text.setText(textInput.getText());
		textArea.setText(textInput.getText());
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#inline(java.awt.Container, int, int, boolean)
	 */
	public int inline(Container container, int row, int column,
			boolean selectionInGroup) {
		
		Component form = dialog();
		form.setPreferredSize(new Dimension(
				Looks.DETAIL_USABLE_WIDTH - 30, 200));
		
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 1.0;
		c.weighty = 0.0;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		c.gridx = column;
		c.gridy = row;
		
		c.gridwidth = GridBagConstraints.REMAINDER;
				
		c.insets = new Insets(3, 3, 3, 3);		 

		container.add(form, c);
		
		return row + 1;
	}

	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		text.setEnabled(enabled);
		if (!enabled) {
			textInput.setText("");
		}
	}
	
}
