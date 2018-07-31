package org.oddjob.arooa.design.view.multitype;

import org.junit.Test;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.junit.Assert;

import org.oddjob.arooa.design.view.FileSelectionWidget;

public class NamedMultiTypeAdaptorTest extends Assert {

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
	
	private static class MyMultiTypeTableModel
	extends AbstractMultiTypeModel {
				
		List<MultiTypeRow> rows = 
				new ArrayList<MultiTypeRow>();
		
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

		@Override
		public void createRow(
				Object creator, int rowIndex) {
			NamedRow row = new NamedRow((String) creator);
			this.rows.add(rowIndex, row);
			fireRowInserted(rowIndex);
		}
		
		class NamedRow implements MultiTypeRow {
			
			String name;
			
			Object type;
			
			EditableValue value;
			
			NamedRow(String name) {
				this.name = name;
			}
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public void setName(String name) {
				this.name = name;
			}
			
			@Override
			public void setType(Object type) {
				this.type = type;
				if (type == DELETE_OPTION) {
					value = null;
				}
				else {
					value = new MyEditableValue();
				}
				int index = rows.indexOf(this);
				fireRowChanged(index);
			}
			
			@Override
			public Object getType() {
				return type;
			}
			
			@Override
			public EditableValue getValue() {
				return value;
			}
			
		}
	}	
	
			
   @Test
	public void testNameFirstModel() {

		
		MyMultiTypeTableModel model = new MyMultiTypeTableModel();
		
		NamedMultiTypeAdaptor test = new NamedMultiTypeAdaptor(model);

		assertEquals(3, test.getColumnCount());
		
		assertEquals(1, test.getRowCount());
		
		assertNull(test.getValueAt(0, 0));
		assertNull(test.getValueAt(0, 1));
		assertNull(test.getValueAt(0, 2));
		assertTrue(test.isCellEditable(0, 0));
		assertFalse(test.isCellEditable(0, 1));
		assertFalse(test.isCellEditable(0, 2));
		
		// Insert a row
		test.setValueAt("myThing", 0, 0);
		
		assertEquals("myThing", test.getValueAt(0, 0));
		assertNull(test.getValueAt(0, 1));
		assertNull(test.getValueAt(0, 2));
		assertTrue(test.isCellEditable(0, 0));
		assertTrue(test.isCellEditable(0, 1));
		assertFalse(test.isCellEditable(0, 2));
		
		// Set the type
		test.setValueAt(new Integer(5), 0, 1);
		
		assertEquals(new Integer(5), test.getValueAt(0, 1));
		assertNotNull(test.getValueAt(0, 2));
		assertTrue(test.isCellEditable(0, 2));
		
		// Insert another row
		test.setValueAt("anotherThing", 1, 0);
		
		assertEquals("anotherThing", test.getValueAt(1, 0));
		assertNull(test.getValueAt(1, 1));
		assertNull(test.getValueAt(1, 2));
		assertTrue(test.isCellEditable(1, 0));
		assertTrue(test.isCellEditable(1, 1));
		assertFalse(test.isCellEditable(1, 2));		
		
		model.swapRow(0, +1);
		
		assertEquals("anotherThing", test.getValueAt(0, 0));
		assertNull(test.getValueAt(0, 1));
		assertNull(test.getValueAt(0, 2));
		assertEquals("myThing", test.getValueAt(1, 0));
		assertEquals(new Integer(5), test.getValueAt(1, 1));
		assertNotNull(test.getValueAt(1, 2));
		
	}
	
	public static void main(String... args) {
		
		MyMultiTypeTableModel model = new MyMultiTypeTableModel();
		
		MultiTypeTableWidget test = new MultiTypeTableWidget(model,
				MultiTypeStrategy.Strategies.NAMED);
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
