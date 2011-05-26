package org.oddjob.arooa.standard;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.registry.InvalidIdException;
import org.oddjob.arooa.registry.ServiceProvider;
import org.oddjob.arooa.registry.Services;
import org.oddjob.arooa.xml.XMLConfiguration;

public class AutoConfigurationTest extends TestCase {

	public static class Snack {
		
		Fruit fruit;
		
		@Inject
		public void setFruit(Fruit fruit) {
			this.fruit = fruit;
		}
	}
	
	public interface Fruit {
		
	}
	
	private class OurProvider implements ServiceProvider {
		
		int provided;
		
		public Services getServices() {
			return new Services() {
				
				public String serviceNameFor(Class<?> theClass, String flavour) {
					assertEquals(Fruit.class, theClass);
					return "fruit";
				}
				
				public Object getService(String serviceName) {
					assertEquals("fruit", serviceName);
					++provided;
					return new Fruit() {
						@Override
						public String toString() {
							return "I could be an apple.";
						}
					};
				}
			};
		}
	}
	
	public void testAutoSet() throws ArooaParseException, InvalidIdException {

		ArooaSession session = new StandardArooaSession();
		
		OurProvider services = new OurProvider();
		
		session.getBeanRegistry().register("services", services);

		Snack root = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(
				root, session);

		parser.parse(new XMLConfiguration("TEST",
				"<whatever/>"));
		
		assertNull(root.fruit);
		
		session.getComponentPool().configure(root);
		
		assertEquals("I could be an apple.", root.fruit.toString());
		
		assertEquals(1, services.provided);
		
	}
	
	public static class Afternoon {
		
		Snack snack;
		Fruit fruit;
		
		public Snack getSnack() {
			return snack;
		}

		public void setSnack(Snack snack) {
			this.fruit = snack.fruit;
			this.snack = snack;
		}
	}
	
	public void testAutoSetNested() throws ArooaParseException, InvalidIdException {

		ArooaSession session = new StandardArooaSession();
		
		OurProvider services = new OurProvider();
		
		session.getBeanRegistry().register("services", services);

		Afternoon root = new Afternoon();
		
		StandardArooaParser parser = new StandardArooaParser(
				root, session);

		parser.parse(new XMLConfiguration("TEST",
				"<whatever>" +
				" <snack>" +
				"  <bean class='" + Snack.class.getName() + "'/>" +
				" </snack>" +
				"</whatever>"));
		
		assertNull(root.snack);
		
		session.getComponentPool().configure(root);
		
		assertNotNull(root.snack);
		assertNotNull(root.fruit);
		assertNotNull(root.snack.fruit);
		
		assertEquals("I could be an apple.", root.snack.fruit.toString());
		
		assertEquals(1, services.provided);
		
	}

}
