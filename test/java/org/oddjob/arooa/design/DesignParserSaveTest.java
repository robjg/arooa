package org.oddjob.arooa.design;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.design.screem.TextField;
import org.oddjob.arooa.life.ArooaLifeAware;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

public class DesignParserSaveTest {

	public static class Snack { 
		
	}

	class SnackDesignF implements DesignFactory {
		
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new SnackDesign(element, parentContext);
		}
	}
	
	
	class SnackDesign extends DesignComponentBase {
		
		private final SimpleTextAttribute id;
		private final SimpleDesignProperty fruit;
		
		public SnackDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Snack.class), parentContext);
			
			id = new SimpleTextAttribute("id", this);
			fruit = new SimpleDesignProperty(
					"fruit", Object.class, ArooaType.VALUE, this);
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { id, fruit };
		}
		
		public Form detail() {
			return new StandardForm("Form", this).addFormItem(id.view());
		}
	}
	

	
	String EOL = System.getProperty("line.separator");

	/**
	 * Check the first step of a save.
	 * 
	 * @throws ArooaParseException
	 */
   @Test
	public void testPreSave() throws Exception {
		
		String xml =
			"<healthy:snack xmlns:healthy='urn:healthy' id='x'/> ";

		XMLConfiguration config = new XMLConfiguration("TEST", xml);
		
		DesignParser designParser = new DesignParser(
				new StandardArooaSession(), new SnackDesignF());
		designParser.setArooaType(ArooaType.COMPONENT);
		
		ConfigurationHandle designHandle = designParser.parse(
				config);
		
		DesignInstance design = designParser.getDesign();
		
		StandardForm form = (StandardForm) design.detail();
		
		TextField idField = (TextField) form.getFormItem(0);
		
		assertEquals("id", idField.getTitle());

		idField.getAttribute().attribute("y");

		final AtomicReference<String > savedXML = new AtomicReference<String>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		designHandle.save();

		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
			"<healthy:snack xmlns:healthy=\"urn:healthy\"" + EOL +
			"               id=\"y\"/>" + EOL;
		
		assertThat(savedXML.get(), isIdenticalTo(expected));
	}

   @Test
	public void testSave() throws Exception {
		
		String xml =
			"<healthy:snack xmlns:healthy='urn:healthy' id='x'/> ";

		Snack snack = new Snack();
		
		StandardArooaParser standardParser = new StandardArooaParser(snack);
		
		XMLConfiguration config = new XMLConfiguration("TEST", xml);
		
		ConfigurationHandle standardHandle = standardParser.parse(config);
		
		DesignParser designParser = new DesignParser(
				standardParser.getSession(), new SnackDesignF());
		designParser.setArooaType(ArooaType.COMPONENT);
		
		ConfigurationHandle designHandle = designParser.parse(
				standardHandle.getDocumentContext().getConfigurationNode());
		
		DesignInstance design = designParser.getDesign();
		
		StandardForm form = (StandardForm) design.detail();
		
		TextField idField = (TextField) form.getFormItem(0);
		
		assertEquals("id", idField.getTitle());

		idField.getAttribute().attribute("y");

		final AtomicReference<String > savedXML = new AtomicReference<String>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		designHandle.save();

		standardHandle.save();
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
			"<healthy:snack xmlns:healthy=\"urn:healthy\"" + EOL +
			"               id=\"y\"/>" + EOL;
		
		assertThat(savedXML.get(), isIdenticalTo(expected));
	}
	
	public static class Snack2 implements ArooaLifeAware { 
		Object fruit;
		
		boolean beBad;

		int count = 0;
		
		public void setFruit(Object fruit) {
			// Ensure everything is destroyed properly on bad save
			if (fruit == null) {
				this.fruit = null;
			}
			else {
				if (this.fruit != null) {
					throw new RuntimeException("Fruit already set!");
				}
				this.fruit = fruit;
			}
		}
		
		public void initialised() {
			if (beBad) {
				beBad = false;
				throw new RuntimeException("Being Bad");
			}
			++count;
		}
		
		public void configured() {
			// TODO Auto-generated method stub
			
		}
		
		public void destroy() {
			--count;
		}
	}
	
   @Test
	public void testSaveFailOnSameId() throws Exception {
		
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
			"<snack id=\"x\">" + EOL +
			"    <fruit>" + EOL +
			"        <is/>" + EOL +
			"    </fruit>" + EOL +
			"</snack>" + EOL;
		

		Snack2 snack = new Snack2();
		
		StandardArooaParser standardParser = new StandardArooaParser(snack);
		
		XMLConfiguration config = new XMLConfiguration("TEST", xml);
		
		ConfigurationHandle standardHandle = standardParser.parse(config);
		
		// stop save.
		snack.beBad = true;
		
		DesignParser designParser = new DesignParser(
				standardParser.getSession(), new SnackDesignF());
		designParser.setArooaType(ArooaType.COMPONENT);
		
		ConfigurationHandle designHandle = designParser.parse(
				standardHandle.getDocumentContext().getConfigurationNode());
		
		DesignInstance design = designParser.getDesign();
		
		StandardForm form = (StandardForm) design.detail();
		
		TextField idField = (TextField) form.getFormItem(0);
		
		assertEquals("id", idField.getTitle());

		idField.getAttribute().attribute("y");

		try {
			designHandle.save();
			fail();
		} catch (ArooaParseException e) {
			// expected
		}

		final AtomicReference<String > savedXML = new AtomicReference<String>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		standardHandle.save();

		// no change
		assertThat(savedXML.get(), isIdenticalTo(xml));
		
		assertEquals(1, snack.count);
		
		standardHandle.getDocumentContext().getRuntime().destroy();
		
		assertEquals(0, snack.count);
		
	}
}
