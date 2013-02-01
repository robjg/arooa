package org.oddjob.arooa.registry;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class ContextHierarcyServiceFinderTest extends TestCase {

	interface FruitService {}
	
	private static class TheServices implements Services {
		
		private final FruitService fruitService;

		public TheServices(FruitService fruitService) {
			this.fruitService = fruitService;
		}
		
		@Override
		public Object getService(String serviceName)
				throws IllegalArgumentException {
			if ("FRUIT".equals(serviceName)) {
					return fruitService;
			}
			else {
				throw new IllegalArgumentException(serviceName);
			}
		}
		@Override
		public String serviceNameFor(Class<?> theClass, String flavour) {
			if (theClass.isAssignableFrom(FruitService.class)) {
				return "FRUIT";
			}
			return null;
		}
	}
	
	public static class AppleService implements ServiceProvider, FruitService { 
		
		@Override
		public Services getServices() {
			return new TheServices(this);
		}

		@ArooaComponent
		public void setComponent(Object component) {
			
		}
		
	}
		
	public static class FruitAdict {
		
		private FruitService fruitService;
		
		@Inject
		public void setFruits(FruitService fruitService) {
			this.fruitService = fruitService;
		}

		public FruitService getFruits() {
			return fruitService;
		}
	}
	
	
	public void testParentMatches() throws ArooaPropertyException, ArooaConversionException, ArooaParseException {
		
		String xml = 
				"<ignored>" +
				" <component>" +
				"  <bean class='" + FruitAdict.class.getName() + "' id='adict'/>" +
				" </component>" +
				"</ignored>";
		
		AppleService appleService = new AppleService();
				
		StandardArooaParser parser = new StandardArooaParser(appleService);
		
		ConfigurationHandle handle = parser.parse(
				new XMLConfiguration("XML", xml));
		
		ArooaSession session = handle.getDocumentContext().getSession();
		
		Object adict = session.getBeanRegistry().lookup("adict");
		
		session.getComponentPool().configure(adict);
		
		FruitService result = session.getBeanRegistry().lookup(
				"adict.fruits", FruitService.class);

		assertEquals(appleService, result);
		
		// The above didn't actually prove much because it would be
		// found with the Component finder.
		
		ArooaContext context = session.getComponentPool().contextFor(
				adict);
		
		result = new ContextHierarchyServiceFinder(context).find(
				FruitService.class, null);
		
		assertEquals(appleService, result);
	}
}