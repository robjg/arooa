package org.oddjob.arooa.runtime;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.registry.ComponentPool;
import org.oddjob.arooa.registry.ServiceProvider;
import org.oddjob.arooa.registry.Services;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Type;

public class AutoSetterTest extends Assert {
	
	public static class MyThing {
		
		String prop1;
		String prop2;
		Integer prop3;
		
		@Inject @Named("junk")
		public void setProp1(String prop1) {
			this.prop1 = prop1;
		}
		
		@Inject 
		public void setProp2(String prop2) {
			this.prop2 = prop2;
		}

		@Inject 
		public void setProp3(Integer prop3) {
			this.prop3 = prop3;
		}
	}
	

	private static class OurRuntime extends MockRuntimeConfiguration {
		
		String name;
		Object value;
		
		@Override
		public ArooaClass getClassIdentifier() {
			return new SimpleArooaClass(MyThing.class);
		}
		
		@Override
		public void setProperty(String name, Object value)
				throws ArooaException {
            assertNull(this.name);
			this.name = name;
			this.value = value;
		}
	}
	
	public static class MyServices implements ServiceProvider {
		@Override
		public Services getServices() {
			return new Services() {
				@Override
				public Object getService(String serviceName)
						throws IllegalArgumentException {
					if (serviceName.equals("fruit")) {
						return "pears";
					}
					else if (serviceName.equals("crisps")) {
						return "salt and vinegar";
					}
					else if (serviceName.endsWith("numbers")) {
						return null;
					}
					else throw new RuntimeException("Unexpected!");
				}
				@Override
				public String serviceNameFor(Type theClass, String flavour) {
					if (Integer.class == theClass) {
						return null;
					}
					else if (String.class == theClass) {
						if (flavour == null) {
							return "fruit";
						}
						else if ("junk".equals(flavour)) {
							return "crisps";
						}
						else throw new RuntimeException("Unexpected " + flavour);
					}
					else throw new RuntimeException("Unexpected " + theClass);
				}
			};
		}
	}
	
	private static class OurContext extends MockArooaContext {
		ArooaSession session;
		RuntimeConfiguration runtime;
				
		@Override
		public RuntimeConfiguration getRuntime() {
			return runtime;
		}
		
		@Override
		public ArooaSession getSession() {
			return session;
		}
		
		@Override
		public ArooaContext getParent() {
			return null;
		}
	}
	
   @Test
	public void testAutoSetting() {
		
		OurRuntime runtime = new OurRuntime();
		
		AutoSetter test = new AutoSetter();
		
		test.markAsSet("prop1");
		test.markAsSet("prop3");
		
		ArooaSession session = new StandardArooaSession();
		
		session.getBeanRegistry().register("services", new MyServices());
		
		OurContext context = new OurContext();
		context.runtime = runtime;
		context.session = session;
		
		test.setServices(context);
		
		assertEquals("pears", runtime.value);
	}
	
	public static class MyRoot implements ServiceProvider {
		
		MyThing thing;
		
		@Override
		public Services getServices() {
			return new Services() {
				
				@Override
				public String serviceNameFor(Type theClass, String flavour) {
					if (Integer.class == theClass) {
						return "numbers";
					}
					else if (String.class == theClass){
						return null;
					}
					else throw new RuntimeException("Unexpected " + theClass);
				}
				
				@Override
				public Object getService(String serviceName)
						throws IllegalArgumentException {
                    return switch (serviceName) {
                        case "numbers" -> 2;
                        case "fruit", "crisps" -> null;
                        default -> throw new RuntimeException("Unexpected " + serviceName);
                    };
				}
			};
		}
		
		public void setAnything(Object o) {
			
		}
		
		@ArooaComponent
		public void setThing(MyThing thing) {
			this.thing = thing;
		}
	}
	
   @Test
	public void testMultipleProviders() throws ArooaParseException {
		
		String xml = 
			"<root>" +
			" <anything>" +
			"  <identify id='register-me'>" +
			"   <value>" +
			"  <bean  class='" + MyServices.class.getName() + "'/>" +
			"   </value>" +
			"  </identify>" +
			" </anything>" +
			" <thing>" +
			"  <bean class='" + MyThing.class.getName() + "'/>" +
			" </thing>" +
			"</root>";

		MyRoot root = new MyRoot();
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		parser.parse(new XMLConfiguration("XML", xml));
		
		ArooaSession session = parser.getSession();
				
		ComponentPool components = session.getComponentPool();
		
		components.configure(root);
				
		components.configure(root.thing);
		
		assertEquals("salt and vinegar", root.thing.prop1);
		assertEquals("pears", root.thing.prop2);
		assertEquals(Integer.valueOf(2), root.thing.prop3);
		
		CutAndPasteSupport cnp = new CutAndPasteSupport(
				components.contextFor(root));
		
		cnp.replace(components.contextFor(root.thing), 
				new XMLConfiguration("XML",
						"  <bean class='" + MyThing.class.getName() + "'/>"));
		
		components.configure(root.thing);
		
		assertEquals("salt and vinegar", root.thing.prop1);
		assertEquals("pears", root.thing.prop2);
		assertEquals(Integer.valueOf(2), root.thing.prop3);
		
		
		root.thing.prop1 = null;
		root.thing.prop2 = null;
		root.thing.prop3 = null;
		
		components.configure(root.thing);
		
		assertEquals("salt and vinegar", root.thing.prop1);
		assertEquals("pears", root.thing.prop2);
		assertEquals(Integer.valueOf(2), root.thing.prop3);
		
		
	}
}
