package org.oddjob.arooa.types;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignSeedContext;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.view.SwingFormFactory;
import org.oddjob.arooa.design.view.SwingFormView;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.xml.sax.SAXException;

public class ValueTypeDesignTest extends XMLTestCase {
	private static final Logger logger = Logger.getLogger(ValueTypeDesignTest.class);
	
	Component comp; 

	DesignInstance design;
	
	public void testChangingCellText() 
	throws BadLocationException, ArooaParseException, 
			SAXException, IOException {
	
		XMLUnit.setIgnoreWhitespace(true);
		
		DesignFactory desFa = new ValueType.ValueDesignFactory();
		
		ArooaContext context = new DesignSeedContext(
				ArooaType.VALUE, new StandardArooaSession());
		
		ArooaElement element = new ArooaElement(
				"value").addAttribute("value", "Some Text");
		
		design = desFa.createDesign(
				element, context);
		
		Form form = design.detail();
		
		SwingFormView view = SwingFormFactory.create(form);
		
		comp = view.cell();
		
		assertTrue(comp instanceof JTextField);
		
		final JTextField text = (JTextField) comp;
		
		assertEquals("Some Text", text.getText());
		
		text.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				logger.info("Change: " + text.getText());
			}
			public void removeUpdate(DocumentEvent e) {
				logger.info("Remove: " + text.getText());
			}
			public void insertUpdate(DocumentEvent e) {
				logger.info("Insert: " + text.getText());
			}
		});		
		
		text.getDocument().insertString(5, "More ", null);
		
		XMLArooaParser parser = new XMLArooaParser();
		parser.parse(design.getArooaContext().getConfigurationNode());
		
		assertXMLEqual("<value value='Some More Text'/>", parser.getXml());
		
		
		text.setText("This Text");
		
		parser.parse(design.getArooaContext().getConfigurationNode());
		
		assertXMLEqual("<value value='This Text'/>", parser.getXml());		
		
		text.getDocument().remove(4, 5);
		
		parser.parse(design.getArooaContext().getConfigurationNode());
		
		assertXMLEqual("<value value='This'/>", parser.getXml());		
	}
	
	public static void  main(String[] args) throws Exception {

		ValueTypeDesignTest test = new ValueTypeDesignTest();
		test.testChangingCellText();
		
		ViewMainHelper helper = new ViewMainHelper(test.design);
		helper.run();
	}
}
