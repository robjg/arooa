package org.oddjob.arooa.parsing;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;


/**
 * More CutAndPaste looking at the runtime lifecycle.
 * @author rob
 *
 */
public class CutAndPasteSupport2Test extends Assert {

	public static class Component {
		
		List<Component> children = new ArrayList<Component>();
		
		@ArooaComponent
		public void setChild(Component component) {
			this.children.add(component);
		}
		
		public void setValue(Object value) {
			
		}
	}
	
   @Test
	public void testRegisterAndRemove() throws ArooaParseException {
		
		Component root = new Component();
		
		String middleBit = 
			"  <bean class='" + Component.class.getName() + "' id='a'>" +
			"   <child>" +
			"    <bean class='" + Component.class.getName() + "' id='b'/>" +
			"   </child>" +
			"   <value>" +
			"	 <identify  id='v'>" +
			"     <value>" +
			"      <bean/>" +
			"     </value>" +
			"    </identify>" +
			"   </value>" +
			"  </bean>";
		
		
		String xml =
			"<component>" +
			" <child>" +
			middleBit +
			" </child>" +
			"</component>";

		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("XML", xml));
		
		ArooaSession session = parser.getSession();
		
		ComponentPool pool = session.getComponentPool();
		
		ComponentGrabber components = new ComponentGrabber(session);
		
		assertEquals(3, components.ids.size());
		
		ArooaContext aContext = pool.trinityForId("a").getTheContext();

		ArooaContext parent = aContext.getParent();
		
		aContext.getRuntime().configure();
		
		Object value1 = session.getBeanRegistry().lookup("v");
		assertNotNull(value1);
		
		// Cut
		
		CutAndPasteSupport.cut(parent, aContext);
		
		components = new ComponentGrabber(session);
		
		assertEquals(1, components.ids.size());
		
		Object value2 = session.getBeanRegistry().lookup("v");
		assertNull(value2);
		
		// Paste
		
		ConfigurationHandle pasteHandle = 
			CutAndPasteSupport.paste(parent, 0,
				new XMLConfiguration("Paste", middleBit));
		
		components = new ComponentGrabber(session);
		
		assertEquals(3, components.ids.size());
		
		pasteHandle.getDocumentContext().getRuntime().configure();
		
		Object value3 = session.getBeanRegistry().lookup("v");
		assertNotNull(value3);
		assertNotSame(value1, value3);
		
		handle.getDocumentContext().getRuntime().destroy();
		
		components = new ComponentGrabber(session);
		
		assertEquals(0, components.ids.size());
		
