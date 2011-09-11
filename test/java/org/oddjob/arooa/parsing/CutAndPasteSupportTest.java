package org.oddjob.arooa.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.custommonkey.xmlunit.XMLTestCase;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.MockArooaBeanDescriptor;
import org.oddjob.arooa.MockArooaDescriptor;
import org.oddjob.arooa.MockElementMappings;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class CutAndPasteSupportTest extends XMLTestCase {

	static final String LS = System.getProperty("line.separator");
	
	public static class Snack {
		
		Fruit fruit;
		
		public void setFruit(Fruit fruit) {
			this.fruit = fruit;
		}		
	}
	
	interface Fruit {
	
	}
	
	public static class Apple implements Fruit{
		
	}
	
	public static class Orange implements Fruit {
		
	}
	
	private class OurDescriptor extends MockArooaDescriptor {
		
		@Override
		public ConversionProvider getConvertletProvider() {
			return null;
		}
		
		@Override
		public ElementMappings getElementMappings() {
			return new MappingsSwitch(new MockElementMappings() {
					@Override
					public ArooaClass mappingFor(ArooaElement element,
			    			InstantiationContext parentContext) {
						if (new ArooaElement("apple").equals(element)) {
							return new SimpleArooaClass(Apple.class);
						}
						if (new ArooaElement("orange").equals(element)) {
							return new SimpleArooaClass(Orange.class);
						}
						return null;
					}
				}, null);
		}
		
		@Override
		public ArooaBeanDescriptor getBeanDescriptor(
				ArooaClass classIdentifier, PropertyAccessor accessor) {
			return new MockArooaBeanDescriptor() {
				@Override
				public String getComponentProperty() {
					return "fruit";
				}
				@Override
				public ParsingInterceptor getParsingInterceptor() {
					return null;
				}
				@Override
				public ConfiguredHow getConfiguredHow(String property) {
					return ConfiguredHow.ELEMENT;
				}
			};
		}
	}
	
	public void testCutting() throws Exception {
		
		String xml = "<snack>" +
				"<fruit>" +
				"	<apple/>" +
				"</fruit>" +
				"</snack>";

		Snack snack = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		
		ArooaContext appleContext = session.getComponentPool().contextFor(
				snack.fruit);
		
		test.cut(appleContext);
		
		assertNull(snack.fruit);
		
		String expected = "<snack/>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected, xmlParser.getXml());
		
	}
	
	public void testPasting() throws Exception {
		
		String xml = "<snack/>";

		Snack snack = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		
		assertTrue(test.supportsPaste());
		
		
		test.paste(0, new XMLConfiguration("PASTE", "<orange/>"));
		
		assertNotNull(snack.fruit);
		
		String expected = "<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected, xmlParser.getXml());
		
	}

	public void testPastingOverExisting() throws Exception {
		
		String xml = "<snack>" +
			"<fruit>" +
			"	<apple/>" +
			"</fruit>" +
			"</snack>";

		Snack snack = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		try {	
			test.paste(0, new XMLConfiguration("PASTE", "<orange/>"));
			fail("Shouldn't fail because a simple property can only have one value.");
		} catch (ArooaParseException e) {
			//expected
		}
		
	}
	
	public void testReplace() throws Exception {
		
		String xml = 
			"<snack>" +
			"<fruit>" +
			"	<orange/>" +
			"</fruit>" +
			"</snack>";

		Snack snack = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		ArooaContext orangeContext = session.getComponentPool().contextFor(
				snack.fruit);
		
		test.replace(orangeContext, new XMLConfiguration("PASTE", "<apple/>"));

		assertEquals(Apple.class, snack.fruit.getClass());
		
		String expected = "<snack>" + LS +
				"    <fruit>" + LS + 
				"        <apple/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected, xmlParser.getXml());
		
		
		// Sanity check the replace by replacing the orange back again.
		
		ArooaContext appleContext = session.getComponentPool().contextFor(
				snack.fruit);
		
		test.replace(appleContext, new XMLConfiguration("PASTE", "<orange/>"));

		assertEquals(Orange.class, snack.fruit.getClass());
		
		String expected2 = "<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
				
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected2, xmlParser.getXml());
		
	}
	
	public void testReplaceRoot() throws ArooaParseException {
		
		String xml = 
			"<snack/>";

		Snack snack = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		XMLConfiguration config = new XMLConfiguration("TEST", xml);
		
		ConfigurationHandle handle = parser.parse(config);
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport.replace(snackContext.getParent(), snackContext, new XMLConfiguration("TEST2", xml));

		// snack hasn't changed but the context should have
		
		ArooaContext snackContext2 = session.getComponentPool().contextFor(
				snack);
		
		assertFalse(snackContext == snackContext2);

		// Sanity check the replace by a cut.
		
		CutAndPasteSupport.cut(snackContext2.getParent(), snackContext2);

		try {
			handle.save();
			
			fail("Configuration should be empty.");
		} catch (NullPointerException e) {
			// expected.
		}
		
	}
	
	
	public void testBadReplace() throws Exception {
		
		String xml = 
			"<snack>" + LS +
			"    <fruit>" + LS + 
			"        <orange/>" + LS + 
			"    </fruit>" + LS + 
			"</snack>" + LS;

		Snack snack = new Snack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());

		XMLConfiguration config = new XMLConfiguration("TEST", xml); 
		
		ConfigurationHandle handle = parser.parse(config);
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		ArooaContext orangeContext = session.getComponentPool().contextFor(
				snack.fruit);
		
		CutAndPasteSupport.ReplaceResult result = test.replace(orangeContext, new XMLConfiguration("PASTE", "<rubbish/>"));
		assertNotNull(result.getException());
		
		assertEquals(Orange.class, snack.fruit.getClass());
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(xml, xmlParser.getXml());
		
		final AtomicReference<String > savedXML = new AtomicReference<String>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		// check it will save - no reason why it shouldn't...
		handle.save();
		
		assertXMLEqual(xml, savedXML.get());

		// check what's there is still valid by cutting it.
		ArooaContext orangeContext2 = session.getComponentPool().contextFor(
				snack.fruit);
		
		test.cut(orangeContext2);
		
		assertNull(snack.fruit);
	}
	
	
	public static class IndexedSnack {
		
		List<Fruit> fruit = new ArrayList<Fruit>();
		
		public void setFruit(int index, Fruit fruit) {
			if (fruit == null) {
				this.fruit.remove(index);
			}
			else {
				this.fruit.add(index, fruit);
			}
		}		
	}
	
	
	public void testIndexedCutting() throws Exception {
		
		String xml = "<snack>" +
				"<fruit>" +
				"	<apple/>" +
				"	<orange/>" +
				"</fruit>" +
				"</snack>";

		IndexedSnack snack = new IndexedSnack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		ArooaContext appleContext = session.getComponentPool().contextFor(
				snack.fruit.get(0));
		
		test.cut(appleContext);
				
		assertEquals(1, snack.fruit.size());
		assertEquals(Orange.class, snack.fruit.get(0).getClass());
		
		String expected = "<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected, xmlParser.getXml());
		
	}

	public void testIndexedPasting() throws Exception {
		
		String xml = "<snack>" +
			"<fruit>" +
			"	<apple/>" +
			"	<orange/>" +
			"</fruit>" +
			"</snack>";

		IndexedSnack snack = new IndexedSnack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		
		assertTrue(test.supportsPaste());
		
		test.paste(0, new XMLConfiguration("PASTE", "<orange/>"));

		assertEquals(3, snack.fruit.size());
		assertEquals(Orange.class, snack.fruit.get(0).getClass());
		assertEquals(Apple.class, snack.fruit.get(1).getClass());
		assertEquals(Orange.class, snack.fruit.get(2).getClass());
		
		
		String expected = "<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange/>" + LS + 
				"        <apple/>" + LS + 
				"        <orange/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected, xmlParser.getXml());	
	}	
	
	public void testIndexedReplace() throws Exception {
		
		String xml = 
			"<snack>" +
			"<fruit>" +
			"	<apple/>" +
			"	<orange/>" +
			"</fruit>" +
			"</snack>";

		IndexedSnack snack = new IndexedSnack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		ArooaContext orangeContext = session.getComponentPool().contextFor(
				snack.fruit.get(1));
		
		test.replace(orangeContext, new XMLConfiguration("PASTE", "<apple/>"));

		assertEquals(2, snack.fruit.size());
		assertEquals(Apple.class, snack.fruit.get(0).getClass());
		assertEquals(Apple.class, snack.fruit.get(1).getClass());
		
		
		String expected = "<snack>" + LS +
				"    <fruit>" + LS + 
				"        <apple/>" + LS + 
				"        <apple/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected, xmlParser.getXml());
	
	}
	
	
	public static class MappedSnack {
		
		Map<String, Fruit> fruit = new HashMap<String, Fruit>();
		
		public void setFruit(String key, Fruit fruit) {
			if (fruit == null) {
				this.fruit.remove(key);
			}
			else {
				this.fruit.put(key, fruit);
			}
		}		
	}
	
	
	public void testMappedCutting() throws Exception {
		
		String xml = "<snack>" +
				"<fruit>" +
				"	<apple key='red'/>" +
				"	<orange key='orange'/>" +
				"</fruit>" +
				"</snack>";

		MappedSnack snack = new MappedSnack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		ArooaContext appleContext = session.getComponentPool().contextFor(
				snack.fruit.get("red"));
		
		test.cut(appleContext);
				
		assertEquals(1, snack.fruit.size());
		assertEquals(Orange.class, snack.fruit.get("orange").getClass());
		
		String expected = "<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange key=\"orange\"/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected, xmlParser.getXml());
		
	}

	public void testMappedPasting() throws Exception {
		
		String xml = "<snack>" +
			"<fruit>" +
			"	<apple key='red'/>" +
			"	<orange key='orange'/>" +
			"</fruit>" +
			"</snack>";

		MappedSnack snack = new MappedSnack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		
		assertTrue(test.supportsPaste());
		
		test.paste(0, new XMLConfiguration("PASTE", "<orange key='yellow'/>"));

		assertEquals(3, snack.fruit.size());
		assertEquals(Orange.class, snack.fruit.get("yellow").getClass());
		assertEquals(Apple.class, snack.fruit.get("red").getClass());
		assertEquals(Orange.class, snack.fruit.get("orange").getClass());
		
		
		String expected = "<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange key=\"yellow\"/>" + LS + 
				"        <apple key=\"red\"/>" + LS + 
				"        <orange key=\"orange\"/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected, xmlParser.getXml());	
	}	
	
	public void testMappedReplace() throws Exception {
		
		String xml = 
			"<snack>" +
			"<fruit>" +
			"	<apple key='red'/>" +
			"	<orange key='orange'/>" +
			"</fruit>" +
			"</snack>";

		MappedSnack snack = new MappedSnack();
		
		StandardArooaParser parser = new StandardArooaParser(
				snack, new OurDescriptor());
		
		parser.parse(new XMLConfiguration("TEST", xml));
		
		ArooaSession session = parser.getSession();
		
		ArooaContext snackContext = session.getComponentPool().contextFor(
				snack);
		
		CutAndPasteSupport test = new CutAndPasteSupport(snackContext);

		ArooaContext orangeContext = session.getComponentPool().contextFor(
				snack.fruit.get("orange"));
		
		test.replace(orangeContext, new XMLConfiguration("PASTE", "<apple key='green'/>"));

		assertEquals(2, snack.fruit.size());
		assertEquals(Apple.class, snack.fruit.get("red").getClass());
		assertEquals(Apple.class, snack.fruit.get("green").getClass());
		
		
		String expected = "<snack>" + LS +
				"    <fruit>" + LS + 
				"        <apple key=\"red\"/>" + LS + 
				"        <apple key=\"green\"/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser();
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertXMLEqual(expected, xmlParser.getXml());
	
	}
	
}
