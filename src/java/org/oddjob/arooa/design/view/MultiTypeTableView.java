/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import org.oddjob.arooa.design.screem.MultiTypeTable;
import org.oddjob.arooa.design.view.multitype.MultiTypeDesignModel;
import org.oddjob.arooa.design.view.multitype.MultiTypeModel;
import org.oddjob.arooa.design.view.multitype.MultiTypeStrategy;
import org.oddjob.arooa.design.view.multitype.MultiTypeTableWidget;

/**
 * This class is capable of representing a DesignElement which consists of 
 * multiple child DesignElements of different types.
 */
public class MultiTypeTableView implements SwingItemView {

	private final MultiTypeModel model;

	private MultiTypeTableWidget component;
	
	public MultiTypeTableView(MultiTypeTable viewModel) {
		this.model = new MultiTypeDesignModel(viewModel);
		
		this.component = new MultiTypeTableWidget(model,
				viewModel.isKeyed() ? 
						MultiTypeStrategy.Strategies.KEYED :
						MultiTypeStrategy.Strategies.LIST);
		
		component.setVisibleRows(viewModel.getVisibleRows());		
		component.setTitle(viewModel.getTitle()); 
	}

	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#inline(java.awt.Container, int, int, boolean)
	 */
	public int inline(Container container, int row, int column,
			boolean selectionInGroup) {
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 1.0;
		c.weighty = 0.0;
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		
		c.gridx = column;
		c.gridy = row;
		
		c.gridwidth = GridBagConstraints.REMAINDER;
				
		c.insets = new Insets(3, 3, 3, 3);		 

		container.add(component, c);
		
		return row + 1;
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		component.setEnabled(enabled);
		if (!enabled) {
			while (model.getRowCount() > 1) {
				model.removeRow(0);
			}
		}
	}		
}
