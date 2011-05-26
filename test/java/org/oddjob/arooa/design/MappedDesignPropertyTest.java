package org.oddjob.arooa.design;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class MappedDesignPropertyTest extends XMLTestCase {

	public static class MyBean {
		
		public void setTest(String key, Object value) {}
	}
	
	
	class MyDesFa implements DesignFactory {
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new MyDesign(element, parentContext);
		}
	}
	
	class MyDesign extends DesignValueBase {
		
		private final MappedDesignProperty test;
		
		public MyDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Object.class), parentContext);
			
			test = new MappedDesignProperty(
					"test", Object.class, ArooaType.VALUE, this);
		}
		
		@Override
		protected DesignProperty[] children() {
			return new DesignProperty[] { test };
		}
		
		public Form detail() {
			return new StandardForm("Our Test", this)
			.addFormItem(test.view());
		}
	}
		
	DesignInstance design;
	
	String EOL = System.getProperty("line.separator");
	
	public void testCreateInstance() throws Exception {
		
		String xml = 
			"<root>" + EOL +
			"    <test>" + EOL +
			"        <value value=\"cox\"" + EOL + 
			"               key=\"apple\"/>" + EOL + 
			"    </test>" + EOL +
			"</root>" + EOL;
		
		ArooaSession session = new StandardArooaSession();
		
		DesignParser parser = new DesignParser(session, new MyDesFa());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		MyDesign design = (MyDesign) parser.getDesign();
		
		MappedDesignProperty test = 
			(MappedDesignProperty) design.children()[0]; 
		
		assertEquals(1, test.instanceCount());
		
		DesignInstance instance = ((MappedDesignProperty.InstanceWrapper)
				test.instanceAt(0)).getWrapping();
		
		assertFalse(instance instanceof Unknown);
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		xmlParser.parse(design.getArooaContext().getConfigurationNode());
		
		assertXMLEqual(xml, xmlParser.getXml());
		
		this.design = design;
	}
	
	public static void main(String args[]) throws Exception {

		MappedDesignPropertyTest test = new MappedDesignPropertyTest();
		test.testCreateInstance();
		
		ViewMainHelper view = new ViewMainHelper(test.design);
		view.run();
	}
	
}
