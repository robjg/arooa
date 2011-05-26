package org.oddjob.arooa.parsing;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.registry.ChangeHow;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * Test to do with the RegistryTransaction.
 * 
 * @author rob
 *
 */
public class DragContextTest extends TestCase {

	public static class Component {
	
		List<Component> children = new ArrayList<Component>();
		
		@ArooaComponent
		public void setChild(int index, Component child) {
			new ListSetterHelper<Component>(children).set(index, child);
		}
	}
		
	public void testPasteCommit() throws ArooaPropertyException, ArooaParseException {

		String xml = 
			"<root>" +
			" <child>" +
			"  <bean class='" + Component.class.getName() + "'/>" +
			" </child>" +
			"</root>";
		
		Component root = new Component();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(
				new XMLConfiguration("XML", xml));

		ArooaSession session = parser.getSession();
		
		DragContext test = new DragContext(handle.getDocumentContext());
		
		DragContext testChild = new DragContext(
				session.getComponentPool().contextFor(root.children.get(0)));
		
		String copy = testChild.copy();
		
		DragTransaction trn = test.beginChange(ChangeHow.FRESH);
		
		test.paste(-1, copy);
		
		assertEquals(1, root.children.size());
				
		trn.commit();
		
		assertEquals(2, root.children.size());
	}
	
	public void testPasteRollback() throws ArooaParseException {
		
		String xml = 
			"<root>" +
			" <child>" +
			"  <bean class='" + Component.class.getName() + "'/>" +
			" </child>" +
			"</root>";
		
		Component root = new Component();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(
				new XMLConfiguration("XML", xml));

		ArooaSession session = parser.getSession();
		
		DragContext test = new DragContext(handle.getDocumentContext());
		
		DragContext testChild = new DragContext(
				session.getComponentPool().contextFor(root.children.get(0)));
		
		String copy = testChild.copy();
		
		DragTransaction trn = test.beginChange(ChangeHow.FRESH);
		
		test.paste(-1, copy);
		
		assertEquals(1, root.children.size());
				
		trn.rollback();
		
		assertEquals(1, root.children.size());
	}
	
