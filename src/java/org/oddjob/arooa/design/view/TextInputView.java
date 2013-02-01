/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.oddjob.arooa.design.screem.TextInput;

/**
 *  A view for form and a form item that is a text area.
 */
public class TextInputView implements SwingItemView, SwingFormView {
		
	private final TextInput textInput;
	private final JTextField text;
	private final JTextArea textArea;
	private final JButton button;
	
	/**
	 * Constructor.
	 * 
	 * @param textInputModel The model.
	 */
	public TextInputView(TextInput textInputModel) {
		this.textInput = textInputModel;
		
		text = new JTextField(Looks.TEXT_FIELD_SIZE);
		Insets insets = text.getBorder().getBorderInsets(text);
		text.setBorder(BorderFactory.createEmptyBorder(
				insets.top, insets.left, insets.bottom, insets.right));
		textArea = new JTextArea();
		
		updateView();
		
		text.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				textInput.setText(text.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				textInput.setText(text.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				textInput.setText(text.getText());
			}
		});
		
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				textInput.setText(textArea.getText());
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				textInput.setText(textArea.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				textInput.setText(textArea.getText());
			}
		});
		
		button = new JButton();
		button.setAction(new AbstractAction("...") {
			private static final long serialVersionUID = 2008100100;

			public void actionPerformed(ActionEvent e) {
				Component form = SwingFormFactory.create(textInput).dialog();
	
				ValueDialog valueDialog = new ValueDialog(form,
						new Callable<Boolean>() {
							@Override
							public Boolean call() throws Exception {
								text.setText(textInput.getText());
								return true;
							}
						});
				valueDialog.showDialog(button);
			}
		});
	}
	
	public Component dialog() {
		
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(textArea);

		scroll.setPreferredSize(new Dimension(
				400, 400));
		
		return scroll;	
	}
	
	public Component cell() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		button.setMargin(new Insets(0, 0, 0, 0));
		panel.add(text);
		panel.add(button);
		// Required when used as the cell of a table.
		panel.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				text.requestFocus();
			}
		});

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
		
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(textArea);
		
		scroll.setPreferredSize(new Dimension(
				Looks.DETAIL_USABLE_WIDTH - 30, 200));
		
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 1.0;
		c.weighty = 1.0;
		
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		c.gridx = column;
		c.gridy = row;
		
		c.gridwidth = GridBagConstraints.REMAINDER;
				
		c.insets = new Insets(3, 3, 3, 3);		 

		container.add(scroll, c);
		
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
