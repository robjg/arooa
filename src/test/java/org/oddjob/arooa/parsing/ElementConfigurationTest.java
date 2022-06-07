package org.oddjob.arooa.parsing;

import org.junit.Test;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.ArooaDescriptorBean;
import org.oddjob.arooa.deploy.BeanDefinitionBean;
import org.oddjob.arooa.deploy.PropertyDefinitionBean;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;

import java.net.URI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

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
		
		BeanDefinitionBean beanDef = new BeanDefinitionBean();
		beanDef.setClassName(Stuff.class.getName());
		beanDef.setElement("stuff");
		beanDef.setProperties(0, 
				new PropertyDefinitionBean(
						"moreStuff",
						PropertyDefinitionBean.PropertyType.COMPONENT));
		
		
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
		
		ConfigurationHandle<ArooaContext> handle = parser.parse(test);
		
		CutAndPasteSupport cnp = new CutAndPasteSupport(handle.getDocumentContext());
		
		ConfigurationHandle<ArooaContext> h2 = cnp.paste(0, test);
		
		assertNotNull(root.moreStuff);
		
		cnp.replace(h2.getDocumentContext(), test);
		
		assertNotNull(root.moreStuff);
		
		XMLArooaParser xmlParser = new XMLArooaParser(descriptor);
		
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
