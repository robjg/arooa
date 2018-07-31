package org.oddjob.arooa.design.view.multitype;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import javax.swing.table.TableModel;

import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.IndexedDesignProperty;
import org.oddjob.arooa.design.MappedDesignProperty;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.MultiTypeTable;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.parsing.MockArooaHandler;
import org.oddjob.arooa.parsing.QTag;
import org.oddjob.arooa.parsing.RootContext;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.types.BeanType;
import org.oddjob.arooa.types.ValueType;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class MultiTypeDesignModelTest {
	
	
	class MyDesign implements DesignInstance {
		
		ArooaContext context;
		
		public MyDesign() {
			StandardArooaSession session = new StandardArooaSession();
			context = new RootContext(
					ArooaType.VALUE, session, new MockArooaHandler());
		}		
		@Override
		public Form detail() {
			throw new RuntimeException("Unexpected!");
		}
		@Override
		public ArooaElement element() {
			throw new RuntimeException("Unexpected!");
		}
		public ArooaContext getArooaContext() {
			return context;
		}
	}
	
	QTag valueTag = new QTag("", ValueType.ELEMENT);
	QTag beanTag = new QTag("", BeanType.ELEMENT);
	
	/**
	 * Test inserting and changing cells in the table.
	 * @throws ArooaParseException 
	 */
   @Test
	public void testInsertChangeDeleteList() throws ArooaParseException {
		
		IndexedDesignProperty property = new IndexedDesignProperty(
				"fruit", String.class, ArooaType.VALUE, new MyDesign()); 
		
		MultiTypeTable multiTable = (MultiTypeTable) property.view();
		
		MultiTypeDesignModel test = new MultiTypeDesignModel(multiTable);

		CutAndPasteSupport.paste(property.getArooaContext(), 0, 
				new XMLConfiguration("XML", "<value value='apple'/>"));
		
		TableModel table = MultiTypeStrategy.Strategies.LIST.tableModelFor(
				test);
		
		assertEquals(2, table.getColumnCount());
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt(valueTag, 1, 0);
		
		assertEquals(2, property.instanceCount());
		
		table.setValueAt(valueTag, 2, 0);
		
		assertEquals(3, property.instanceCount());

		table.setValueAt(MultiTypeDesignModel.NULL_TAG, 1, 0);
		
		assertEquals(2, property.instanceCount());
		
		table.setValueAt(MultiTypeDesignModel.NULL_TAG, 0, 0);
		
		assertEquals(1, property.instanceCount());
	}

	String EOL = System.getProperty("line.separator");
	
   @Test
	public void testMapped() throws Exception {
		
		MappedDesignProperty property = new MappedDesignProperty(
				"fruit", String.class, ArooaType.VALUE, new MyDesign()); 
		
		MultiTypeTable multiTable = (MultiTypeTable) property.view();;
		
		MultiTypeDesignModel test = new MultiTypeDesignModel(multiTable);
		
		TableModel table = MultiTypeStrategy.Strategies.KEYED.tableModelFor(
				test);
		
		table.setValueAt(valueTag, 0, 0);
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt("morning", 0, 1);

		XMLArooaParser parser = new XMLArooaParser();
		
		parser.parse(property.getArooaContext().getConfigurationNode());

		String expected = 
				"<fruit>" + EOL +
				"    <value key=\"morning\"/>" + EOL +
				"</fruit>" + EOL; 
		
		assertThat(parser.getXml(), isSimilarTo(expected));
				
		table.setValueAt(beanTag, 0, 0);
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt(valueTag, 1, 0);
		
		assertEquals(2, property.instanceCount());

		table.setValueAt(MultiTypeDesignModel.NULL_TAG, 1, 0);
		
		assertEquals(1, property.instanceCount());
		
		table.setValueAt(MultiTypeDesignModel.NULL_TAG, 0, 0);
		
		assertEquals(0, property.instanceCount());
	}
	
   @Test
	public void testSwap() {
	
		MappedDesignProperty designProperty = 
				new MappedDesignProperty("fruit", String.class, 
						ArooaType.VALUE, new MyDesign());
		
		MultiTypeTable multiTypeTable = designProperty.view();
		
		MultiTypeDesignModel test = new MultiTypeDesignModel(multiTypeTable);
				
		test.createRow(valueTag, 0);
		
		MultiTypeRow row = test.getRow(0);
		row.setName("a");
		
		test.createRow(valueTag, 1);
		row = test.getRow(1);
		row.setName("b");
		
		test.swapRow(1, -1);
		
		row = test.getRow(0);
		
		assertEquals("b", row.getName());
		
		row = test.getRow(1);
		
		assertEquals("a", row.getName());
	}
			
}
