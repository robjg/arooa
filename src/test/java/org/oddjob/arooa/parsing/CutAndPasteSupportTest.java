package org.oddjob.arooa.parsing;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class CutAndPasteSupportTest {

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
				@Override
				public ArooaAnnotations getAnnotations() {
					return new NoAnnotations();
				}
			};
		}
	}
	
   @Test
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
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + LS +
				"<snack/>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isIdenticalTo(expected));
		
	}
	
    @Test
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
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + LS +
				"<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isIdenticalTo(expected));
		
	}

    @Test
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
	
   @Test
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
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + LS +
				"<snack>" + LS +
				"    <fruit>" + LS + 
				"        <apple/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isIdenticalTo(expected));
		
		
		// Sanity check the replace by replacing the orange back again.
		
		ArooaContext appleContext = session.getComponentPool().contextFor(
				snack.fruit);
		
		test.replace(appleContext, new XMLConfiguration("PASTE", "<orange/>"));

		assertEquals(Orange.class, snack.fruit.getClass());
		
		String expected2 = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + LS +
				"<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
				
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isIdenticalTo(expected2));
		
	}
	
   @Test
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
	
	
   @Test
	public void testBadReplace() throws Exception {
		
		String xml = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + LS +
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
		
		CutAndPasteSupport.ReplaceResult result =
				test.replace(orangeContext,
                             new XMLConfiguration("PASTE",
                                                  "<rubbish/>"));
		assertNotNull(result.getException());
		
		assertEquals(Orange.class, snack.fruit.getClass());
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isIdenticalTo(xml));
		
		final AtomicReference<String > savedXML = new AtomicReference<String>();
		config.setSaveHandler(new XMLConfiguration.SaveHandler() {
			@Override
			public void acceptXML(String xml) {
				savedXML.set(xml);
			}
		});
		
		// check it will save - no reason why it shouldn't...
		handle.save();
		
		assertThat(savedXML.get(), isIdenticalTo(xml));

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
	
	
   @Test
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
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + LS +
				"<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isIdenticalTo(expected));
		
	}

    @Test
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
		
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + LS +
				"<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange/>" + LS + 
				"        <apple/>" + LS + 
				"        <orange/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isIdenticalTo(expected));
	}	
	
    @Test
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
		
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + LS +
				"<snack>" + LS +
				"    <fruit>" + LS + 
				"        <apple/>" + LS + 
				"        <apple/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isIdenticalTo(expected));
	
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
	
	
    @Test
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
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isSimilarTo(expected));
		
	}

   @Test
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
		
		
		String expected = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + LS +
				"<snack>" + LS +
				"    <fruit>" + LS + 
				"        <orange key=\"yellow\"/>" + LS + 
				"        <apple key=\"red\"/>" + LS + 
				"        <orange key=\"orange\"/>" + LS + 
				"    </fruit>" + LS + 
				"</snack>" + LS;
		
		XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isIdenticalTo(expected));	
	}	
	
   @Test
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
		
		XMLArooaParser xmlParser = new XMLArooaParser(session.getArooaDescriptor());
	
		xmlParser.parse(
				session.getComponentPool().contextFor(snack).getConfigurationNode());
		
		assertThat(xmlParser.getXml(), isSimilarTo(expected));
	
	}
	
}
