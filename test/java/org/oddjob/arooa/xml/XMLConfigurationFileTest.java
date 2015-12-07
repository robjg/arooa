package org.oddjob.arooa.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.xml.sax.SAXException;


public class XMLConfigurationFileTest extends XMLTestCase {
	
	private static final Logger logger = Logger.getLogger(XMLConfigurationFileTest.class);

	final File workDir;
	
	final File file;

	public XMLConfigurationFileTest() throws IOException {
		this.workDir = new File("work").getCanonicalFile();
		this.file = new File(workDir, "XMLConfigurationFileText.xml");
	}
	
	protected void setUp() {
		
		logger.info("----------------------   " + getName() + "   -------------------------");
		
		if (!workDir.exists()) {
			logger.info("Creating directory " + workDir);
			workDir.mkdir();
		}
		
		// For some reason occasionally the file is left open?
		logger.info("Check file exists: " + file.exists());
		
		for (int i = 0; i < 3; ++i) {
			logger.info("Try Creating file " + file);
			try {
				PrintWriter writer = new PrintWriter(
						new FileWriter(file));
				writer.println("<snack/>");
				writer.close();
				return;
			}
			catch (IOException e) {
				logger.error("Failed creating " + file, e);
			}
		}
		throw new RuntimeException("Failed attempting to create " + file);
	}
	
	protected void tearDown() {
		logger.info("Deleting file " + file);
		file.delete();
	}
	
	String EOL = System.getProperty("line.separator");
	
	/**
	 * Check the configuration is re-readable.
	 * 
	 * @throws ArooaParseException
	 */
	public void testReParse() throws Exception {
		
		XMLConfiguration config = new XMLConfiguration(file);
		
		XMLArooaParser parser = new XMLArooaParser();
		
		parser.parse(config);

		assertXMLEqual("<snack/>" + EOL, parser.getXml());
		
		parser = new XMLArooaParser();
		
		// should force re-read of the file.
		parser.parse(config);
		
		assertXMLEqual("<snack/>" + EOL, parser.getXml());
	}
	
	public static class Snack {
		String stuff;
		
		public void setStuff(String stuff) {
			this.stuff = stuff;
		}
	}
	
	public static class SnackArooa extends MockArooaBeanDescriptor {
		
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
		
		@Override
		public ArooaAnnotations getAnnotations() {
			return new NoAnnotations();
		}
	}

	
	public void testChangeAndSave() throws ArooaParseException, SAXException, IOException {
		
		XMLConfiguration config = new XMLConfiguration(file);
		
		Snack root = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(config);

		ArooaContext context = parser.getSession().getComponentPool().contextFor(root);

		String pasteXML = "<stuff><value value='apple'/></stuff>";
		
		CutAndPasteSupport.paste(
				context, 
				0, 
				new XMLConfiguration("Paste XML", pasteXML));

		context.getRuntime().configure();
		
		assertEquals("apple", root.stuff);
		
		handle.save();
		
		XMLConfiguration configCheck = new XMLConfiguration(file);
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		xmlParser.parse(configCheck);

		String expected =
			"<snack>" + EOL +
			"    <stuff>" + EOL +
			"        <value value=\"apple\"/>" + EOL +
			"    </stuff>" + EOL +
			"</snack>" + EOL;
		
		assertXMLEqual(expected, xmlParser.getXml());
		
	}
	
	public void testChangeRoot() throws Exception {
		
		XMLConfiguration config = new XMLConfiguration(file);
		
		Snack root = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(config);

		ArooaContext context = parser.getSession().getComponentPool().contextFor(root);

		String pasteXML = "<meal/>";
		
		CutAndPasteSupport.paste(
				context.getParent(), 
				0, 
				new XMLConfiguration("Paste XML", pasteXML));

		handle.save();
		
		XMLConfiguration configCheck = new XMLConfiguration(file);
		
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		xmlParser.parse(configCheck);

		String expected =
			"<meal/>" + EOL;
		
		assertXMLEqual(expected, xmlParser.getXml());
	}
	
}
