/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.design.DesignElementProperty;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.design.DesignStructureEvent;
import org.oddjob.arooa.design.InstanceSupport;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.MultiTypeTable;
import org.oddjob.arooa.parsing.QTag;

/**
 * This class is capable of representing a DesignElement which consists of 
 * multiple child DesignElements of different types.
 */
public class MultiTypeTableView implements SwingItemView {

	public static final QTag NULL_TAG = new QTag("");
	
	private final ValuesTableModel tm = new ValuesTableModel();
	
	private final MultiTypeTable viewModel;

	private JLabel label;
	
	private Component component;
	
	private JTable table; 
	
	private List<DesignInstance> instances = 
		new ArrayList<DesignInstance>();
	
	public MultiTypeTableView(MultiTypeTable viewModel) {
		this.viewModel = viewModel;
		
		this.component = component();
		
		String title = viewModel.getTitle(); 
		if (title != null && title.trim().length() > 0) {
			label = new JLabel(viewModel.getTitle());
		}
		
		viewModel.getDesignProperty().addDesignListener(new DesignListener() {
			public void childAdded(DesignStructureEvent event) {
				instances.add(event.getIndex(), event.getChild());
				tm.fireTableChanged(new TableModelEvent(tm));
			}
			public void childRemoved(DesignStructureEvent event) {
				instances.remove(event.getIndex());
				tm.fireTableChanged(new TableModelEvent(tm));
			}
		});		
	}

	private boolean isNamedValues() {
		return viewModel.isKeyed();
	}
	
	/* (non-Javadoc)
	 * @see org.oddjob.designer.view.ViewProducer#inline(java.awt.Container, int, int, boolean)
	 */
	public int inline(Container container, int row, int column,
			boolean selectionInGroup) {
		
		GridBagConstraints c = new GridBagConstraints();

		if (label != null) {
			c.weightx = 1.0;
			c.weighty = 0.0;
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.NORTHWEST;
			c.gridx = column;
			c.gridy = row;
			if (selectionInGroup) {
				c.gridwidth = 2;
			}
			
			c.insets = new Insets(3, 3, 3, 20);		 
	
			container.add(label, c);
			
			++row;
		}
		
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
		if (label != null) {
			label.setEnabled(enabled);
		}
		table.setEnabled(enabled);
		
		
		if (!enabled) {
			while (tm.getRowCount() > 1) {
				tm.setValueAt(NULL_TAG, 0, 0);
			}
		}
	}

	/**
	 * Creates the table component.
	 * 
	 * @return The table.
	 */
	private Component component() {
		table = new JTable(tm);
		table.setRowHeight(new JTextField().getPreferredSize().height);
		
		setPreferredTableSize(table);
		
		TableColumn typeCol = table.getColumnModel().getColumn(0);
		
		JComboBox comboBox = new JComboBox();
		QTag[] types = getOptions();
		for (int i = 0; i < types.length; ++i) {
			comboBox.addItem(types[i]);				
		}
		typeCol.setCellEditor(new DefaultCellEditor(comboBox));

		TableColumn valueCol = table.getColumnModel().getColumn(
				tm.getColumnCount() - 1);
		valueCol.setCellEditor(new DialogEditor());
		valueCol.setCellRenderer(new DialogRenderer());

		JScrollPane jsp = new JScrollPane(table);
		return jsp;
	}
		
	private void setPreferredTableSize(JTable table){ 
	    int height = 0; 
	    for(int row=0; row < viewModel.getVisibleRows(); row++) 
	        height += table.getRowHeight(row); 
	 
	    table.setPreferredScrollableViewportSize(new Dimension( 
	            Looks.DETAIL_USABLE_WIDTH - 100,
	            height)); 
	}
			
	class ValuesTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 2008100100;

		String[] unnamedHeaders = { "Type", "Value" };
		String[] namedHeaders = { "Type", "Key", "Value" };

		public String getColumnName(int c) {
			if (isNamedValues()) {
				return namedHeaders[c];
			} else {
				return unnamedHeaders[c];
			}
		}

		public int getColumnCount() {
			return isNamedValues() ? 3 : 2;
		}

		public int getRowCount() {
			return instances.size() + 1;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return true;
			}
			if (!"".equals(getValueAt(rowIndex, 0))) {
				return true;
			}
			return false;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex == getRowCount() - 1) {
				return "";
			}
			
			if (columnIndex == 0) {
				DesignInstance design = instances.get(rowIndex);
				return InstanceSupport.tagFor(design);
			} else if (columnIndex == 1) {
				if (isNamedValues()) {
					return viewModel.getChildName(rowIndex);
				} else {
					return instances.get(rowIndex);
				}
			} else if (columnIndex == 2) {
				return instances.get(rowIndex);
			} else {
				throw new RuntimeException("This should be impossible!");
			}
		}

		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				DesignElementProperty designProperty =
					viewModel.getDesignProperty();
				
				InstanceSupport support = new InstanceSupport(
						designProperty);
				
				QTag type = (QTag) value;
				if (rowIndex < getRowCount() - 1) {
					
					DesignInstance oldInstance = instances.get(rowIndex);
					QTag oldType = InstanceSupport.tagFor(oldInstance);
					
					if (type.equals(oldType)) {
						// if the type hasn't changed then nothing to do.
						return;
					}
					else {
						support.removeInstance(oldInstance);
					}
				}
				if (NULL_TAG.equals(type)) {
					return;
				}
				
				try {
					support.insertTag(rowIndex, type);
				}
				catch (ArooaParseException e) {
					throw new DesignViewException(e);
				}
			} else if (columnIndex == 1) {
				if (isNamedValues()) {
					viewModel.setChildName(rowIndex, (String)value);
				} 
			} else if (columnIndex == 2) {
				// don't do anything - the render takes care of the display.
			} else {
				throw new RuntimeException("This should be impossible!");
			}
		}

	}
	
	public class DialogEditor extends AbstractCellEditor 
	implements TableCellEditor {
		private static final long serialVersionUID = 20081008;
		
		DesignInstance value;

		//Implement the one CellEditor method that AbstractCellEditor doesn't.
		public Object getCellEditorValue() {
			return value;
		}

		//Implement the one method defined by TableCellEditor.
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			this.value = (DesignInstance) value;
			return generateRenderer((DesignInstance) value);
		}
	}

	class DialogRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (value instanceof String) {
				return new JLabel(value.toString());
			} else {
				return generateRenderer((DesignInstance) value);
			}
		}
	}
	
	static Component generateRenderer(DesignInstance de) {
		Form designDefintion = de.detail();
		if (de != null) {
			return SwingFormFactory.create(designDefintion).cell();
		}
		else {
			JTextField nothing = new JTextField();
			nothing.setEnabled(false);
			return nothing;
		}
	}
	
	QTag[] getOptions() {
		QTag[] supportedTypes = new InstanceSupport(
				viewModel.getDesignProperty()).getTags();
		QTag[] allOptions = new QTag[supportedTypes.length + 1];
		allOptions[0] = NULL_TAG;
		System.arraycopy(supportedTypes, 0, 
				allOptions, 1, supportedTypes.length);
		return allOptions;
	}
}
