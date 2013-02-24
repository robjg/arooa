package org.oddjob.arooa.parsing;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.registry.ChangeHow;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.xml.sax.SAXException;

public class ContextConfigurationSessionTest extends XMLTestCase {

	public static interface Fruit {
		
	}
	
	public static class Apple implements Fruit {
		
	}
	
	public static class Orange implements Fruit {
		
	}
	
	public static class Snack implements ConfigurationOwner {
		
		Fruit fruit;

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
		public DesignFactory rootDesignFactory() {
			throw new RuntimeException("Unexpected");
		}
		
		@Override
		public ArooaElement rootElement() {
			throw new RuntimeException("Unexpected");
		}
		
		public Fruit getFruit() {
			return fruit;
		}

		@ArooaComponent
		public void setFruit(Fruit fruit) {
			this.fruit = fruit;
		}
	}
		
	class OurListener implements SessionStateListener {
		boolean modified;
		
		public void sessionModifed(ConfigSessionEvent event) {
			modified = true;
		}
		
		public void sessionSaved(ConfigSessionEvent event) {
			modified = false;
		}
	}
	
	public void testModified() 
	throws ArooaParseException, SAXException, 
			IOException, ArooaPropertyException {
		
		XMLConfiguration config = new XMLConfiguration("TEST", 
				"<snack>" +
				" <fruit>" +
				"  <bean id='fruit' class='" + Apple.class.getName() + "'/>" +
				" </fruit>" +
				"</snack>");

		final AtomicReference<String > savedXML = new AtomicReference<String>();
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
		
		Object fruit = session.getBeanRegistry().lookup("fruit");

		ArooaContext fruitContext = session.getComponentPool().contextFor(fruit);
		
		ContextConfigurationSession test = 
			new ContextConfigurationSession(fruitContext);
		
		root.support.setConfigurationSession(new HandleConfigurationSession(handle));
		
		OurListener listener = new OurListener();
		
		test.addSessionStateListener(listener);
		
		assertFalse(test.isModified());
		assertFalse(listener.modified);
		
		DragPoint dragPoint = test.dragPointFor(fruit);
		
		DragTransaction trn = dragPoint.beginChange(ChangeHow.FRESH);
		dragPoint.cut();
		trn.commit();
		
		assertTrue(test.isModified());
		assertTrue(listener.modified);
		
		test.save();
		
		assertFalse(test.isModified());
		assertFalse(listener.modified);
		
		DragPoint rootPoint = test.dragPointFor(
				root);
		
		trn = rootPoint.beginChange(ChangeHow.FRESH);
		rootPoint.paste(0, 
				"  <bean id='fruit' class='" + Orange.class.getName() + "'/>");
		trn.commit();
		
		assertTrue(test.isModified());
		assertTrue(listener.modified);
		
		test.save();
		
		assertFalse(test.isModified());
		assertFalse(listener.modified);
		
		XMLUnit.setIgnoreWhitespace(true);

		String expected = 
			"<snack>" +
			" <fruit>" +
			"  <bean id='fruit' class='" + Orange.class.getName() + "'/>" +
			" </fruit>" +
			"</snack>";
		
		assertXMLEqual(expected, savedXML.get());
	}

}
