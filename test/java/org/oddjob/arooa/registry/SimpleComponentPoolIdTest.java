package org.oddjob.arooa.registry;

import org.junit.Test;

import javax.inject.Inject;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class SimpleComponentPoolIdTest extends Assert {

	public static class Root implements ArooaSessionAware {
		
		ArooaSession session;
		
		String id;
		
		public void setArooaSession(ArooaSession session) {
			this.session = session;
		}
		
		@ArooaComponent
		public void setComp(Object comp) {
			id = session.getBeanRegistry().getIdFor(comp);
		}
	}
	
   @Test
	public void testIdAvailable() throws ArooaParseException {
		
		Root root = new Root();
		
		XMLConfiguration config = new XMLConfiguration("TEST",
				"<root>" +
				" <comp>" +
				"  <bean id='apple'/>" +
				" </comp>" +
				"</root>");
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		parser.parse(config);
		
		assertEquals("apple", root.id);
	}

	private interface SnackProvider {}
	
	private static class SnackMachine implements SnackProvider {
		
	}
	
	public static class MyServices implements ServiceProvider {
		private SnackProvider mySnacks;
		
		public Services getServices() {
			return new Services() {
				
				@Override
				public String serviceNameFor(Class<?> theClass, String flavour) {
					assertEquals(SnackProvider.class, theClass);
					return "snacks";
				}
				
				@Override
				public Object getService(String serviceName)
						throws IllegalArgumentException {
					assertEquals("snacks", serviceName);
					return new SnackMachine();
				}
			};
		}
		
		@Inject
		public void setMySnacks(SnackProvider snacks) {
			this.mySnacks = snacks;
		}
		
		public SnackProvider getMySnacks() {
			return mySnacks;
		}
	}
	
   @Test
	public void testNoIdRegisteredForServiceProvider() throws ArooaParseException {
		
		MyServices root = new MyServices();
		
		XMLConfiguration config = new XMLConfiguration("TEST",
				"<root/>");
		
		StandardArooaParser parser = new StandardArooaParser(root);
		
		ConfigurationHandle handle = parser.parse(config);

		handle.getDocumentContext().getRuntime().configure();
		
		assertNotNull(root.getMySnacks());
	}
}
