package org.oddjob.arooa.standard;

import java.util.concurrent.atomic.AtomicReference;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.MockConfigurationHandle;
import org.oddjob.arooa.deploy.ConfigurationDescriptorFactory;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.parsing.RootContext;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardArooaParserSaveTest extends XMLTestCase {

	public static class Root {
		Object child;
		@ArooaComponent
		public void setSnack(Object child) {
			this.child = child;
		}
	}
	
	public static class Apple {
		
	}
	
	public static class Orange {
		
	}
		
	private class OrangeConfiguration implements ArooaConfiguration {
		
		public ConfigurationHandle parse(ArooaContext parentContext)
				throws ArooaParseException {
			ArooaContext c1 = parentContext.getArooaHandler().onStartElement(
					new ArooaElement("orange"), parentContext);

			parentContext.getConfigurationNode().insertChild(
					c1.getConfigurationNode());
			
			c1.getRuntime().init();
			
			return new MockConfigurationHandle() {
			};
		}
	}
	

	String EOL = System.getProperty("line.separator");
	
	public void testConfigurationBackToXML() throws Exception {

		String descriptorXML =
				"<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
			    " <components>" +
			    "  <arooa:bean-def element='apple'" +
			    "      className='" + Apple.class.getName() + "'/>" +
			    "  <arooa:bean-def element='orange'" +
			    "      className='" + Orange.class.getName() + "'/>" +
        		" </components>" +
        		"</arooa:descriptor>";
		
		ArooaDescriptor descriptor = new ConfigurationDescriptorFactory(
				new XMLConfiguration("XML", descriptorXML)).createDescriptor(
								getClass().getClassLoader());

		Root root = new Root();

		String xml = "<root><snack><apple/></snack></root>";
		
		XMLConfiguration config = new XMLConfiguration("TEST", xml);
		
		StandardArooaParser parser = new StandardArooaParser(root,
				descriptor);
		
		parser.parse(config);

		XMLArooaParser parser2 = new XMLArooaParser();
		
		ArooaContext contextForRoot = parser.getSession().getComponentPool().contextFor(
				root);

		// sanity check.
		assertFalse(contextForRoot instanceof RootContext);
		
		
		parser2.parse(
				contextForRoot.getConfigurationNode());
		
		String expected = 
			"<root>" + EOL +
			"    <snack>" + EOL +
			"        <apple/>" + EOL +
			"    </snack>" + EOL +
			"</root>" + EOL;
		
		assertXMLEqual(expected, parser2.getXml());
	}
	
	public void testSave() throws Exception {

		String descriptorXML =
				"<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
			    " <components>" +
			    "  <arooa:bean-def element='apple'" +
			    "      className='" + Apple.class.getName() + "'/>" +
			    "  <arooa:bean-def element='orange'" +
			    "      className='" + Orange.class.getName() + "'/>" +
        		" </components>" +
        		"</arooa:descriptor>";
		
		ArooaDescriptor descriptor = new ConfigurationDescriptorFactory(
				new XMLConfiguration("XML", descriptorXML)).createDescriptor(
								getClass().getClassLoader());

		Root root = new Root();

		String xml = "<root><snack><apple/></snack></root>";
		
		XMLConfiguration config = new XMLConfiguration("TEST", xml);
		
		final AtomicReference<String > savedXML = new AtomicReference<String>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		StandardArooaParser parser = new StandardArooaParser(root,
				descriptor);
		
		ConfigurationHandle handle = parser.parse(config);
		
		ComponentPool components = parser.getSession().getComponentPool();
		
		ArooaContext rootContext = components.contextFor(root);
		ArooaContext appleContext = components.contextFor(root.child);
		
		CutAndPasteSupport cap = new CutAndPasteSupport(rootContext);

		cap.replace(appleContext, new OrangeConfiguration());
		
		handle.save();
		
		String expected = 
			"<root>" + EOL +
			"    <snack>" + EOL +
			"        <orange/>" + EOL +
			"    </snack>" + EOL +
			"</root>" + EOL;
		
		assertXMLEqual(expected, savedXML.get());
	}
	
	public void testSaveOfAParse() throws Exception {

		String descriptorXML =
				"<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
			    " <components>" +
			    "  <arooa:bean-def element='apple'" +
			    "      className='" + Apple.class.getName() + "'/>" +
			    "  <arooa:bean-def element='orange'" +
			    "      className='" + Orange.class.getName() + "'/>" +
        		" </components>" +
        		"</arooa:descriptor>";
		
		ArooaDescriptor descriptor = new ConfigurationDescriptorFactory(
				new XMLConfiguration("XML", descriptorXML)).createDescriptor(
								getClass().getClassLoader());

		Root root = new Root();

		String xml = "<root><snack><apple/></snack></root>";
		
		XMLConfiguration config = new XMLConfiguration("TEST", xml);
		
		final AtomicReference<String > savedXML = new AtomicReference<String>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		StandardArooaParser parser = new StandardArooaParser(root,
				descriptor);
		
		ConfigurationHandle handle1 = parser.parse(config);
		
		StandardArooaParser parser2 = new StandardArooaParser(root,
				descriptor);
		
		ConfigurationHandle handle2 = parser2.parse(
				parser.getSession().getComponentPool().contextFor(
						root).getConfigurationNode());
		
		ComponentPool components = parser2.getSession().getComponentPool();
		
		ArooaContext rootContext = components.contextFor(root);
		ArooaContext appleContext = components.contextFor(
				root.child);
		
		CutAndPasteSupport cap = new CutAndPasteSupport(rootContext);

		cap.replace(appleContext, new OrangeConfiguration());
		
		handle2.save();
		
		assertNull(savedXML.get());
		
		handle1.save();
		
		String expected = 
			"<root>" + EOL +
			"    <snack>" + EOL +
			"        <orange/>" + EOL +
			"    </snack>" + EOL +
			"</root>" + EOL;
		
		assertXMLEqual(expected, savedXML.get());
	}
	
	public void testChangeDocAndSave() throws Exception {

		String descriptorXML =
				"<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
			    " <components>" +
			    "  <arooa:bean-def element='apple'" +
			    "      className='" + Apple.class.getName() + "'/>" +
			    "  <arooa:bean-def element='orange'" +
			    "      className='" + Orange.class.getName() + "'/>" +
        		" </components>" +
        		"</arooa:descriptor>";
		
		ArooaDescriptor descriptor = new ConfigurationDescriptorFactory(
				new XMLConfiguration("XML", descriptorXML)).createDescriptor(
								getClass().getClassLoader());

		Root root = new Root();

		XMLConfiguration config = new XMLConfiguration("TEST", 
				"<root id='x'/>");
		
		final AtomicReference<String > savedXML = new AtomicReference<String>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		StandardArooaParser parser = new StandardArooaParser(root,
				descriptor);
		
		ConfigurationHandle handle1 = parser.parse(config);
		
		StandardArooaParser parser2 = new StandardArooaParser(root,
				descriptor);
		
		ConfigurationHandle handle2 = parser2.parse(
				handle1.getDocumentContext().getConfigurationNode());
		
		ArooaContext rootContext = handle2.getDocumentContext();
		
		CutAndPasteSupport.replace(
				rootContext.getParent(), 
				rootContext, 
				new XMLConfiguration("TEST", "<root id='y'/>")
			);
		
		handle2.save();
		
		assertNull(savedXML.get());
		
		handle1.save();
		
		String expected = 
			"<root id=\"y\"/>" + EOL;
		
		assertXMLEqual(expected, savedXML.get());
	}
}
