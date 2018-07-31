package org.oddjob.arooa.parsing;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import java.net.URI;

import org.junit.Test;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.ArooaDescriptorBean;
import org.oddjob.arooa.deploy.BeanDefinition;
import org.oddjob.arooa.deploy.PropertyDefinition;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;

public class ElementConfigurationTest {

	public static class Stuff  {
		
		Stuff moreStuff;
		
		public void setMoreStuff(Stuff moreStuff) {
			if (moreStuff != null && this.moreStuff != null) {
				throw new RuntimeException("moreStuff not reset!");
			}
			this.moreStuff = moreStuff;
		}
		
	}
	
	String EOL = System.getProperty("line.separator");
	
    @Test
	public void testParseAndSave() throws Exception {
		
		BeanDefinition beanDef = new BeanDefinition();
		beanDef.setClassName(Stuff.class.getName());
		beanDef.setElement("stuff");
		beanDef.setProperties(0, 
				new PropertyDefinition(
						"moreStuff",
						PropertyDefinition.PropertyType.COMPONENT));
		
		
		ArooaDescriptorBean ourDescriptor = new ArooaDescriptorBean();
		ourDescriptor.setNamespace(new URI("http://stuff"));
		ourDescriptor.setPrefix("stf");
		ourDescriptor.setComponents(0, beanDef);
		
		ArooaDescriptor descriptor = ourDescriptor.createDescriptor(
				getClass().getClassLoader());
		
		StandardArooaSession session = new StandardArooaSession(descriptor);

		Stuff root = new Stuff();
		
		StandardArooaParser parser = new StandardArooaParser(root, session);

		ElementConfiguration test = new ElementConfiguration(
				new ArooaElement(new URI("http://stuff"), "stuff"));
		
		ConfigurationHandle handle = parser.parse(test);
		
		CutAndPasteSupport cnp = new CutAndPasteSupport(handle.getDocumentContext());
		
		ConfigurationHandle h2 = cnp.paste(0, test);
		
		assertNotNull(root.moreStuff);
		
		cnp.replace(h2.getDocumentContext(), test);
		
		assertNotNull(root.moreStuff);
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		xmlParser.parse(handle.getDocumentContext().getConfigurationNode());
		
		String expected = 
			"<stf:stuff xmlns:stf=\"http://stuff\">" + EOL +
			"    <moreStuff>" + EOL +
			"        <stf:stuff/>" + EOL +
			"    </moreStuff>" + EOL +
			"</stf:stuff>" + EOL;
			
		assertThat(xmlParser.getXml(), isSimilarTo(expected));
	}
	
}
