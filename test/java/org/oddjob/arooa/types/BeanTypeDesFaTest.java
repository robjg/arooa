package org.oddjob.arooa.types;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.DesignElementProperty;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignListener;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignStructureEvent;
import org.oddjob.arooa.design.DynamicDesignInstance;
import org.oddjob.arooa.design.GenericDesignFactory;
import org.oddjob.arooa.design.MappedDesignProperty;
import org.oddjob.arooa.design.ParsableDesignInstance;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.xml.sax.SAXException;

public class BeanTypeDesFaTest {

	static final String EOL = System.getProperty("line.separator");
	
	private static final Logger logger = 
			Logger.getLogger(BeanTypeDesFa.class);
	
	@Rule public TestName name = new TestName();

	public String getName() {
        return name.getMethodName();
    }

	DesignInstance design;
	
   @Before
   public void setUp() throws Exception {
		
		logger.info("-----------------------------------  " +
				getName() + "  ----------------------------------");
	}
	
   @Test
	public void testDesign() throws Exception {
		
		DesignParser parser = new DesignParser(
				new StandardArooaSession(),
				new BeanTypeDesFa());
		
		String xml = 
				"<bean class='org.oddjob.arooa.deploy.ArooaDescriptorBean'>" +
				" <components>" +
				"  <is/>" +
				" </components>" +
				"</bean>";
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		design = parser.getDesign();
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		xmlParser.parse(design.getArooaContext().getConfigurationNode());
		
		String expected = 
				"<bean class='org.oddjob.arooa.deploy.ArooaDescriptorBean'>" + EOL +
				"    <components>" + EOL +
				"        <is/>" + EOL +
				"    </components>" + EOL +
				"</bean>" + EOL;
		
		
		
		assertThat(xmlParser.getXml(), isSimilarTo(expected));
		
	}
	
	
   @Test
	public void testDesignComponent() throws Exception {
		
		DesignParser parser = new DesignParser(
				new StandardArooaSession(),
				new BeanTypeDesFa());
		
		parser.setArooaType(ArooaType.COMPONENT);
		
		String xml = 
				"<bean class='org.oddjob.arooa.deploy.ArooaDescriptorBean'" +
				"      id='mybean'>" +
				"    <components>" +
				"        <is/>" +
				"    </components>" +
				"</bean>";
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		design = parser.getDesign();
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		xmlParser.parse(design.getArooaContext().getConfigurationNode());
		
		String expected = 
				"<bean class='org.oddjob.arooa.deploy.ArooaDescriptorBean'" +
				"      id='mybean'>" + EOL +
				"    <components>" + EOL +
				"        <is/>" + EOL +
				"    </components>" + EOL +
				"</bean>" + EOL;
		
		assertThat(xmlParser.getXml(), isSimilarTo(expected));
	}
	
    @Test
	public void testNoSettableProperties() throws Exception {
		
		DesignParser parser = new DesignParser(
				new StandardArooaSession(),
				new BeanTypeDesFa());
		
		parser.setArooaType(ArooaType.COMPONENT);
		
		String xml = 
				"<bean class='java.lang.Object'" +
				"      id='mybean'/>";
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		design = parser.getDesign();
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		xmlParser.parse(design.getArooaContext().getConfigurationNode());
		
		String expected = 
				"<bean class='java.lang.Object'" + EOL +
				"      id='mybean'/>" + EOL;
		
		assertThat(xmlParser.getXml(), isSimilarTo(expected));
	}
	
	public static class Stuff {
		
		public void setThings(String key, Object thing) {}
		
	}
	
	private class OurListener implements DesignListener {
		List<DesignInstance> children = new ArrayList<DesignInstance>();
		
		public void childAdded(DesignStructureEvent event) {
			children.add(event.getIndex(), event.getChild());
		}
		
		public void childRemoved(DesignStructureEvent event) {
			children.remove(event.getIndex());
		}
	}	
	
    @Test
	public void testMappedProperties() throws ArooaParseException, SAXException, IOException {
		
		String xml =
				"<stuff>" +
				" <things>" +
				"  <bean key='something' class='java.lang.Object'/>" +
				" </things>" +
				"</stuff>";
		
		DesignParser parser = new DesignParser(new GenericDesignFactory(
				new SimpleArooaClass(Stuff.class)));
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ParsableDesignInstance top = 
				(ParsableDesignInstance) parser.getDesign();
		
		DesignElementProperty things = 
				(DesignElementProperty) top.children()[0];
		
		OurListener ourListener = new OurListener();
		
		things.addDesignListener(ourListener);
		
		MappedDesignProperty.InstanceWrapper wrapper = 
				(MappedDesignProperty.InstanceWrapper) 
				ourListener.children.get(0);
		
		DynamicDesignInstance test = (DynamicDesignInstance) 
				wrapper.getWrapping();
		
		test.setClassName("java.lang.String");
		
		String expected =
				"<stuff>" + EOL +
				"    <things>" + EOL +
				"        <bean key='something' class='java.lang.String'/>" + EOL +
				"    </things>" + EOL +
				"</stuff>" + EOL;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		xmlParser.parse(top.getArooaContext().getConfigurationNode());

		String actual = xmlParser.getXml();
		
		logger.info(actual);
		
		assertThat(actual, isSimilarTo(expected));
	}
	
	
	public static void main(String args[]) throws Exception {

		BeanTypeDesFaTest test = new BeanTypeDesFaTest();
		test.testNoSettableProperties();
		
		ViewMainHelper helper = new ViewMainHelper(test.design);
		helper.run();
	}
}
