package org.oddjob.arooa.parsing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.life.ArooaContextAware;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.registry.ChangeHow;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.xml.sax.SAXException;
import org.xmlunit.matchers.CompareMatcher;

public class ContextConfigurationSessionTest {

	public static class Pip {
		
	}
	
	public static class Apple implements ArooaContextAware {
		ContextConfigurationSession test;
		
		@Override
		public void setArooaContext(ArooaContext context) {
			test = new ContextConfigurationSession(context);
				
		}
		
		@ArooaComponent
		public void setPip(int index, Pip pip) {
			
		}
	}
	
	public static class Snack implements ConfigurationOwner {
		
		Apple fruit;

		ConfigurationOwnerSupport support = new ConfigurationOwnerSupport(this);
		
		@Override
		public ConfigurationSession provideConfigurationSession() {
			return support.provideConfigurationSession();
		}
		
		@Override
		public void addOwnerStateListener(OwnerStateListener listener) {
			support.addOwnerStateListener(listener);
		}

		@Override
		public void removeOwnerStateListener(OwnerStateListener listener) {
			support.removeOwnerStateListener(listener);
		}

		@Override
		public SerializableDesignFactory rootDesignFactory() {
			throw new RuntimeException("Unexpected");
		}
		
		@Override
		public ArooaElement rootElement() {
			throw new RuntimeException("Unexpected");
		}
		
		public Apple getFruit() {
			return fruit;
		}

		@ArooaComponent
		public void setFruit(Apple fruit) {
			this.fruit = fruit;
		}
	}
		
	class OurListener implements SessionStateListener {
		boolean modified;
		
		public void sessionModified(ConfigSessionEvent event) {
			modified = true;
		}
		
		public void sessionSaved(ConfigSessionEvent event) {
			modified = false;
		}
	}
	
    @Test
	public void testModified() 
	throws ArooaParseException, ArooaPropertyException {
		
		XMLConfiguration config = new XMLConfiguration("TEST", 
				"<snack>" +
				" <fruit>" +
				"  <bean id='fruit' class='" + Apple.class.getName() + "'/>" +
				" </fruit>" +
				"</snack>");

		final AtomicReference<String > savedXML = new AtomicReference<>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		Snack root = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(root);

		ConfigurationHandle handle = parser.parse(config);
		
		ArooaSession session = handle.getDocumentContext().getSession();
		
		Apple fruit = (Apple) session.getBeanRegistry().lookup("fruit");

		root.support.setConfigurationSession(new HandleConfigurationSession(handle));
		
		OurListener listener = new OurListener();
		
		ContextConfigurationSession test = fruit.test;
		
		test.addSessionStateListener(listener);
		
		assertFalse(test.isModified());
		assertFalse(listener.modified);
		
		DragPoint dragPoint = test.dragPointFor(fruit);
		
		DragTransaction trn = dragPoint.beginChange(ChangeHow.FRESH);
		dragPoint.paste(0, 
				"  <bean class='" + Pip.class.getName() + "'/>");

		trn.commit();
		
		assertTrue(test.isModified());
		assertTrue(listener.modified);
		
		test.save();
		
		assertFalse(test.isModified());
		assertFalse(listener.modified);
		
		String EOL = System.getProperty("line.separator");
		
		String expected = 
			"<snack>" + EOL +
			"    <fruit>" + EOL +
			"        <bean id='fruit' class='" + Apple.class.getName() + "'>" + EOL +
			"            <pip>" + EOL +
			"                <bean class='" + Pip.class.getName() + "'/>" + EOL +
			"            </pip>" + EOL +
			"        </bean>" + EOL +
			"    </fruit>" + EOL +
			"</snack>";
		
		assertThat(savedXML.get(), isSimilarTo(expected));
		
		handle.getDocumentContext().getRuntime().destroy();
		
	}

    @Test
	public void testModifiedWhenPastingIntoExistingConfiguration() 
	throws ArooaParseException, SAXException, 
			IOException, ArooaPropertyException {
		
		XMLConfiguration config = new XMLConfiguration("TEST", 
				"<snack/>");

		final AtomicReference<String > savedXML = 
				new AtomicReference<String>();
		
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		Snack root = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(root);

		ConfigurationHandle handle = parser.parse(config);
		
		ArooaSession session = handle.getDocumentContext().getSession();
		
		root.support.setConfigurationSession(new HandleConfigurationSession(handle));
		
		// Paste in Fruit.
		
		DragPoint dragPoint = root.support.provideConfigurationSession(
				).dragPointFor(root);
		
		DragTransaction trn = dragPoint.beginChange(ChangeHow.FRESH);
		dragPoint.paste(0, 
				"  <bean id='fruit' class='" + Apple.class.getName() + "'/>");

		trn.commit();
				
		// Check test session.
		
		Apple fruit = (Apple) session.getBeanRegistry().lookup("fruit");

		OurListener listener = new OurListener();
		
		ContextConfigurationSession test = fruit.test;
		
		test.addSessionStateListener(listener);
		
		assertTrue(test.isModified());
		assertFalse(listener.modified);
		
		test.save();
		
		assertFalse(test.isModified());
		assertFalse(listener.modified);
		
		String EOL = System.getProperty("line.separator");
		
		String expected = 
			"<snack>" + EOL +
			"    <fruit>" + EOL +
			"        <bean id='fruit' class='" + Apple.class.getName() + "'/>" + EOL +
			"    </fruit>" + EOL +
			"</snack>";
		
		assertThat(savedXML.get(), CompareMatcher.isSimilarTo(expected));
		
		handle.getDocumentContext().getRuntime().destroy();
		
	}
}
