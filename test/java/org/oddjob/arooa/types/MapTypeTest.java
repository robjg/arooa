package org.oddjob.arooa.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.deploy.ConfigurationDescriptorFactory;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * Tests for ListType.
 */
public class MapTypeTest extends TestCase {

	private static final Logger logger = Logger.getLogger(MapTypeTest.class);
	
	@Override
	protected void setUp() throws Exception {
		logger.info("-----------------------  " + getName() + 
				"  ----------------------------");
	}

	public void testMapAssumptions() {
		
		Map<String, String> test = new LinkedHashMap<String, String>();
		test.put("A", "Apple");
		test.put("B", "Ball");
		test.put("C", "Cat");
		
		Iterator<Map.Entry<String, String>> it = 
				new ArrayList<Map.Entry<String, String>>(test.entrySet()
						).iterator();
		
		Map.Entry<String, String> entry = it.next();		
		assertEquals("Apple", entry.getValue());
		
		test.put("B", "Bag");
		
		entry = it.next();		
		assertEquals("Bag", entry.getValue());
		
		test.remove("C");
		
		test.put("D", "Dog");
		
		entry = it.next();		
		assertEquals("Cat", entry.getValue());
		
		assertEquals(false, it.hasNext());
	}
	
	public void testDefaultConversions() 
	throws NoConversionAvailableException, ConversionFailedException {
		
		MapType test = new MapType();
		
		ArooaConverter converter = new DefaultConverter();
		
		// Check Object conversion is a map.
		Object o = converter.convert(test, Object.class);
		
		assertTrue(Map.class.isAssignableFrom(o.getClass()));
		
		// Check iterable conversion.
		Iterable<?> iterable = converter.convert(test, Iterable.class);
		Iterator<?> iterator = iterable.iterator();
		assertEquals(false, iterator.hasNext());
	}
	
	public void testConvertContents() throws Exception {
		MapType test = new MapType();

		test.setValues("one", new ArooaObject("1"));
		test.setValues("two", new ArooaObject("2"));
		
		ArooaConverter converter = new DefaultConverter();
		Map<String, Integer> result = 
				test.convertContents(converter, Integer.class);
		
		assertEquals(new Integer(1), result.get("one"));
		assertEquals(new Integer(2), result.get("two"));
	}
	
	/**
	 * Test a MapType can be a Map, and that the
	 * containing ArooaValue is converted to it's
	 * simplest type (i.e. doesn't stay an ArooaValue).
	 * 
	 * @throws Exception
	 */
	public void testGettingAsMap() throws Exception {

		MapType test = new MapType();

		ValueType content = new ValueType();
		content.setValue(new ArooaObject("test"));
		test.setValues("x", content);

		DefaultConverter converter = new DefaultConverter();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = converter.convert(
				test, Map.class);
		
		assertEquals(1, result.size());
		
		assertEquals("test", result.get("x"));
	}
	
	
	public static class Root1 {
		public Map<String, Object> results;
		public void setResults(Map<String, Object> results) {
			this.results = results;
		}
	}
	
	public static class A {
	}
	
	
	/**
	 * Test that a map of mixed types gets resolved.
	 * 
	 * @throws Exception
	 */
	public void testObjects() throws Exception {
		
		String descriptorXML =
				"<arooa:descriptor xmlns:arooa='http://rgordon.co.uk/oddjob/arooa'>" +
			    " <values>" +
			    "  <arooa:bean-def element='a'" +
			    "      className='" + A.class.getName() + "'>" +
        		"  </arooa:bean-def>" +
        		" </values>" +
        		"</arooa:descriptor>";
		
		ArooaDescriptor descriptor = new ConfigurationDescriptorFactory(
				new XMLConfiguration("XML", descriptorXML)).createDescriptor(
								getClass().getClassLoader());
		
		
		String EOL = System.getProperty("line.separator");
		
		String xml= 
			"<root>" + EOL +
			" <results>" + EOL +
			"  <map>" + EOL +
			"   <values>" + EOL +
			"    <a key='one'/>\n" + EOL +
			"    <value key='two' value='apple'/>\n" +
			"   </values>" + EOL +
			"  </map>\n" + EOL +
			"</results>\n" + EOL +
			"</root>\n" + EOL; 
		
		Root1 root = new Root1();
		
		ArooaConfiguration config = new XMLConfiguration("Test", xml);
		
		StandardArooaParser parser = new StandardArooaParser(
				root, descriptor);
	    
		parser.parse(config);
		
		ArooaSession session = parser.getSession();
		session.getComponentPool().configure(root);
		
	    assertNotNull(root.results);
	    	    
	    assertEquals("The object.", A.class, root.results.get("one").getClass());
	    assertEquals("The string.", "apple", root.results.get("two"));
	}

