package org.oddjob.arooa.design.view.multitype;

import org.junit.Test;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.junit.Assert;

import org.oddjob.arooa.design.view.FileSelectionWidget;

public class KeyedMultiTypeAdaptorTest extends Assert {

	private static final Integer DELETE_OPTION = new Integer(0);
	
	private static class MyEditableValue implements EditableValue {
		FileSelectionWidget widget = new FileSelectionWidget();
		String file;
		
		@Override
		public Component getEditor() {
			widget.setSelectedFile(file);
			return widget;
		}

		@Override
		public void commit() {
			file = widget.getSelectedFile();
		}
		
		@Override
		public void abort() {
		}
	}
	
	private static class MyMultiTypeTableRow implements MultiTypeRow {

		Object type;
		EditableValue value;
		String name;
		
		public MyMultiTypeTableRow(Integer type) {
			this.type = type;
			this.value = new MyEditableValue();
		}
		
		@Override
		public Object getType() {
			return type;
		}
		
		@Override
		public void setType(Object type) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public EditableValue getValue() {
			return value;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public void setName(String name) {
			this.name= name;
		}
	}
	
	private static class MyMultiTypeTableModel
	extends AbstractMultiTypeModel {
				
		List<MultiTypeRow> rows = 
				new ArrayList<MultiTypeRow>();
		
		@Override
		public void createRow(
				Object creator, int rowIndex) {
			MultiTypeRow row = new MyMultiTypeTableRow((Integer) creator);
			this.rows.add(rowIndex, row);
			fireRowInserted(rowIndex);
		}
		
		@Override
		public MultiTypeRow getRow(int index) {
			return rows.get(index);
		}
		
		@Override
		public void swapRow(int from, int direction) {
			MultiTypeRow row = rows.remove(from);
			fireRowRemoved(from);
			int to = from+direction;
			rows.add(to, row);
			fireRowInserted(to);
		}
		
		@Override
		public void removeRow(int rowIndex) {
			this.rows.remove(rowIndex);
			fireRowRemoved(rowIndex);
		}

		@Override
		public Integer[] getTypeOptions() {
			return new Integer[] { 1, 2, 3, 4, 5 };
		}

		@Override
		public Object getDeleteOption() {
			return DELETE_OPTION;
		}
		
		@Override
		public int getRowCount() {
			return rows.size();
		}
	}
	
   @Test
	public void testInsertSwapDelete() {

		MyMultiTypeTableModel model = new MyMultiTypeTableModel();
		
		KeyedMultiTypeAdaptor test = new KeyedMultiTypeAdaptor(model);

		assertEquals(3, test.getColumnCount());
		
		assertEquals(1, test.getRowCount());
		assertNull(test.getValueAt(0, 0));
		assertNull(test.getValueAt(0, 1));
		assertNull(test.getValueAt(0, 2));
		assertTrue(test.isCellEditable(0, 0));
		assertFalse(test.isCellEditable(0, 1));
		assertFalse(test.isCellEditable(0, 2));
		
		// one row
		test.setValueAt(new Integer(1), 0, 0);
		
		assertEquals(2, test.getRowCount());
		assertEquals(new Integer(1), test.getValueAt(0, 0));
		assertEquals(null, test.getValueAt(0, 1));
		assertTrue(test.getValueAt(0, 2) instanceof EditableValue);
		assertTrue(test.isCellEditable(0, 0));
		assertTrue(test.isCellEditable(0, 1));
		assertTrue(test.isCellEditable(0, 2));

		// set name
		test.setValueAt("apple", 0, 1);
		assertEquals("apple", test.getValueAt(0, 1));
		
		// add another row
		test.setValueAt(new Integer(2), 1, 0);
		test.setValueAt("orange", 1, 1);
		
		assertEquals(3, test.getRowCount());
		assertEquals(new Integer(2), test.getValueAt(1, 0));
		assertEquals("orange", test.getValueAt(1, 1));
		assertTrue(test.getValueAt(1, 2) instanceof EditableValue);
		
		// swap up
		model.swapRow(0, +1);
		assertEquals(new Integer(1), test.getValueAt(1, 0));
		assertEquals("apple", test.getValueAt(1, 1));
		assertEquals(new Integer(2), test.getValueAt(0, 0));
		assertEquals("orange", test.getValueAt(0, 1));
		
		// swap back
		model.swapRow(1, -1);
		assertEquals(new Integer(1), test.getValueAt(0, 0));
		assertEquals("apple", test.getValueAt(0, 1));
		assertEquals(new Integer(2), test.getValueAt(1, 0));
		assertEquals("orange", test.getValueAt(1, 1));
		
		// delete row
		test.setValueAt(model.getDeleteOption(), 0, 0);
		assertEquals(2, test.getRowCount());
		assertEquals(1, model.getRowCount());
		assertEquals(new Integer(2), test.getValueAt(0, 0));
		assertEquals("orange", test.getValueAt(0, 1));
	}

