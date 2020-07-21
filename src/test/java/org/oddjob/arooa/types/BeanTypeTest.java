package org.oddjob.arooa.types;

import org.junit.Test;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class BeanTypeTest {
	
	static final String EOL = System.getProperty("line.separator");
	
   @Test
	public void testCreateAndParse() throws Exception {
		
		DesignParser parser = new DesignParser(
				new StandardArooaSession());
		
		parser.parse(new XMLConfiguration("TEST", "<idontexist/>"));
		
		UnknownInstance design = (UnknownInstance) parser.getDesign();
		
		String xml = 
			"<bean class=\"some.Class\">" + EOL + 
			"    <stuff>" + EOL +
			"        <value value=\"Some Stuff\"/>" + EOL +
			"    </stuff>" + EOL +
			"</bean>" + EOL;
			
		design.setXml(xml);
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
		
		xmlParser.parse(design.getArooaContext().getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isSimilarTo(xml));
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
		public DesignProperty[] children() {
			return new DesignProperty[] { fruit };
		}
		
		public Form detail() {
			return new StandardForm("test", this)
			.addFormItem(fruit.view());
		}
	}
	
   @Test
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
	
	// Class can't be a runtime property!!!
   @Test
	public void testClassAtRuntime() throws ArooaParseException {
		
		String xml = 
				"<bean class='${the-class}' name='John'/>";

		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("the-class", PersonBean.class);
		
		StandardFragmentParser parser = new StandardFragmentParser();
		
		try {
			parser.parse(new XMLConfiguration("TEST", xml));
		}
		catch (ArooaParseException e) {
			ArooaException e2 = (ArooaException) e.getCause();
			assertEquals("Can't find class ${the-class}", e2.getMessage());
		}
	}
}