	public void testStrings() throws Exception {

		String xml= "<root>\n" +
			"<results>" +
			" <map>" +
			"   <values>" +
			"    <value key='one' value='orange'/>" +
			"    <value key='two' value='apple'/>" +
			"  </values>" +
			" </map>" +
			"</results>" +
			"</root>";
	
		Root1 root = new Root1();
		
		ArooaConfiguration config = new XMLConfiguration("Test", xml);
		
		StandardArooaParser parser = new StandardArooaParser(root);
	    
		parser.parse(config);
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(root);
		
	    assertNotNull(root.results);
	    assertEquals("Num results", 2, root.results.size());
	    assertEquals("Orange.", "orange", root.results.get("one"));
	    assertEquals("The string.", "apple", root.results.get("two"));	    
	}
	
	/**
	 * Configure/Destroy sets null values. How map cope?
	 * 
	 * @throws Exception
	 */
	public void testNullsAndReconfiguring() throws Exception {
		String EOL = System.getProperty("line.separator");
		
		String xml= 
			"<root>" + EOL +
			" <results>" + EOL +
			"  <map>" + EOL +
			"   <values>" + EOL +
			"    <value key='one'/>\n" + EOL +
			"    <value key='two' value='apple'/>\n" +
			"   </values>" + EOL +
			"  </map>\n" + EOL +
			"</results>\n" + EOL +
			"</root>\n" + EOL; 
		
		Root1 root = new Root1();
		
		ArooaConfiguration config = new XMLConfiguration("Test", xml);
		
		StandardArooaParser parser = new StandardArooaParser(
				root);
	    
		parser.parse(config);
		
	    assertNull(root.results);
	    
		ArooaSession session = parser.getSession();
		session.getComponentPool().configure(root);
		
	    assertNotNull(root.results);
	    	    
	    Map<String, Object> results =  root.results;

	    assertEquals(1, results.size());
	    
	    assertEquals(null, results.get("one"));
	    assertEquals("apple", results.get("two"));
	    
		session.getComponentPool().contextFor(
				root).getRuntime().destroy();
		
	    assertNull(root.results);
	}
	
	@SuppressWarnings("unchecked")
	public void testAddingValues() throws NoConversionAvailableException, ConversionFailedException {
		
		MapType test = new MapType();
		test.setAdd("one", new ArooaObject("Apple"));
		test.setAdd("two", new ArooaObject("Pear"));
		
		DefaultConverter converter = new DefaultConverter();
		
		Map<String, ?> mapResult = converter.convert(
				test, Map.class);

		assertEquals(2, mapResult.size());
		
		assertEquals("Apple", mapResult.get("one"));
		assertEquals("Pear", mapResult.get("two"));		
		
		test.configured();
		
		mapResult = converter.convert(
				test, Map.class);

		assertEquals(0, mapResult.size());
		
		test.setAdd("three", new ArooaObject("Orange"));
		
		mapResult = converter.convert(
				test, Map.class);

		assertEquals(1, mapResult.size());
		
		assertEquals("Orange", mapResult.get("three"));
	}
}
