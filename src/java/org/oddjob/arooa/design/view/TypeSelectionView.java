/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.DesignElementProperty;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.design.DesignStructureEvent;
import org.oddjob.arooa.design.InstanceSupport;
import org.oddjob.arooa.design.screem.SingleTypeSelection;
import org.oddjob.arooa.parsing.QTag;

/**
 * A view which renders a SelectionList. This Swing view uses a ComboBox
 * to rener the view.
 */
public class TypeSelectionView implements SwingItemView {
		
	public static final QTag NULL_TAG = new QTag("");
	
	private final DesignElementProperty designProperty;
	
	final JComboBox comboBox;
	final JLabel label;
	final JPanel cell;
	
	final JPanel form;

	private DesignInstance selected;
	
	private final JTextField dummy = new JTextField(Looks.TEXT_FIELD_SIZE);
	
	
	/**
	 * Constructor.
	 * 
	 * @param designProperty A SelectionList.
	 */
	public TypeSelectionView(SingleTypeSelection selection) {
		this.designProperty = selection.getDesignElementProperty();
		
		label = new JLabel(ViewHelper.padLabel(selection.getTitle()), 
				SwingConstants.LEADING);

		comboBox = new JComboBox();
		QTag[] types = getOptions();
		for (int i = 0; i < types.length; ++i) {
			comboBox.addItem(types[i]);				
		}
		comboBox.setSelectedItem(getSelected());	
		
		cell = new JPanel(new BorderLayout());
		cell.setBorder(dummy.getBorder());
		Insets insets = dummy.getBorder().getBorderInsets(dummy);
		dummy.setBorder(BorderFactory.createEmptyBorder(
				insets.top, insets.left, insets.bottom, insets.right));
		dummy.setEnabled(false);
		cell.add(dummy, BorderLayout.CENTER);
		
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
		        QTag type = (QTag) cb.getSelectedItem();
		        try {
		        	setSelected(type);
		        }
		        catch (ArooaParseException ex) {
		        	throw new DesignViewException(ex);
		        }
			}

		});		

		form = new JPanel();
		form.setLayout(new BoxLayout(form, BoxLayout.X_AXIS));
		form.add(comboBox);
		form.add(Box.createHorizontalStrut(5));
		form.add(cell);
		
		designProperty.addDesignListener(
				new DesignListener() {
					public void childAdded(DesignStructureEvent nodeEvent) {
						selected = nodeEvent.getChild();
						update();
					}
					public void childRemoved(DesignStructureEvent event) {
						selected = null;
						update();
					}
				});
	}
		
	private void update() {
		comboBox.setSelectedItem(getSelected());
        DesignInstance child = selected;
        cell.removeAll();
        if (child != null) {
	        cell.add(SwingFormFactory.create(child.detail()).cell(), 
	        		BorderLayout.CENTER);
        }
        else {
	        cell.add(dummy, 
	        		BorderLayout.CENTER);
        }
        cell.validate();
        cell.repaint();
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
		
		// ccell
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.gridx = columnCount++;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(3, 0, 3, 0);
		
		container.add(cell, c);
				
		return row + 1;
	}

	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.design.view.SwingItemView#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		label.setEnabled(enabled);
		comboBox.setEnabled(enabled);
		cell.setEnabled(enabled);
		
		if (!enabled) {
			comboBox.setSelectedItem(NULL_TAG);
		}
	}
		
	QTag[] getOptions() {
		QTag[] supportedTypes = new InstanceSupport(designProperty).getTags();
		QTag[] allOptions = new QTag[supportedTypes.length + 1];
		allOptions[0] = NULL_TAG;
		System.arraycopy(supportedTypes, 0, 
				allOptions, 1, supportedTypes.length);
		return allOptions;
	}
	
	QTag getSelected() {
		if (selected == null) {
			return NULL_TAG;
		}
		else {
			return InstanceSupport.tagFor(selected);
		}
	}
	
	void setSelected(QTag type) throws ArooaParseException {
		if (type == null) {
			throw new NullPointerException("New selected type can not be null.");
		}
		
		InstanceSupport instanceSupport = new InstanceSupport(designProperty);
		
		QTag oldType = NULL_TAG;
		DesignInstance oldDesign = selected; 
		if (selected != null) {
			oldType = InstanceSupport.tagFor(oldDesign);	
		}
		
		if (!NULL_TAG.equals(oldType) && !oldType.equals(type)) {
			instanceSupport.removeInstance(oldDesign);
		}
		
		if (!NULL_TAG.equals(type) && !oldType.equals(type)) {
			instanceSupport.insertTag(0, type);
		}
	}
	
	
}

