package org.oddjob.arooa.design;

import org.junit.Test;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class IndexedDesignPropertyTest {

	class MyDesign extends DesignValueBase {
		
		private final IndexedDesignProperty test;
		
		public MyDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Object.class), parentContext);
			
			test = new IndexedDesignProperty(
					"test", Object.class, ArooaType.VALUE, this);
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { test };
		}
		
		public Form detail() {
			return new StandardForm("Our Test", this)
			.addFormItem(test.view());
		}
	}
		
	DesignInstance design;
	
	String EOL = System.getProperty("line.separator");
	
   @Test
	public void testCreateInstance() throws Exception {
		
		ArooaSession session = new StandardArooaSession();
		
		ArooaElement element = new ArooaElement("value");
		element = element.addAttribute("key", "apple");
		element = element.addAttribute("value", "cox");

		MyDesign design = new MyDesign(new ArooaElement("root"),
				new DesignSeedContext(ArooaType.VALUE, session));
		
		IndexedDesignProperty test = 
			(IndexedDesignProperty) design.children()[0]; 
		
		ArooaContext instanceContext = test.getArooaContext().getArooaHandler().onStartElement(
				element, test.getArooaContext());

		test.getArooaContext().getConfigurationNode().insertChild(
				instanceContext.getConfigurationNode());
		
		instanceContext.getRuntime().init();
		
		assertEquals(1, test.instanceCount());
		
		String expected = 
			"<root>" + EOL +
			"    <test>" + EOL +
			"        <value value=\"cox\"/>" + EOL + 
			"    </test>" + EOL +
			"</root>" + EOL;
		
		XMLArooaParser xmlParser = new XMLArooaParser(session.getArooaDescriptor());
		xmlParser.parse(design.getArooaContext().getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isSimilarTo(expected));
		
		this.design = design;
	}
	
	public static void main(String args[]) throws Exception {

		IndexedDesignPropertyTest test = new IndexedDesignPropertyTest();
		test.testCreateInstance();

		ViewMainHelper view = new ViewMainHelper(test.design);
		view.run();
	}
	
}
