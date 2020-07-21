package org.oddjob.arooa.parsing;

import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.registry.ChangeHow;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HandleConfigurationSessionTest {

	public static interface Fruit {
		
	}
	
	public static class Apple implements Fruit {
		
	}
	
	public static class Orange implements Fruit {
		
	}
	
	public static class Snack {
		
		Fruit fruit;

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
		
		public void sessionModified(ConfigSessionEvent event) {
			modified = true;
		}
		
		public void sessionSaved(ConfigSessionEvent event) {
			modified = false;
		}
	}
	
   @Test
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
		
		HandleConfigurationSession test = 
			new HandleConfigurationSession(parser.getSession(), handle);
		
		OurListener listener = new OurListener();
		
		test.addSessionStateListener(listener);
		
		assertFalse(test.isModified());
		assertFalse(listener.modified);
		
		DragPoint dragPoint = test.dragPointFor(
				parser.getSession().getBeanRegistry().lookup("fruit"));
		
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
		
		String expected = 
			"<snack>" +
			" <fruit>" +
			"  <bean id='fruit' class='" + Orange.class.getName() + "'/>" +
			" </fruit>" +
			"</snack>";
		
		Diff diff = DiffBuilder.compare(expected)
				.withTest(savedXML.get()).ignoreWhitespace()
				.build();
		assertFalse(diff.toString(), diff.hasDifferences());
	}

   @Test
	public void testModifiedWhenReplaceRoot() throws ArooaParseException {
		
		XMLConfiguration config = new XMLConfiguration("TEST", 
				"<snack/>");
		
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				// don't care
			}
		});

		Snack root = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(root);

		ConfigurationHandle<ArooaContext> handle = parser.parse(config);
		
		HandleConfigurationSession test = 
			new HandleConfigurationSession(parser.getSession(), handle);
		
		OurListener listener = new OurListener();
		
		test.addSessionStateListener(listener);
		
		assertFalse(test.isModified());
		assertFalse(listener.modified);
		
		ArooaContext context = handle.getDocumentContext();
		ArooaContext parent = context.getParent();
		
		CutAndPasteSupport.replace(parent, context, config);
		
		
		assertTrue(test.isModified());
		assertTrue(listener.modified);
		
		test.save();
		
		assertFalse(test.isModified());
		assertFalse(listener.modified);
		
	}
}
