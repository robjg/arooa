package org.oddjob.arooa.design.view;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.oddjob.arooa.design.screem.FileSelectionOptions;

/**
 * A simple file selection widget that is a text field and a button that
 * launches the a file chooser.
 * 
 * @author rob
 *
 */
public class FileSelectionWidget extends JPanel {
	private static final long serialVersionUID = 2012042300L;
	
	public static final String SELECTED_FILE_PROPERTY = "selectedFile";
	
	private final JTextField textField;
	private final JButton detailButton;
	
	private String selectedFile;
	
	private FileSelectionOptions options;

	/**
	 * Constructor.
	 */
	public FileSelectionWidget() {

		textField = new JTextField(Looks.TEXT_FIELD_SIZE);
		
		textField.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				updateFileFromTextField();
			}
			public void removeUpdate(DocumentEvent e) {
				updateFileFromTextField();
			}
			public void insertUpdate(DocumentEvent e) {
				updateFileFromTextField();
			}
		});		
		addPropertyChangeListener(SELECTED_FILE_PROPERTY, 
				new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				String file = (String) evt.getNewValue();
				String text = file == null ? "" : file;
				if (!text.equals(textField.getText())) {
					textField.setText(text);
				}
			}
		});
		
		detailButton = new JButton();
		detailButton.setAction(new AbstractAction("...") {
			private static final long serialVersionUID = 2008120800L;
			
			public void actionPerformed(ActionEvent e) {
			    JFileChooser chooser = new JFileChooser();
			    
				chooser.setFileSelectionMode(
						JFileChooser.FILES_AND_DIRECTORIES);
				
			    if (options != null) {
					// set file selection mode.
					if (options.getSelectionMode() != null) {
						switch (options.getSelectionMode()) {
						case FILE:
							chooser.setFileSelectionMode(
									JFileChooser.FILES_ONLY);
							break;
						case DIRECTORY:
							chooser.setFileSelectionMode(
									JFileChooser.DIRECTORIES_ONLY);
							break;
						}
					}
	
					// set default directory and file.
					chooser.setCurrentDirectory(
							options.getCurrentDirectory());
				    
					// set file filter extensions.
					if (options.getFileFilterExtensions() != null) {
						FileNameExtensionFilter filter =
								new FileNameExtensionFilter(
										options.getFileFilterDescription(), 
										options.getFileFilterExtensions());
						chooser.setFileFilter(filter);
					}
			    }
			    
			    if (getSelectedFile() != null) {
			        chooser.setSelectedFile(new File(getSelectedFile()));
			    }
			    
				int option = chooser.showDialog(detailButton, "OK");
				
				if (option == JFileChooser.APPROVE_OPTION) {
					File chosen = chooser.getSelectedFile();
					setSelectedFile(chosen.getPath());
				}
			}
		});
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		detailButton.setMargin(new Insets(0, 1, 0, 1));
		add(textField);
		add(detailButton);
		
		// Required when used as the cell of a table.
		addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				textField.requestFocus();
			}
		});
	}
	
	protected void updateFileFromTextField() {
		String text = textField.getText();
		if (text.length() == 0) {
			setSelectedFile(null);
		}
		else {
			setSelectedFile(text);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		textField.setEditable(enabled);
		detailButton.setEnabled(enabled);
		
		if (!enabled) {
			Document doc = textField.getDocument();
			try {
				doc.remove(0, doc.getLength());
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void setSelectedFile(String file) {
		String oldValue = this.selectedFile;
		this.selectedFile = file;
		firePropertyChange(SELECTED_FILE_PROPERTY, oldValue, file);
	}
	
	public String getSelectedFile() {
		return selectedFile;
	}
	
	public FileSelectionOptions getOptions() {
		return options;
	}

	public void setOptions(FileSelectionOptions options) {
		this.options = options;
	}

}
