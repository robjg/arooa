package org.oddjob.arooa.types;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.DesignProperty;
import org.oddjob.arooa.design.DesignValueBase;
import org.oddjob.arooa.design.SimpleDesignProperty;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class XMLTypeTest extends XMLTestCase {

	static final String EOL = System.getProperty("line.separator");
	
	public static class HasXMLProperty {
		
		String ourXml;
		
		public void setOurXml(String xml) {
			this.ourXml = xml;
		}
	}
	
	public static class HasXMLPropertyArooa extends MockArooaBeanDescriptor {
		
		@Override
		public ConfiguredHow getConfiguredHow(String property) {
			return ConfiguredHow.ELEMENT;
		}
		
		@Override
		public ParsingInterceptor getParsingInterceptor() {
			return null;
		}
		
		@Override
		public String getComponentProperty() {
			return null;
		}
		@Override
		public boolean isAuto(String property) {
			return false;
		}
	}
	
	public void testAsValue() throws Exception {
		
		String xml = 
			"<whatever>" +
			" <ourXml>" +
			"  <xml>" +
			"   <snack>" +
			"    <fruit>" +
			"     <apple/>" +
			"    </fruit>" +
			"   </snack>" +
			"  </xml>" +
			" </ourXml>" +
			"</whatever>";
		
		HasXMLProperty bean = new HasXMLProperty();
		
		StandardArooaParser parser = new StandardArooaParser(bean);
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(bean);
		
		String expected = 
			"<snack>" + EOL + 
			"    <fruit>" + EOL + 
			"        <apple/>" + EOL +
			"    </fruit>" + EOL +
			"</snack>" + EOL;
		
		assertXMLEqual(expected, bean.ourXml);

	}
	
	public void testMultipleDocElementsInXml() throws Exception {
		
		String xml = 
			"<whatever>" +
			" <ourXml>" +
			"  <xml>" +
			"   <apple/>" +
			"  </xml>" +
			" </ourXml>" +
			"</whatever>";
		
		HasXMLProperty bean = new HasXMLProperty();
		
		StandardArooaParser parser = new StandardArooaParser(bean);
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(bean);
		
		String expected = 
			"<apple/>";
		
		assertXMLEqual(expected, bean.ourXml);

	}
	
	public void testAsFragment() throws Exception {
		
		String xml = 
			"<xml>" +
			" <apples/>" +
			"</xml>";
		
		StandardFragmentParser parser = new StandardFragmentParser();
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		XMLType test = (XMLType) parser.getRoot();
		
		DefaultConverter converter = new DefaultConverter();
		
		String result = converter.convert(
				test, String.class);
		
		assertXMLEqual("<apples/>" + EOL, result);
	}
	
	
	DesignInstance design;

	private class HasXMLPropertyDesignF implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new HasXMLPropertyDesign(element, parentContext);
		}
	}
	
	private class HasXMLPropertyDesign extends DesignValueBase {
		
		SimpleDesignProperty ourXml;
		
		public HasXMLPropertyDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, 
					new SimpleArooaClass(HasXMLProperty.class),
					parentContext);
			
			ourXml = new SimpleDesignProperty("ourXml", this);
		}
		
		@Override
		protected DesignProperty[] children() {
			return new DesignProperty[] { ourXml };
		}
		
		public Form detail() {
			return new StandardForm("Test", this).addFormItem(ourXml.view());
		}
		
	}
	
	public void testDesign() throws Exception {
		
		String xml = 
			"<whatever>" + EOL +
			"    <ourXml>" + EOL +
			"        <xml>" + EOL +
			"            <snack>" + EOL +
			"                <fruit>" + EOL +
			"                    <apple/>" + EOL +
			"                </fruit>" + EOL +
			"            </snack>" + EOL +
			"        </xml>" + EOL +
			"    </ourXml>" + EOL +
			"</whatever>" + EOL;
				
		DesignParser parser = new DesignParser(
				new StandardArooaSession(), 
				new HasXMLPropertyDesignF());
		
		parser.setExpectedDoucmentElement(new ArooaElement("whatever"));
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		HasXMLPropertyDesign design = (HasXMLPropertyDesign) parser.getDesign();
	
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		xmlParser.parse(design.getArooaContext().getConfigurationNode());
		
		assertXMLEqual(xml, xmlParser.getXml());
		
		this.design = design;
	}
	
	public static void main(String args[]) throws Exception {

		XMLTypeTest test = new XMLTypeTest();
		test.testDesign();
		
		ViewMainHelper helper = new ViewMainHelper(test.design);
		helper.run();
		
	}
}
