/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.FieldSelection;

/**
 * Used to select between different option elements.
 */
public class FieldSelectionView implements SwingItemView {

	private final FieldSelection fieldSelection;
	
	public FieldSelectionView(FieldSelection fieldSelection) {
		this.fieldSelection = fieldSelection;
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#inline(java.awt.Container, int, int, boolean)
	 */
	public int inline(Container container, int row, int column,
			boolean selectionInGroup) {
		ButtonGroup bg = new ButtonGroup();
		
		SwingItemView[] views = new SwingItemView[fieldSelection.size()];
		// remember which design definition was populated, if any.
		JRadioButton populated = null;
		
		for (int groupNum = 0; groupNum < fieldSelection.size(); ++groupNum) {

			JRadioButton button = new JRadioButton();
			button.addActionListener(new GroupActionListener(views, groupNum));
			bg.add(button);
			
			FormItem group = fieldSelection.get(groupNum);

			int startRow = row;
			
			SwingItemView view = SwingItemFactory.create(group);
			views[groupNum] = view;
			row = view.inline(container, row, column + 1, false);
			addOptionButton(container, startRow, row - startRow, button);
			
			if (group.isPopulated()) {
				populated = button;				
			}
		}
		if (populated != null) {
			populated.doClick();
		}
		else {
			for (int i = 0; i < views.length; ++i) {
				views[i].setEnabled(false);
			}
		}
		return row;
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		throw new UnsupportedOperationException("Not supporting nested selections yet!");
	}
	
	void addOptionButton(Container container, int row, int height, Component button) {
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 0.0;

		c.gridx = 0;
		c.gridy = row;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		
		c.gridheight = height;
		c.insets = new Insets(3, 1, 3, 1);
		
		container.add(button, c);

	}		
	
	static class GroupActionListener implements ActionListener {
		final int ourGroupNum;

		final SwingItemView[] components;

		GroupActionListener(SwingItemView[] components, int ourGroupNum) {
			this.components = components;
			this.ourGroupNum = ourGroupNum;
		}

		public void actionPerformed(ActionEvent e) {
			for (int groupNum = 0; groupNum < components.length; ++groupNum) {
				if (groupNum == ourGroupNum) {
					components[groupNum].setEnabled(true);
				}
				else {
					components[groupNum].setEnabled(false);						
				}
			}
		}
	}
}