	public void testDuplicateId() throws ArooaParseException {
		
		String xml = 
			"<root>" +
			" <child>" +
			"  <bean class='" + Component.class.getName() + 
				"' id='a'/>" +
			" </child>" +
			"</root>";
		
		Component root = new Component();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(
				new XMLConfiguration("XML", xml));

		ArooaSession session = parser.getSession();
		
		DragContext test = new DragContext(handle.getDocumentContext());
		
		DragContext testChild = new DragContext(
				session.getComponentPool().contextFor(root.children.get(0)));
		
		String copy = testChild.copy();
		
		DragTransaction trn = test.beginChange(ChangeHow.FRESH);
		
		test.paste(-1, copy);
		
		assertEquals(1, root.children.size());
				
		try {
			trn.commit();
		}
		catch (ArooaParseException e) {
			trn.rollback();		
			throw e;
		}
		
		assertEquals(2, root.children.size());
		
		assertNotNull(session.getBeanRegistry().lookup("a2"));
	}

	
	public void testExisting() throws ArooaParseException {
		
		String xml = 
			"<root>" +
			" <child>" +
			"  <bean class='" + Component.class.getName() + 
				"' id='a'/>" +
			"  <bean class='" + Component.class.getName() + 
				"' id='b'/>" +
			" </child>" +
			"</root>";
		
		Component root = new Component();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		parser.parse(
				new XMLConfiguration("XML", xml));

		ArooaSession session = parser.getSession();
		
		Object a = session.getBeanRegistry().lookup("a");
		Object b = session.getBeanRegistry().lookup("b");
		
		DragContext testA = new DragContext(
				session.getComponentPool().contextFor(a));
		DragContext testB = new DragContext(
				session.getComponentPool().contextFor(b));
		
		DragTransaction trn = testA.beginChange(ChangeHow.FRESH);

		String config = testA.copy();
		testA.cut();
		
		DragTransaction trn2 = testB.beginChange(ChangeHow.AGAIN);
		
		testB.paste(0, config);
		
		trn2.commit();
		
		assertEquals(1, root.children.size());
		assertEquals(1, root.children.get(0).children.size());
		
		try {
			trn.commit();
			fail("Should be already commited.");
		} catch (IllegalStateException e){
			// Expected.
		}
	}

	
	public void testEither() throws ArooaParseException {
		
		String xml = 
			"<root>" +
			" <child>" +
			"  <bean class='" + Component.class.getName() + 
				"' id='a'/>" +
			"  <bean class='" + Component.class.getName() + 
				"' id='b'/>" +
			" </child>" +
			"</root>";
		
		Component root = new Component();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		parser.parse(
				new XMLConfiguration("XML", xml));

		ArooaSession session = parser.getSession();
		
		Object a = session.getBeanRegistry().lookup("a");
		Object b = session.getBeanRegistry().lookup("b");
		
		DragContext testA = new DragContext(
				session.getComponentPool().contextFor(a));
		DragContext testB = new DragContext(
				session.getComponentPool().contextFor(b));
		
		DragTransaction trn = testA.beginChange(ChangeHow.FRESH);

		String config = testA.copy();
		testA.cut();
						
		DragTransaction trn2 = testB.beginChange(ChangeHow.EITHER);
		
		testB.paste(0, config);
		
		trn2.commit();
		
		assertEquals(2, root.children.size());
		
		trn.commit();
		
		assertEquals(1, root.children.size());
		assertEquals(1, root.children.get(0).children.size());
	}
	
	
	public void testEitherRollback() throws ArooaParseException {
		
		String xml = 
			"<root>" +
			" <child>" +
			"  <bean class='" + Component.class.getName() + 
				"' id='a'/>" +
			"  <bean class='" + Component.class.getName() + 
				"' id='b'/>" +
			" </child>" +
			"</root>";
		
		Component root = new Component();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		parser.parse(
				new XMLConfiguration("XML", xml));

		ArooaSession session = parser.getSession();
		
		Object a = session.getBeanRegistry().lookup("a");
		Object b = session.getBeanRegistry().lookup("b");
		
		DragContext testA = new DragContext(
				session.getComponentPool().contextFor(a));
		DragContext testB = new DragContext(
				session.getComponentPool().contextFor(b));
		
		DragTransaction trn = testA.beginChange(ChangeHow.FRESH);

		String config = testA.copy();
		testA.cut();
						
		DragTransaction trn2 = testB.beginChange(ChangeHow.EITHER);
		
		testB.paste(0, config);
		
		trn2.rollback();
		
		assertEquals(2, root.children.size());
		
		trn.commit();
		
		assertEquals(2, root.children.size());
	}
	
	public void testMaybe() throws ArooaParseException {
		
		String xml = 
			"<root>" +
			" <child>" +
			"  <bean class='" + Component.class.getName() + 
				"' id='a'/>" +
			" </child>" +
			"</root>";
		
		Component root = new Component();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		parser.parse(
				new XMLConfiguration("XML", xml));

		ArooaSession session = parser.getSession();
		
		Object a = session.getBeanRegistry().lookup("a");
		
		DragContext test = new DragContext(
				session.getComponentPool().contextFor(a));
		
		DragTransaction trn = test.beginChange(ChangeHow.MAYBE);
		
		assertNull(trn);
				
		DragTransaction trn2 = test.beginChange(ChangeHow.EITHER);
		
		test.cut();
		
		trn = test.beginChange(ChangeHow.MAYBE);
		
		assertNotNull(trn);
		
		trn.rollback();
		
		assertEquals(1, root.children.size());
		
		trn = test.beginChange(ChangeHow.MAYBE);
		
		try {
			trn2.commit();
			fail("Transactions should be complete.");
		} catch (IllegalStateException e) {
			// expected.
		}
		
	}
}