   @Test
	public void testTreeModelEvents() {

		final List<TableModelEvent> results = 
				new ArrayList<TableModelEvent>();
		
		MyMultiTypeTableModel model = new MyMultiTypeTableModel();
	
		KeyedMultiTypeAdaptor test = new KeyedMultiTypeAdaptor(model);
		
		test.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				results.add(e);
			}
		});
		
		TableModelEvent event;
		
		assertEquals(0, results.size());
				
		// one row
		test.setValueAt(new Integer(1), 0, 0);
		
		assertEquals(1, results.size());
		
		event = results.get(0);
		assertEquals(event.getColumn(), TableModelEvent.ALL_COLUMNS);
		assertEquals(0, event.getFirstRow());
		assertEquals(0, event.getLastRow());
		assertEquals(TableModelEvent.INSERT, event.getType());
		
		// set name
		test.setValueAt("apple", 0, 1);

		// Todo: should fire event here;
		
		// add another row		
		test.setValueAt(new Integer(2), 1, 0);
		
		assertEquals(2, results.size());
		
		event = results.get(1);
		assertEquals(1, event.getFirstRow());
		assertEquals(1, event.getLastRow());
		assertEquals(TableModelEvent.INSERT, event.getType());
		
		// swap up
		model.swapRow(0, +1);
		
		assertEquals(4, results.size());
		
		event = results.get(2);
		assertEquals(0, event.getFirstRow());
		assertEquals(0, event.getLastRow());
		assertEquals(TableModelEvent.DELETE, event.getType());
		event = results.get(3);
		assertEquals(1, event.getFirstRow());
		assertEquals(1, event.getLastRow());
		assertEquals(TableModelEvent.INSERT, event.getType());
		
		// swap back
		model.swapRow(1, -1);
		
		assertEquals(6, results.size());
		
		event = results.get(4);
		assertEquals(1, event.getFirstRow());
		assertEquals(1, event.getLastRow());
		assertEquals(TableModelEvent.DELETE, event.getType());
		event = results.get(5);
		assertEquals(0, event.getFirstRow());
		assertEquals(0, event.getLastRow());
		assertEquals(TableModelEvent.INSERT, event.getType());
		
		// delete row
		test.setValueAt(model.getDeleteOption(), 0, 0);
				
		assertEquals(7, results.size());
		
		event = results.get(6);
		assertEquals(0, event.getFirstRow());
		assertEquals(0, event.getLastRow());
		assertEquals(TableModelEvent.DELETE, event.getType());
	}
	
	public static void main(String... args) {
		
		MyMultiTypeTableModel model = new MyMultiTypeTableModel();
		
		model.createRow(new Integer(1), 0);
		model.createRow(new Integer(2), 0);
		model.createRow(new Integer(3), 0);
		
		MultiTypeTableWidget test = new MultiTypeTableWidget(model,
				MultiTypeStrategy.Strategies.KEYED);
		test.setVisibleRows(7);

		JFrame frame = new JFrame();
		
		JPanel panel = new JPanel();
		panel.add(test);
		panel.add(new JButton("Whatever"));
				
		frame.getContentPane().add(panel);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		frame.pack();
		frame.setVisible(true);
		
	}
}
