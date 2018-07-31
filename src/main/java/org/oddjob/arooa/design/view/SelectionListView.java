/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.oddjob.arooa.design.screem.SelectionList;

/**
 * A view which renders a SelectionList. This Swing view uses a ComboBox
 * to rener the view.
 */
public class SelectionListView implements SwingItemView {
		
	private final SelectionList selectionList;
	
	private final JComboBox<String> comboBox;
	private final JLabel label;
	
	/**
	 * Constructor.
	 * 
	 * @param selection A SelectionList.
	 */
	public SelectionListView(SelectionList selection) {
		this.selectionList = selection;
				
		label = new JLabel(ViewHelper.padLabel(selectionList.getTitle()), 
				SwingConstants.LEADING);

		comboBox = new JComboBox<String>();
		comboBox.addItem("");
		String[] types = selection.getOptions();
		for (int i = 0; i < types.length; ++i) {
			comboBox.addItem(types[i]);				
		}
		if (selection.getSelected() != null) {
			comboBox.setSelectedItem(selection.getSelected());	
		}		
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				@SuppressWarnings("unchecked")
				JComboBox<String> cb = (JComboBox<String>)e.getSource();
		        selectionList.setSelected((String)cb.getSelectedItem());
			}
		});		
	}
	
	public Component group() {
		throw new UnsupportedOperationException();
		
//		JPanel group = new JPanel();
//		group.setBorder(Looks.groupBorder(selectionList.getTitle()));
//		group.add(comboBox);
//		return group;
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
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = columnCount++;
		c.gridwidth = 1;
		c.insets = new Insets(3, 0, 3, 0);
		
		container.add(comboBox, c);
		
		return row + 1;
	}

	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		comboBox.setEditable(enabled);
		if (!enabled) {
			selectionList.setSelected(null);
		}
	}
	
}
