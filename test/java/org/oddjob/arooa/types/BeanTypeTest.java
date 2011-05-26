package org.oddjob.arooa.types;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.SimpleDesignProperty;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class BeanTypeTest extends XMLTestCase {
	
	static final String EOL = System.getProperty("line.separator");
	
	DesignInstance design;
	
	public void testCreateAndParse() throws Exception {
		
		DesignParser parser = new DesignParser(
				new StandardArooaSession());
		
		parser.parse(new XMLConfiguration("TEST", "<bean/>"));
		
		UnknownInstance design = (UnknownInstance) parser.getDesign();
		
		String xml = 
			"<bean class=\"some.Class\">" + EOL + 
			"    <stuff>" + EOL +
			"        <value value=\"Some Stuff\"/>" + EOL +
			"    </stuff>" + EOL +
			"</bean>" + EOL;
			
		design.setXml(xml);
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		xmlParser.parse(design.getArooaContext().getConfigurationNode());
		
		assertXMLEqual(xml, xmlParser.getXml());
	}
	
	public class SnackDesignFactory implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new SnackDesign(element, parentContext);
		}
	}
	
	public class SnackDesign extends DesignValueBase {
		
		SimpleDesignProperty fruit; 
		
		public SnackDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, parentContext);
			
			fruit = new SimpleDesignProperty(
						"fruit", 
						Object.class, 
						ArooaType.VALUE, 
						this);
		}
		
		@Override
		protected DesignProperty[] children() {
			return new DesignProperty[] { fruit };
		}
		
		public Form detail() {
			return new StandardForm("test", this)
			.addFormItem(fruit.view());
		}
	}
	
	public void testDesign() throws Exception {
	
		DesignParser parser = new DesignParser(
				new StandardArooaSession(),
				new BeanType.ClassDesignFactory());
		
		parser.parse(new XMLConfiguration("TEST", "<bean/>"));
		
		design = parser.getDesign();
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		xmlParser.parse(design.getArooaContext().getConfigurationNode());
		
		assertXMLEqual("<bean/>" + EOL, xmlParser.getXml());
		
	}
	
	public static void main(String args[]) throws Exception {

		BeanTypeTest test = new BeanTypeTest();
		test.testDesign();
		
		ViewMainHelper helper = new ViewMainHelper(test.design);
		helper.run();
	}
	
	public void testBeanExample() throws ArooaParseException {
		
		StandardFragmentParser parser = new StandardFragmentParser();
		
		parser.parse(new XMLConfiguration(
				"org/oddjob/arooa/types/BeanExample.xml",
				getClass().getClassLoader()));
				
		PersonBean person = (PersonBean) parser.getRoot();
		
		assertEquals("John", person.getName());

		String[] friends = person.getFriends();
		
		assertEquals("Rod", friends[0]);
		assertEquals("Jane", friends[1]);
		assertEquals("Freddy", friends[2]);
		
		assertEquals(3, friends.length);
		
	}
}