		assertEquals(4, root.children.size());
		assertNotNull(root.children.get(0));
		assertNull(root.children.get(1));
		assertNotNull(root.children.get(2));
		assertNull(root.children.get(3));
	}

	/**
	 * Test with the CutAndPasteSupport member methods.
	 * 
	 * @throws ArooaParseException
	 */
   @Test
	public void testRegisterAndRemove2() throws ArooaParseException {
		
		Component root = new Component();
		
		String middleBit = 
			"  <bean class='" + Component.class.getName() + "' id='a'>" +
			"   <child>" +
			"    <bean class='" + Component.class.getName() + "' id='b'/>" +
			"   </child>" +
			"  </bean>";
		
		
		String xml =
			"<component>" +
			" <child>" +
			middleBit +
			" </child>" +
			"</component>";

		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("XML", xml));
		
		ArooaSession session = parser.getSession();
		
		ComponentPool pool = session.getComponentPool();
		
		ComponentGrabber components = new ComponentGrabber(session);
		
		assertEquals(3, components.ids.size());
		
		ArooaContext aContext = pool.trinityForId("a").getTheContext();

		CutAndPasteSupport test = new CutAndPasteSupport(handle.getDocumentContext());

		test.cut(aContext);
		
		components = new ComponentGrabber(session);
		
		assertEquals(1, components.ids.size());
		
		test.paste(0,
				new XMLConfiguration("Paste", middleBit));
		
		components = new ComponentGrabber(session);
		
		assertEquals(3, components.ids.size());
		
		handle.getDocumentContext().getRuntime().destroy();
		
		components = new ComponentGrabber(session);
		
		assertEquals(0, components.ids.size());
		
		assertEquals(4, root.children.size());
		assertNotNull(root.children.get(0));
		assertNull(root.children.get(1));
		assertNotNull(root.children.get(2));
		assertNull(root.children.get(3));
	}
	
	/**
	 * Test with the CutAndPasteSupport member methods.
	 * 
	 * @throws ArooaParseException
	 */
   @Test
	public void testRegisterAndRemove3() throws ArooaParseException {
		
		Component root = new Component();
		
		String middleBit = 
			"  <bean class='" + Component.class.getName() + "' id='a'>" +
			"   <child>" +
			"    <bean class='" + Component.class.getName() + "' id='b'/>" +
			"   </child>" +
			"  </bean>";
		
		
		String xml =
			"<component/>";

		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("XML", xml));
		
		ArooaSession session = parser.getSession();
		
		ComponentGrabber components = new ComponentGrabber(session);
		
		assertEquals(1, components.ids.size());
		
		CutAndPasteSupport test = new CutAndPasteSupport(handle.getDocumentContext());

		test.paste(0,
				new XMLConfiguration("Paste", middleBit));
		
		components = new ComponentGrabber(session);
		
		assertEquals(3, components.ids.size());

		handle.getDocumentContext().getRuntime().destroy();
		
		components = new ComponentGrabber(session);
		
		assertEquals(0, components.ids.size());
		
		assertEquals(2, root.children.size());
		assertNotNull(root.children.get(0));
		assertNull(root.children.get(1));
	}
	
   @Test
	public void testSameWitReplace() throws ArooaParseException {
		
		Component root = new Component();
		
		String middleBit = 
			"  <bean class='" + Component.class.getName() + "' id='a'>" +
			"   <child>" +
			"    <bean class='" + Component.class.getName() + "' id='b'/>" +
			"   </child>" +
			"   <value>" +
			"    <bean/>" +
			"   </value>" +
			"  </bean>";
		
		
		String xml =
			"<component>" +
			" <child>" +
			middleBit +
			" </child>" +
			"</component>";

		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("XML", xml));
		
		ArooaSession session = parser.getSession();
		
		ComponentPool pool = parser.getSession().getComponentPool();
		
		ComponentGrabber components = new ComponentGrabber(session);
		
		assertEquals(3, components.ids.size());
		
		ArooaContext aContext = pool.trinityForId("a").getTheContext();

		ArooaContext parent = aContext.getParent();
		
		// Replace 
		
		CutAndPasteSupport.replace(parent, aContext,
				new XMLConfiguration("Paste", middleBit));
		
		components = new ComponentGrabber(session);
				
		assertEquals(3, components.ids.size());
		
		handle.getDocumentContext().getRuntime().destroy();
		
		components = new ComponentGrabber(session);
		
		assertEquals(0, components.ids.size());
		
		assertEquals(4, root.children.size());
		assertNotNull(root.children.get(0));
		assertNull(root.children.get(1));
		assertNotNull(root.children.get(2));
		assertNull(root.children.get(3));
	}

   @Test
	public void testSameWitReplace2() throws ArooaParseException {
		
		Component root = new Component();
		
		String middleBit = 
			"  <bean class='" + Component.class.getName() + "' id='a'>" +
			"   <child>" +
			"    <bean class='" + Component.class.getName() + "' id='b'/>" +
			"   </child>" +
			"  </bean>";
		
		
		String xml =
			"<component>" +
			" <child>" +
			middleBit +
			" </child>" +
			"</component>";

		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(new XMLConfiguration("XML", xml));
		
		ArooaSession session = parser.getSession();
		
		ComponentPool pool = session.getComponentPool();
		
		ComponentGrabber components = new ComponentGrabber(session);
		
		assertEquals(3, components.ids.size());
		
		ArooaContext aContext = pool.trinityForId("a").getTheContext();
		
		CutAndPasteSupport test = new CutAndPasteSupport(
				handle.getDocumentContext());
				
		test.replace(aContext,
				new XMLConfiguration("Paste", middleBit));
		
		components = new ComponentGrabber(session);
				
		assertEquals(3, components.ids.size());
		
		handle.getDocumentContext().getRuntime().destroy();
		
		components = new ComponentGrabber(session);
		
		assertEquals(0, components.ids.size());
		
		assertEquals(4, root.children.size());
		assertNotNull(root.children.get(0));
		assertNull(root.children.get(1));
		assertNotNull(root.children.get(2));
		assertNull(root.children.get(3));
	}
	
	class ComponentGrabber {
	
		Set<Object> ids = new HashSet<Object>();
		
		ComponentGrabber(ArooaSession session) {
			ComponentPool components = session.getComponentPool();
			
			for (Object component: components.allTrinities()) {
				ids.add(component);
			}
		}
		
	}
}
