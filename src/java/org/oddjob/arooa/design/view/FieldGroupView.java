/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;

import org.oddjob.arooa.design.screem.BorderedGroup;
import org.oddjob.arooa.design.screem.FieldGroup;
import org.oddjob.arooa.design.screem.FormItem;

/**
 * 
 */
public class FieldGroupView implements SwingItemView {

	private final List<SwingItemView> childViews = 
		new ArrayList<SwingItemView>();
	
	private final JPanel panel;
	
	public FieldGroupView(FieldGroup fieldGroup) {
		
		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		if (fieldGroup instanceof BorderedGroup) {
			panel.setBorder(Looks.groupBorder(fieldGroup.getTitle()));
		}
		
		int panelRow = 0;
		for (int i = 0; i < fieldGroup.size(); ++i) {
			FormItem designDefinition = fieldGroup.get(i); 
			SwingItemView viewProducer = SwingItemFactory.create(designDefinition);

			panelRow = viewProducer.inline(panel, panelRow, 0, 
					fieldGroup.isContainsSelection());
			
			childViews.add(viewProducer);
		}
	}

	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#form()
	 */
	public Component group() {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#inline(java.awt.Container, int, int, boolean)
	 */
	public int inline(Container container, int row, int column,
			boolean selectionInGroup) {
		
		GridBagConstraints c = new GridBagConstraints();

		c.weightx = 1.0;
		c.weighty = 0.0;
		
		// label.
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = column;
		c.gridy = row;
		c.gridwidth = GridBagConstraints.REMAINDER;
		
		container.add(panel, c);
		
		return row + 1;
	}

	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		for (Iterator<SwingItemView> it = childViews.iterator(); it.hasNext(); ) {
			it.next().setEnabled(enabled);
		}
		
		// unfortunately this has no affect on the border!
		panel.setEnabled(enabled);
	}

}
