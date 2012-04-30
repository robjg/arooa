/*
 * (c) Rob Gordon 2005.
 */
package org.oddjob.arooa.design.view.multitype;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;


/**
 * Create a widget that supports editing of different element types using
 * a table.
 * <p>
 * This widget with different {@link MultiTypeStrategy}s is used for 
 * index properties, mapped properties and the Oddjob Variables designer.
 * 
 */
public class MultiTypeTableWidget extends JPanel {
	private static final long serialVersionUID = 20120424;

	public static final String SWAP_UP_ACTION_COMMAND = "swapUp";
	
	public static final String SWAP_DOWN_ACTION_COMMAND = "swapDown";
	
	/** The model. */
	private final MultiTypeModel model;
	
	/** Used for the title. */
	private final JLabel label;
	
	/** The widgets table. */
	private final JTable table; 
	
	/** The action for moving the selection up a row. */
	private final Action swapUpAction;
	
	/** The action for moving the selection down a row. */
	private final Action swapDownAction;
	
	/**
	 * Create a new widget.
	 * 
	 * @param model The model.
	 * @param strategy The strategy.
	 */
	public MultiTypeTableWidget(MultiTypeModel model, 
			MultiTypeStrategy strategy) {
		
		this.model = model;
		
		TableModel tableModel = strategy.tableModelFor(model);
		
		setLayout(new BorderLayout());
		
		JPanel topPanel = new JPanel(new BorderLayout());
		label = new JLabel();
		topPanel.add(label, BorderLayout.WEST);
		
		table = new JTable(tableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setSurrendersFocusOnKeystroke(true);		
		table.setRowHeight(new JTextField().getPreferredSize().height);
		
		swapUpAction = new SwapUp();
		swapDownAction = new SwapDown();
		registerActions(table);
		registerActions(this);
		
		JButton upButton = new JButton(swapUpAction);
		JButton downButton = new JButton(swapDownAction);

		JPanel buttons = new JPanel();
		buttons.add(upButton);
		buttons.add(downButton);
		topPanel.add(buttons, BorderLayout.EAST);
		
		table.getSelectionModel().addListSelectionListener(
				new TableSelectionListener());

		// Create the options for the type combo drop down
		TableColumn typeCol = table.getColumnModel().getColumn(
				strategy.getTypeColumn());
		Object[] options = model.getTypeOptions();
		Object[] optionsPlus = new Object[options.length + 1];
		optionsPlus[0] = model.getDeleteOption();
			System.arraycopy(options, 0, optionsPlus, 1, options.length);
		JComboBox typeChooser = new JComboBox(optionsPlus);
		typeCol.setCellEditor(new DefaultCellEditor(typeChooser));

		// Set the value renderer and editor.
		TableColumn valueCol = table.getColumnModel().getColumn(
				strategy.getValueColumn());
		valueCol.setCellEditor(new DialogEditor());
		valueCol.setCellRenderer(new DialogRenderer(table.getDefaultRenderer(Object.class)));

		model.addMultiTypeListener(new SetSelectionOnNewInsert());
		
		table.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				if (table.getSelectedRow() < 0) {
					table.changeSelection(0, 0, false, false);
				}
				table.removeFocusListener(this);
			}
		});
		
		// Create scroll pane.
		JScrollPane tableScrollPanel = new JScrollPane(table);
		add(topPanel, BorderLayout.NORTH);
		add(tableScrollPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Helper method to register the up/down actions.
	 * 
	 * @param component
	 */
	private void registerActions(JComponent component) {
		ActionMap actionMap = component.getActionMap();
		actionMap.put(swapUpAction.getValue(Action.ACTION_COMMAND_KEY), 
				swapUpAction);
		actionMap.put(swapDownAction.getValue(Action.ACTION_COMMAND_KEY), 
				swapDownAction);
		
		// register alt up/down keys for swap action.
		InputMap inputMap = component.getInputMap(
				JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put((KeyStroke) swapUpAction.getValue(Action.ACCELERATOR_KEY), 
				swapUpAction.getValue(Action.ACTION_COMMAND_KEY));
		inputMap.put((KeyStroke) swapDownAction.getValue(Action.ACCELERATOR_KEY), 
				swapDownAction.getValue(Action.ACTION_COMMAND_KEY));

	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		if (label != null) {
			label.setEnabled(enabled);
		}
		table.setEnabled(enabled);
	}

	/**
	 * Set the selected row. Uses by test.
	 * 
	 * @param selectedRow The row index.
	 */
	public void setSelectedRow(int selectedRow) {
		table.setRowSelectionInterval(selectedRow, selectedRow);
	}

	/**
	 * Get the selected row. Used by tests.
	 * 
	 * @return The row index.
	 */
	public int getSelectedRow() {
		return table.getSelectedRow();
	}
	
	/**
	 * Set the title.
	 * 
	 * @param title The title.
	 */
	public void setTitle(String title) {
		label.setText(title);
	}
	
	/**
	 * Set the number of visible rows in the widgets scroll area.
	 * 
	 * @param visibleRows the number of rows.
	 */
	public void setVisibleRows(int visibleRows) {
	    int height = 0; 
	    
	    for(int row = 0; row < visibleRows; row++) 
	        height += table.getRowHeight(row); 
	 
	    table.setPreferredScrollableViewportSize(new Dimension( 
	            (int) table.getPreferredSize().getWidth(),
	            height)); 
	}
	
	/**
	 * When a new type is created as the last row, the selection would
	 * normally move down. This moves it back to next to what was
	 * inserted which is more convenient.
	 * 
	 * @author rob
	 *
	 */
	class SetSelectionOnNewInsert implements MultiTypeListener {
		@Override
		public void rowChanged(MultiTypeEvent event) {}
		@Override
		public void rowRemoved(MultiTypeEvent event) {}
		@Override
		public void rowInserted(MultiTypeEvent event) {
			if (event.getRow() == model.getRowCount() - 1) {
				table.changeSelection(event.getRow(), 1, false, false);
			}
		}
	}	
	
	/**
	 * Listen to selection events and enable and disable up/down 
	 * actions.
	 */
	class TableSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			int rowIndex = table.getSelectedRow();
			
			if (rowIndex > -1 && rowIndex < model.getRowCount() -1) {
				swapDownAction.setEnabled(true);
			}
			else {
				swapDownAction.setEnabled(false);
			}
			
			if (rowIndex > 0 && rowIndex < model.getRowCount()) {
				swapUpAction.setEnabled(true);
			}
			else {
				swapUpAction.setEnabled(false);
			}
		}		
	}
	
	/**
	 * The Action the moves the selection up.
	 */
	class SwapUp extends AbstractAction {
		private static final long serialVersionUID = 2012042700L;
		
		public SwapUp() {
			super("Up");
			putValue(SHORT_DESCRIPTION, "Swap selection upwards");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_UP, ActionEvent.ALT_MASK));
			putValue(ACTION_COMMAND_KEY, "swapUp");
			setEnabled(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int rowIndex = table.getSelectedRow();
			
			TableCellEditor editor = table.getCellEditor();
			if (editor != null) {
				editor.stopCellEditing();
			}
			
			model.swapRow(rowIndex, -1);
			table.setRowSelectionInterval(
					rowIndex - 1, rowIndex - 1);
		}		
	}
	
	/**
	 * The action that moves the selection down.
	 */
	class SwapDown extends AbstractAction {
		private static final long serialVersionUID = 2012042700L;

		public SwapDown() {
			super("Down");
			putValue(SHORT_DESCRIPTION, "Swap selection downwards");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(
					KeyEvent.VK_DOWN, ActionEvent.ALT_MASK));
			putValue(ACTION_COMMAND_KEY, "swapDown");
			setEnabled(false);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int rowIndex = table.getSelectedRow();
			
			TableCellEditor editor = table.getCellEditor();
			if (editor != null) {
				editor.stopCellEditing();
			}
			
			model.swapRow(rowIndex, +1);
			table.setRowSelectionInterval(
					rowIndex + 1, rowIndex + 1);
		}		
	}
	
	/**
	 * The editor for the value.
	 */
	public class DialogEditor extends AbstractCellEditor 
	implements TableCellEditor {
		private static final long serialVersionUID = 20081008;
		
		private EditableValue editableValue;
		
		/*
		 * (non-Javadoc)
		 * @see javax.swing.CellEditor#getCellEditorValue()
		 */
		public Object getCellEditorValue() {
			// This is null because it is ignored by the model.
			return null;
		}

		@Override
		public boolean stopCellEditing() {
			editableValue.commit();
			return super.stopCellEditing();
		}
		
		@Override
		public void cancelCellEditing() {
			editableValue.abort();
			super.cancelCellEditing();
		}
		
		//Implement the one method defined by TableCellEditor.
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			editableValue = (EditableValue) value;
			return editableValue.getEditor();
		}
	}

	/**
	 * The renderer for the value.
	 */
	class DialogRenderer implements TableCellRenderer {
		private final TableCellRenderer defaultRenderer;
		
		public DialogRenderer(TableCellRenderer defaultRenderer) {
			this.defaultRenderer = defaultRenderer;			
		}
		
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (row == model.getRowCount() || value == null) {
				return defaultRenderer.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);
			}
			else {
				Component component = ((EditableValue) value).getEditor();
				JPanel panel = new JPanel(new BorderLayout());
				panel.add(component, BorderLayout.CENTER);
				if (isSelected) {
					panel.setBorder(new LineBorder(table.getSelectionBackground()));
				}
				if (hasFocus) {
					panel.setBorder(new LineBorder(table.getSelectionForeground()));
				}
				return panel;
			}
		}
	}	
}
