package org.oddjob.arooa.types;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for ListType.
 */
public class MapTypeTest {

	private static final Logger logger = LoggerFactory.getLogger(MapTypeTest.class);
	
	@Rule public TestName name = new TestName();

	public String getName() {
        return name.getMethodName();
    }

    @Before
    public void setUp() throws Exception {
		logger.info("-----------------------  " + getName() + 
				"  ----------------------------");
	}

   @Test
	public void testMapAssumptions() {
		
		Map<String, String> test = new LinkedHashMap<>();
		test.put("A", "Apple");
		test.put("B", "Ball");
		test.put("C", "Cat");
		
		Iterator<Map.Entry<String, String>> it =
				new ArrayList<>(test.entrySet()).iterator();
		
		Map.Entry<String, String> entry = it.next();		
		assertThat(entry.getValue(), is("Apple"));
		
		test.put("B", "Bag");
		
		entry = it.next();		
		assertThat(entry.getValue(), is("Bag"));
		
		test.remove("C");
		
		test.put("D", "Dog");
		
		entry = it.next();
	   assertThat(entry.getValue(), is("Cat"));
		
		assertThat(it.hasNext(), is(false));
	}
	
   @Test
	public void testDefaultConversions() 
	throws NoConversionAvailableException, ConversionFailedException {
		
		MapType test = new MapType();
		
		ArooaConverter converter = new DefaultConverter();
		
		// Check Object conversion is a map.
		Object o = converter.convert(test, Object.class);
		
		assertThat(Map.class.isAssignableFrom(o.getClass()), is(true));
		
		// Check iterable conversion.
		Iterable<?> iterable = converter.convert(test, Iterable.class);
		Iterator<?> iterator = iterable.iterator();
		assertThat(iterator.hasNext(), is(false));
	}
	
   @Test
	public void testConvertContents() throws Exception {
		MapType test = new MapType();

		test.setValues("one", new ArooaObject("1"));
		test.setValues("two", new ArooaObject("2"));
		
		ArooaConverter converter = new DefaultConverter();
		Map<String, Integer> result = 
				test.convertContents(converter, Integer.class);
		
		assertThat(result.get("one"), is(1));
		assertThat(result.get("two"), is(2));
	}
	
	/**
	 * Test a MapType can be a Map, and that the
	 * containing ArooaValue is converted to it's
	 * simplest type (i.e. doesn't stay an ArooaValue).
	 * 
	 * @throws Exception
	 */
   @Test
	public void testGettingAsMap() throws Exception {

		MapType test = new MapType();

		ValueType content = new ValueType();
		content.setValue(new ArooaObject("test"));
		test.setValues("x", content);

		DefaultConverter converter = new DefaultConverter();
		
		@SuppressWarnings("unchecked")
		Map<String, Object> result = converter.convert(
				test, Map.class);
		
		assertThat(result.size(), is(1));
		
		assertThat(result, Matchers.hasEntry("x", "test"));
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
   @Test
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
		
	    assertThat(root.results, notNullValue());
	    	    
	    assertThat("The object.", root.results.get("one").getClass(), is( A.class));
	    assertThat("The string.",  root.results, hasEntry("two", "apple"));
	}

   @Test
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
		
	    assertThat(root.results, notNullValue());
	    assertThat("Num results", root.results.size(), is(2));
	    assertThat("Orange.", root.results, hasEntry("one", "orange"));
	    assertThat("The string.", root.results, hasEntry("two", "apple"));
	}
	
	/**
	 * Configure/Destroy sets null values. How map cope?
	 * 
	 * @throws Exception
	 */
   @Test
	public void testNullsAndReconfiguring() throws Exception {
		String EOL = System.lineSeparator();
		
		String xml= 
			"<root>" + EOL +
			" <results>" + EOL +
			"  <map>" + EOL +
			"   <values>" + EOL +
			"    <value key='one'/>" + EOL +
			"    <value key='two' value='apple'/>" +
			"   </values>" + EOL +
			"  </map>" + EOL +
			"</results>" + EOL +
			"</root>" + EOL;
		
		Root1 root = new Root1();
		
		ArooaConfiguration config = new XMLConfiguration("Test", xml);
		
		StandardArooaParser parser = new StandardArooaParser(
				root);
	    
		parser.parse(config);
		
	    assertThat(root.results, nullValue());
	    
		ArooaSession session = parser.getSession();
		session.getComponentPool().configure(root);
		
	    assertThat(root.results, notNullValue());
	    	    
	    Map<String, Object> results =  root.results;

	    assertThat(results.size(), is(1));
	    
	    assertThat(results.get("one"), nullValue());
	    assertThat(results, hasEntry("two", "apple"));
	    
		session.getComponentPool().contextFor(
				root).getRuntime().destroy();

		// Destroy no long set null
	    assertThat(root.results, is(results));
	}
	
	@SuppressWarnings("unchecked")
   @Test
	public void testAddingValues() throws NoConversionAvailableException, ConversionFailedException {
		
		MapType test = new MapType();
		test.setAdd("one", new ArooaObject("Apple"));
		test.setAdd("two", new ArooaObject("Pear"));
		
		DefaultConverter converter = new DefaultConverter();
		
		Map<String, ?> mapResult = converter.convert(
				test, Map.class);

		assertThat(mapResult.size(), is(2));
		
		assertThat(mapResult, hasEntry("one", "Apple"));
		assertThat(mapResult, hasEntry("two", "Pear"));
		
		test.configured();
		
		mapResult = converter.convert(
				test, Map.class);

		assertThat(mapResult.isEmpty(), is(true));
		
		test.setAdd("three", new ArooaObject("Orange"));
		
		mapResult = converter.convert(
				test, Map.class);

		assertThat(mapResult.size(), is(1));
		
		assertThat(mapResult, hasEntry("three", "Orange"));
	}
}
