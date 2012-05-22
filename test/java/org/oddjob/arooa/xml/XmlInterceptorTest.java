package org.oddjob.arooa.xml;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.MockArooaContext;
import org.oddjob.arooa.parsing.MutableAttributes;
import org.oddjob.arooa.parsing.PrefixMappings;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeListener;
import org.oddjob.arooa.standard.StandardArooaParser;

public class XmlInterceptorTest extends XMLTestCase {

	private class TestContext extends MockArooaContext {
		String name;
		String result;

		RuntimeListener listener;
		
		@Override
		public PrefixMappings getPrefixMappings() {
			return new SimplePrefixMappings();
		}
		
		@Override
		public RuntimeConfiguration getRuntime() {
			return new MockRuntimeConfiguration() {
				@Override
				public void addRuntimeListener(
						RuntimeListener listener) {
					TestContext.this.listener = listener;
				}
				
				@Override
				public void setProperty(String name, Object value) throws ArooaException {
					TestContext.this.name = name;
					result = (String) value;
				}
			};
		}
				
	}
	
	public void testMethodAtATime() throws Exception {
		
		ParsingInterceptor test= new XMLInterceptor("result");

		TestContext testContext = new TestContext();
		
		ArooaContext xmlContext = test.intercept(testContext);
				
		MutableAttributes a1 = new MutableAttributes();
		ArooaElement e1 = new ArooaElement("a", a1);

		MutableAttributes a2 = new MutableAttributes();
		a2.set("x","y");
		ArooaElement e2 = new ArooaElement("b", a2);

		ArooaContext nextContext = xmlContext.getArooaHandler(
				).onStartElement(e1, xmlContext);
	
		nextContext.getConfigurationNode().addText("Hello World");
		
		ArooaContext anotherContext = nextContext.getArooaHandler(
				).onStartElement(e2, nextContext);
				
		anotherContext.getRuntime().init();

		nextContext.getRuntime().init();

		testContext.listener.beforeInit(null);
		
		assertEquals("result", testContext.name);
		
		String ls = System.getProperty("line.separator");
		
		String expected = "<a>" + ls + 
				"    <b x=\"y\"/><![CDATA[Hello World]]></a>" + ls;
		
		assertXMLEqual(expected, testContext.result);
	}
	
	
	
	public static class AComp {
		
		String xml;
		int i;
		
		public void setXml(String xml) {
			if (this.xml != null) {
				throw new RuntimeException("Expected Once.");
			}
			this.xml = xml;
		}
	}
	
	private class OurDescriptor extends MockArooaDescriptor {
		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return null;
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass forClass, PropertyAccessor accessor) {
			if (forClass == null) {
				return null;
			}
			assertEquals(new SimpleArooaClass(AComp.class), 
					forClass);
			return new MockArooaBeanDescriptor() {
				@Override
				public ParsingInterceptor getParsingInterceptor() {
					return new XMLInterceptor("xml");
				}
				@Override
				public String getComponentProperty() {
					return null;
				}
				@Override
				public ArooaAnnotations getAnnotations() {
					return new NoAnnotations();
				}
			};
		}
	}
	
	public void testInParser() throws Exception {

		String xml = "<comp>" +
						"<a fruit='apple' colour='red'><b/></a>" +
						"</comp>";
		
		AComp comp = new AComp();
		
		StandardArooaParser parser = new StandardArooaParser(
				comp, new OurDescriptor());
		parser.parse(new XMLConfiguration("Test", xml));
		
		String ls = System.getProperty("line.separator");
		
		String expected = 
				"<a fruit=\"apple\"" + ls +
				"   colour=\"red\">" + ls + 
				"    <b/>" + ls + 
				"</a>";		
		assertXMLEqual(expected, comp.xml);
	}
}
