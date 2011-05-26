/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.oddjob.arooa.design.screem.FileSelection;

/**
 * Produces views for FileSelection. 
 */
public class FileSelectionView implements SwingItemView, SwingFormView {
		
	private final FileSelection fileSelection;
	private final JLabel label;
	private final JTextField textField;
	private JButton detailButton;
	
	public FileSelectionView(FileSelection fs) {
		this.fileSelection = fs;

		String title = fileSelection.getTitle();
		StringBuffer paddedTitle = new StringBuffer();
		paddedTitle.append(title);
		for (int i = title.length(); i < Looks.LABEL_SIZE; ++i) {
			paddedTitle.append(' ');
		}
		label = new JLabel(paddedTitle.toString(), SwingConstants.LEADING);

		textField = new JTextField(Looks.TEXT_FIELD_SIZE);
		updateView();
		
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				fileSelection.setFile(textField.getText());
			}
			public void removeUpdate(DocumentEvent e) {
				fileSelection.setFile(textField.getText());
			}
			public void insertUpdate(DocumentEvent e) {
				fileSelection.setFile(textField.getText());
			}
		});		
				
		detailButton = new JButton();
		detailButton.setAction(new AbstractAction("...") {
			private static final long serialVersionUID = 2008120800L;
			
			public void actionPerformed(ActionEvent e) {
			    JFileChooser chooser = new JFileChooser();
			    if (fileSelection.getFile() != null) {
			        chooser.setCurrentDirectory(new File(fileSelection.getFile()).getParentFile());
			    }
			    
				int option = chooser.showOpenDialog(detailButton);
				
				if (option == JFileChooser.APPROVE_OPTION) {
					File chosen = chooser.getSelectedFile();
					textField.setText(chosen.getPath());
				}
			}
		});
		
	}
	
	// cell and inline have a lot in common.
	private void updateView() {
		String fileName = fileSelection.getFile();
		if (fileName == null) {			
			textField.setText("");
		}
		else {
			textField.setText(fileName);
		}
		
	}
	
	public Component cell() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		detailButton.setMargin(new Insets(0, 0, 0, 0));
		panel.add(textField);
		panel.add(detailButton);
		return 	panel;
	}

	public Component dialog() {
		// only used from the test.
		return cell();
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#inline(java.awt.Container, int, int, boolean)
	 */
	public int inline(Container container, int row, int column,
			boolean selectionInGroup) {
		int columnCount = column;
		
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 1.0;
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
		
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.gridx = columnCount++;
		c.gridwidth = 1;
		c.insets = new Insets(3, 0, 3, 0);
		
		container.add(textField, c);
		
		if (detailButton != null) {
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.gridx = columnCount++;
			
			container.add(detailButton, c);
		}
		return row + 1;
	}

	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		label.setEnabled(enabled);
		textField.setEditable(enabled);
		detailButton.setEnabled(enabled);
		
		if (!enabled) {
			fileSelection.setFile(null);
		}
	}
	
}
