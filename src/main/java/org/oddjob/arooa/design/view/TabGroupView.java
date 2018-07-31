/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.oddjob.arooa.design.screem.FormItem;
import org.oddjob.arooa.design.screem.TabGroup;

/**
 * 
 */
public class TabGroupView implements SwingItemView {

	private final List<SwingItemView> childViews = 
		new ArrayList<SwingItemView>();
	
	private final JTabbedPane tabbedPane;
	
	private boolean resizable;
	
	public TabGroupView(TabGroup fieldGroup) {
		
		tabbedPane = new JTabbedPane();
		
		int panelRow = 0;
		for (int i = 0; i < fieldGroup.size(); ++i) {
			FormItem designDefinition = fieldGroup.get(i); 
			SwingItemView viewProducer = SwingItemFactory.create(designDefinition);

			FormPanel form = new FormPanel();
			
			panelRow = viewProducer.inline(form, panelRow, 0, 
					false);
			
			if (form.isVerticallyResizable()) {
				resizable = true;
			}
			else {
				GridBagConstraints c = new GridBagConstraints();
				
				c.weightx = 1.0;
				c.weighty = 0.0;
				c.fill = GridBagConstraints.BOTH;
				c.anchor = GridBagConstraints.NORTHWEST;
				
				c.gridx = 0;
				c.gridy = panelRow;
				
				// pad the bottom.
				c.weighty = 1.0;
				form.add(new JPanel(), c);
			}
			
			tabbedPane.addTab(designDefinition.getTitle(), form);
			
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
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = column;
		c.gridy = row;
		c.gridwidth = GridBagConstraints.REMAINDER;
		
		if (resizable) {
			c.weighty = 1.0;
			c.fill = GridBagConstraints.BOTH;
		}
		else {
			c.weighty = 0.0;
			c.fill = GridBagConstraints.HORIZONTAL;
		}
		
		container.add(tabbedPane, c);
		
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
		tabbedPane.setEnabled(enabled);
	}

}
