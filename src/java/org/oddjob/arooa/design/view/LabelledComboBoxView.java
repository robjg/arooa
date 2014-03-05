/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.oddjob.arooa.design.screem.LabelledComboBox;

/**
 * A view which renders a SelectionList. This Swing view uses a ComboBox
 * to rener the view.
 */
public class LabelledComboBoxView<T> implements SwingItemView {
		
	private final LabelledComboBox<T> formItem;
	
	final JComboBox<T> comboBox;
	final JLabel label;
	
	final JPanel form;

	/**
	 * Constructor.
	 * 
	 * @param formItem The form item.
	 */
	public LabelledComboBoxView(LabelledComboBox<T> formItem) {
		this.formItem = formItem;
		
		String title = formItem.getTitle();
		
		if (title == null) {
			throw new NullPointerException(
					"Null Titile. A LabelledComboBox must be labelled!");
		}

		label = new JLabel(ViewHelper.padLabel(title), 
					SwingConstants.LEADING);
		
		comboBox = new JComboBox<T>();
		
		T[] types = formItem.getSelections();
		for (int i = 0; i < types.length; ++i) {
			comboBox.addItem(types[i]);				
		}
		comboBox.setSelectedItem(formItem.getSelected());	
				
		comboBox.addActionListener(new ActionListener() {
	        @SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				JComboBox<T> cb = (JComboBox<T>) e.getSource();
				T type = (T) cb.getSelectedItem();
		        LabelledComboBoxView.this.formItem.setSelected(type);
			}

		});		

		form = new JPanel();
		form.setLayout(new BoxLayout(form, BoxLayout.X_AXIS));
		form.add(comboBox);
		form.add(Box.createHorizontalStrut(5));
		
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
		
		// label.
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
		
		// combo box.
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = columnCount++;
		c.gridy = row;
		c.gridwidth = 1;
		
		c.insets = new Insets(3, 3, 3, 20);		 

		container.add(comboBox, c);
						
		return row + 1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.view.SwingItemView#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		label.setEnabled(enabled);
		comboBox.setEnabled(enabled);
		
		if (!enabled) {
			comboBox.setSelectedItem(null);
		}
	}
}

