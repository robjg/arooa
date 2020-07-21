package org.oddjob.arooa.life;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ContextDestroyer;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.runtime.RuntimeEvent;
import org.oddjob.arooa.runtime.RuntimeListener;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ArooaLifecycleTest extends Assert {

	private static List<String> events = new ArrayList<String>();

   @Before
   public void setUp() throws Exception {
		events.clear();
	}
	
	public static class EventCapture 
	implements ArooaSessionAware, ArooaContextAware {
		
		@Override
		public void setArooaSession(ArooaSession session) {
			events.add("setArooaSession");
		}
		
		@Override
		public void setArooaContext(ArooaContext context) {
			events.add("setArooaContext");
			
			context.getRuntime().addRuntimeListener(new RuntimeListener() {
				
				@Override
				public void beforeInit(RuntimeEvent event)
						throws ArooaConfigurationException {
					events.add("beforeInit");
				}
				
				@Override
				public void beforeDestroy(RuntimeEvent event)
						throws ArooaConfigurationException {
					events.add("beforeDestroy");
				}
				
				@Override
				public void beforeConfigure(RuntimeEvent event)
						throws ArooaConfigurationException {
					events.add("beforeConfigure");
				}
				
				@Override
				public void afterInit(RuntimeEvent event)
						throws ArooaConfigurationException {
					events.add("afterInit");
				}
				
				@Override
				public void afterDestroy(RuntimeEvent event)
						throws ArooaConfigurationException {
					events.add("afterDestroy");
				}
				
				@Override
				public void afterConfigure(RuntimeEvent event)
						throws ArooaConfigurationException {
					events.add("afterConfigure");
				}
			});
		}
		
		public void setConstant(String constant) {
			events.add("setConstant: " + constant);
		}
		
		public void setRuntime(String runtime) {
			events.add("setRuntime: " + runtime);
		}
		
		@Override
		public String toString() {
			return "OurBean";
		}
	}
	
   @Test
	public void testAllLifecycleOnRoot() throws ArooaParseException {
		
		String xml = 
			"<test constant='apple' runtime='${orange}'/>";
		
		EventCapture capture = new EventCapture();
		
		ArooaParser parser = new StandardArooaParser(capture);

		ConfigurationHandle<ArooaContext> handle = parser.parse(new XMLConfiguration("XML",
				xml	));
		
		ArooaSession session = handle.getDocumentContext().getSession();
		
		session.getBeanRegistry().register("orange", "jaffa");
		
		session.getComponentPool().configure(
				capture);
		
		new ContextDestroyer().destroy(handle.getDocumentContext());
		
		assertEquals("setArooaContext", events.get(0));
		assertEquals("setArooaSession", events.get(1));
		assertEquals("beforeInit", events.get(2));
		assertEquals("setConstant: apple", events.get(3));
		assertEquals("afterInit", events.get(4));
		assertEquals("beforeConfigure", events.get(5));
		assertEquals("setRuntime: jaffa", events.get(6));
		assertEquals("afterConfigure", events.get(7));
		assertEquals("beforeDestroy", events.get(8));
		assertEquals("afterDestroy", events.get(9));
		
		assertEquals(10, events.size());		
		
	}
	
	public static class ThingWithObject {

		public void setObject(Object o) {
			events.add("ParentProperty set: " + o);
		}
		
		@Override
		public String toString() {
			return "ThingWithObject";
		}
	}
	
   @Test
	public void testAllLifecycleOnObject() throws ArooaParseException, ArooaPropertyException, ArooaConversionException {
		
		String xml = 
			"<test>" +
			" <object>" +
			"  <bean class='" + EventCapture.class.getName() + "' " +
			"    constant='apple' runtime='${orange}'/>" +
			" </object>" +
			"</test>";
		
		ThingWithObject root = new ThingWithObject();
		
		ArooaParser parser = new StandardArooaParser(root);

		ConfigurationHandle<ArooaContext> handle = parser.parse(new XMLConfiguration("XML",
				xml	));
		
		ArooaSession session = handle.getDocumentContext().getSession();
		
		session.getBeanRegistry().register("orange", "jaffa");
		
		session.getComponentPool().configure(
				root);
		
		new ContextDestroyer().destroy(handle.getDocumentContext());
		
		assertEquals("setArooaContext", events.get(0));
		assertEquals("setArooaSession", events.get(1));
		assertEquals("beforeInit", events.get(2));
		assertEquals("setConstant: apple", events.get(3));
		assertEquals("afterInit", events.get(4));
		assertEquals("beforeConfigure", events.get(5));
		assertEquals("setRuntime: jaffa", events.get(6));
		assertEquals("afterConfigure", events.get(7));
		assertEquals("afterConfigure", events.get(7));
		assertEquals("ParentProperty set: OurBean", events.get(8));
		assertEquals("beforeDestroy", events.get(9));
		assertEquals("ParentProperty set: null", events.get(10));
		assertEquals("afterDestroy", events.get(11));
		
		assertEquals(12, events.size());		
		
	}
	
	public static class ThingWithComponent {

		@ArooaComponent
		public void setComponent(Object o) {
			events.add("ParentProperty set: " + o);
		}
		
		@Override
		public String toString() {
			return "ThingWithComponent";
		}
	}
	
   @Test
	public void testAllLifecycleOnComponent() throws ArooaParseException, ArooaPropertyException, ArooaConversionException {
		
		String xml = 
			"<test>" +
			" <component>" +
			"  <bean class='" + EventCapture.class.getName() + "' " +
			"    id='comp' constant='apple' runtime='${orange}'/>" +
			" </component>" +
			"</test>";
		
		ThingWithComponent root = new ThingWithComponent();
		
		ArooaParser parser = new StandardArooaParser(root);

		ConfigurationHandle<ArooaContext> handle = parser.parse(new XMLConfiguration("XML",
				xml	));
		
		ArooaSession session = handle.getDocumentContext().getSession();
		
		session.getBeanRegistry().register("orange", "jaffa");
		
		EventCapture capture = session.getBeanRegistry().lookup("comp", 
				EventCapture.class);
		
		session.getComponentPool().configure(
				capture);
		
		new ContextDestroyer().destroy(handle.getDocumentContext());
		
		assertEquals("setArooaContext", events.get(0));
		assertEquals("setArooaSession", events.get(1));
		assertEquals("beforeInit", events.get(2));
		assertEquals("setConstant: apple", events.get(3));
		assertEquals("afterInit", events.get(4));
		assertEquals("ParentProperty set: OurBean", events.get(5));
		assertEquals("beforeConfigure", events.get(6));
		assertEquals("setRuntime: jaffa", events.get(7));
		assertEquals("afterConfigure", events.get(8));
		assertEquals("beforeDestroy", events.get(9));
		assertEquals("ParentProperty set: null", events.get(10));
		assertEquals("afterDestroy", events.get(11));
		
		assertEquals(12, events.size());		
		
	}
	
   @Test
	public void testManyLevelsDown() throws ArooaParseException, ArooaPropertyException, ArooaConversionException {
		
		String xml = 
			"<test>" +
			" <component>" +
			"  <bean class='" + ThingWithComponent.class.getName() + "'>" +
			"   <component>" +
			"    <bean id='owner' class='" + ThingWithObject.class.getName() + "'>" +
			"     <object>" +
			"      <bean class='" + EventCapture.class.getName() + "' " +
			"       	constant='apple' runtime='${orange}'/>" +
			"     </object>" +
			"    </bean>" +
			"   </component>" +
			"  </bean>" +
			" </component>" +
			"</test>";
		
		ThingWithComponent root = new ThingWithComponent();
		
		ArooaParser parser = new StandardArooaParser(root);

		ConfigurationHandle<ArooaContext> handle = parser.parse(new XMLConfiguration("XML",
				xml	));
		
		ArooaSession session = handle.getDocumentContext().getSession();
		
		session.getBeanRegistry().register("orange", "jaffa");
		
		session.getComponentPool().configure(
				session.getBeanRegistry().lookup("owner"));
		
		new ContextDestroyer().destroy(handle.getDocumentContext());
		
		assertEquals("setArooaContext", events.get(0));
		assertEquals("setArooaSession", events.get(1));
		assertEquals("beforeInit", events.get(2));
		assertEquals("setConstant: apple", events.get(3));
		assertEquals("afterInit", events.get(4));
		assertEquals("ParentProperty set: ThingWithObject", events.get(5));
		assertEquals("ParentProperty set: ThingWithComponent", events.get(6));
		assertEquals("beforeConfigure", events.get(7));
		assertEquals("setRuntime: jaffa", events.get(8));
		assertEquals("afterConfigure", events.get(9));
		assertEquals("ParentProperty set: OurBean", events.get(10));
		assertEquals("beforeDestroy", events.get(11));
		assertEquals("ParentProperty set: null", events.get(12));
		assertEquals("afterDestroy", events.get(13));
		assertEquals("ParentProperty set: null", events.get(14));
		assertEquals("ParentProperty set: null", events.get(15));
		
		assertEquals(16, events.size());		
		
	}
}
