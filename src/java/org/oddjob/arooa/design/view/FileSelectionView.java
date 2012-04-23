/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.oddjob.arooa.design.screem.FileSelection;

/**
 * Produces views for FileSelection. 
 */
public class FileSelectionView implements SwingItemView, SwingFormView {
		
	private final FileSelection fileSelection;
	private final JLabel label;
	
	private final FileSelectionWidget widget;
	
	public FileSelectionView(FileSelection fs) {
		this.fileSelection = fs;

		String title = fileSelection.getTitle();
		StringBuffer paddedTitle = new StringBuffer();
		paddedTitle.append(title);
		for (int i = title.length(); i < Looks.LABEL_SIZE; ++i) {
			paddedTitle.append(' ');
		}
		label = new JLabel(paddedTitle.toString(), SwingConstants.LEADING);

		widget = new FileSelectionWidget();
		widget.setSelectedFile(fileSelection.getFile() == null ? null :
			new File(fileSelection.getFile()));		
		widget.addPropertyChangeListener(
				FileSelectionWidget.SELECTED_FILE_PROPERTY, 
				new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				File file = (File) evt.getNewValue();
				if (file == null) {
					fileSelection.setFile(null);
				}
				else {
					fileSelection.setFile(file.getPath());
				}
			}
		});
		
	}
	
	
	public Component cell() {
		return widget;
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
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.gridx = columnCount++;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(3, 3, 3, 3);
		
		container.add(widget, c);
		
		return row + 1;
	}

	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		label.setEnabled(enabled);
		widget.setEnabled(enabled);

		// Why do we need this?
		if (!enabled) {
			fileSelection.setFile(null);
		}
	}
	
}
