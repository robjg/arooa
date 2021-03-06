package org.oddjob.arooa.design;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.standard.StandardTools;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class DesignInstanceBaseTest {

	public static class Snack {
	
		public void setFruit(Fruit fruit) {};
	}
	
	public static class Fruit {
		
	}
		
	class SnackDesignFactory implements DesignFactory {
		public DesignInstance createDesign(
				ArooaElement element, ArooaContext parentContext) {
			return new SnackDesign(
					element, parentContext);
		}
	}
		
	class SnackDesign extends DesignInstanceBase {

		SimpleDesignProperty fruit;
		
		SnackDesign(ArooaElement element, ArooaContext parentContext) {
			super(element, new SimpleArooaClass(Snack.class), parentContext);
			
			fruit = new SimpleDesignProperty(
					"fruit", Fruit.class, ArooaType.VALUE, this);
		}
		
		@Override
		public DesignProperty[] children() {
			return new DesignProperty[] { fruit };
		}
		
		public Form detail() {
			// TODO Auto-generated method stub
			return null;
		}
				
	}

	
	String EOL = System.getProperty("line.separator");
	
   @Test
	public void testLoadAndSave() throws Exception {
		
		ArooaSession session = new StandardArooaSession();

		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
				"<food:snack xmlns:food=\"http://food\">" + EOL +
				"    <fruit>" + EOL +
				"        <is/>" + EOL +
				"    </fruit>" + EOL +
				"</food:snack>" + EOL;

		DesignParser designParser = new DesignParser(session, 
				new SnackDesignFactory());
		
		designParser.parse(new XMLConfiguration("TEST", xml));
		
		DesignInstance design = designParser.getDesign();
		
		XMLArooaParser parser = new XMLArooaParser(session.getArooaDescriptor());
		
		parser.parse(design.getArooaContext().getConfigurationNode());
		
		assertThat(parser.getXml(), isIdenticalTo(xml));
	}
	

   @Test
	public void testEmptyProperty() throws Exception {
		
		ArooaSession session = new StandardArooaSession();
		
		DesignParser designParser = new DesignParser(session, 
				new DesignFactory() {
			public DesignInstance createDesign(
					ArooaElement element, ArooaContext parentContext) {
				return new SnackDesign(
						element, parentContext);
			}
		});
		
		String LS = System.getProperty("line.separator");
		
		String xml = "<snack/>" + LS;

		designParser.parse(new XMLConfiguration("TEST", xml));
		
		DesignInstance design = designParser.getDesign();
		
		XMLArooaParser parser = new XMLArooaParser(session.getArooaDescriptor());
		
		parser.parse(design.getArooaContext().getConfigurationNode());
		
		assertThat(parser.getXml(), isSimilarTo(xml));
	}
	
   @Test
	public void testRuntimeDestroy() {
	
		class PretendPropertyContext extends MockArooaContext {

			int index;
			
			@Override
			public RuntimeConfiguration getRuntime() {
				return new MockRuntimeConfiguration() {
					@Override
					public void setIndexedProperty(String name, int index,
							Object value) {
						PretendPropertyContext.this.index = index;
						assertNull(value);
						
					}
				};
			}
			
			@Override
			public ConfigurationNode getConfigurationNode() {
				return new MockConfigurationNode() {
					@Override
					public int indexOf(ConfigurationNode child) {
						return 99;
					}
				};
			}
			
			@Override
			public ArooaSession getSession() {
				return new MockArooaSession() {					
					@Override
					public ArooaDescriptor getArooaDescriptor() {
						return new MockArooaDescriptor() {
							@Override
							public ArooaBeanDescriptor getBeanDescriptor(
									ArooaClass classIdentifier, 
									PropertyAccessor propertyAccessor) {
								return null;
							}
						};
					}
					@Override
					public ArooaTools getTools() {
						return new StandardTools();
					}
				};
			}
		}
		
		PretendPropertyContext context = new PretendPropertyContext();
		
		SnackDesign test = new SnackDesign(new ArooaElement("test"), context);
		
		test.getArooaContext().getRuntime().destroy();
		
		assertEquals(99, context.index);
	}
	
   @Test
	public void testPropertyConfigurationNodes() {
		
		ArooaSession session = new StandardArooaSession();
		
		DesignSeedContext context = new DesignSeedContext(
				ArooaType.VALUE, session);
		
		SnackDesign test = new SnackDesign(new ArooaElement("test"), context);
		
		
		int index = test.getArooaContext().getConfigurationNode().indexOf(
				test.fruit.getArooaContext().getConfigurationNode());
		
		assertFalse(index < 0);
		
	}
	
   @Test
	public void testInvalidAttirbute() throws ArooaParseException, URISyntaxException {
		
		ArooaSession session = new StandardArooaSession();
		
		String xml = 
			"<food:snack xmlns:food=\"http://food\"" +
			"            fruit='apple'/>" + EOL;

		DesignParser designParser = new DesignParser(session, 
				new SnackDesignFactory());
		
		designParser.parse(new XMLConfiguration("TEST", xml));
		
		DesignInstance design = designParser.getDesign();
		
		assertTrue(design instanceof Unknown);
	}
	
}
