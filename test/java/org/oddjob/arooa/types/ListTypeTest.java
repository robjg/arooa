/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.types;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.convert.convertlets.CollectionConvertlets;
import org.oddjob.arooa.deploy.ConfigurationDescriptorFactory;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * Tests for ListType.
 */
public class ListTypeTest extends Assert {

	private static final Logger logger = LoggerFactory.getLogger(ListTypeTest.class);
	
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
	public void testDefaultConversions() 
	throws NoConversionAvailableException, ConversionFailedException {
		
		ListType test = new ListType();
		
		ArooaConverter converter = new DefaultConverter();
		
		// Check conversion to Object is a List.
		Object o = converter.convert(test, Object.class);
		
		assertTrue(List.class.isAssignableFrom(o.getClass()));
		
		// Check Conversion to Iterable.
		@SuppressWarnings("rawtypes")
		ConversionPath<ListType, Iterable> conversion = 
				converter.findConversion(ListType.class, Iterable.class);
		assertEquals("ListType-Iterable", conversion.toString());
		
		Iterable<?> iterable = converter.convert(test, Iterable.class);
		Iterator<?> it = iterable.iterator();
		assertEquals(false, it.hasNext());
		
		// Is lack of conversion to String a problem?
		ConversionPath<ListType, String> conversionToString = 
				converter.findConversion(ListType.class, String.class);
		assertEquals(null, conversionToString);
	}
	
   @Test
	public void testConvertContents() throws Exception {
		ListType test = new ListType();
		
		
		test.convertContents(new ArooaConverter() {
			@SuppressWarnings("unchecked")
			public <F, T> T convert(F from, Class<T> required) 
			throws NoConversionAvailableException ,ConversionFailedException {
				assertEquals(String[].class, required);
				return (T) new String[] { (String) from };
			};
			public <F, T> ConversionPath<F, T> findConversion(Class<F> from,
					Class<T> to) {
				throw new RuntimeException("Unexpected!");
			}
		}, String[].class);
	}
	
	/**
	 * Test a ListType can be a List, and that the
	 * containing ArooaValue is converted to it's
	 * simplest type (i.e. doesn't stay an ArooaValue).
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
   @Test
	public void testGettingAsList() throws Exception {
		ListType test = new ListType();

		ValueType content = new ValueType();
		content.setValue(new ArooaObject("test"));
		test.setValues(0, content);

		DefaultConverter converter = new DefaultConverter();
		List<Object> result = converter.convert(
				test, List.class);
		
		assertEquals(1, result.size());
		
		assertEquals("test", result.get(0));
	}

	/**
	 * Test a ListType can be an Object[], and that the
	 * containing ArooaValue is converted to it's
	 * simplest type (i.e. doesn't stay an ArooaValue).
	 * 
	 * @throws Exception
	 */
   @Test
	public void testGettingAsObjectArray() throws Exception {
		ListType test = new ListType();
		
		ValueType content = new ValueType();
		content.setValue(new ArooaObject("test"));
		test.setValues(0, content);
		
		DefaultConverter converter = new DefaultConverter();
		
		Object[] result = converter.convert(test, Object[].class);
		
		assertEquals(1, result.length);
		
		assertEquals("test", result[0]);
	}
	
	/**
	 * Test that when the content type is String, a conversion
	 * to a String[] is possible.
	 * 
	 * @throws Exception
	 */
   @Test
	public void testGetAsStringArray() throws Exception {
		ListType test = new ListType();

		ValueType content = new ValueType();
		content.setValue(new ArooaObject("test"));		
		test.setValues(0, content);
		
		DefaultConverter converter = new DefaultConverter();
		
		String[] result = converter.convert(
				test, String[].class);
		
		assertEquals(1, result.length);
		
		assertEquals("test", result[0]);
	}
	
	/**
	 * Test that when the content is convertable to an int
	 * a conversion type int[] is possible.
	 * 
	 * @throws Exception
	 */
   @Test
	public void testGetArrayOfInts() throws Exception {
		ListType test = new ListType();
		
		ValueType av1 = new ValueType();
		av1.setValue(new ArooaObject("1"));
		test.setValues(0, av1);
		
		ValueType av2 = new ValueType();
		av2.setValue(new ArooaObject("2"));
		test.setValues(1, av2);
		
		int[] result = (int[]) 
				new DefaultConverter().convert(
						test, int[].class);
		
		assertEquals(2, result.length);
		
		assertEquals(1, result[0]);
		assertEquals(2, result[1]);
	}
	
   @Test
	public void testConvertToListOfObjects() 
	throws ArooaConversionException {

		ListType.ToListConverter<Object> test = 
			new ListType.ToListConverter<Object>(
					Object.class, new DefaultConverter());
		
		List<Object> results;
		
		results = test.convert(new String[] { "apples", "oranges" });
		
		assertEquals(2, results.size());
		assertEquals("apples", results.get(0));
		assertEquals("oranges", results.get(1));
		
		
		results = test.convert(Arrays.asList("apples", "oranges"));
		
		assertEquals(2, results.size());
		assertEquals("apples", results.get(0));
		assertEquals("oranges", results.get(1));
		
		results = test.convert("apples");
		
		assertEquals(1, results.size());
		assertEquals("apples", results.get(0));
		
		results = test.convert(null);
		
		assertEquals(1, results.size());
		assertEquals(null, results.get(0));
		
		results = test.convert(new int[] { 3, 5});
		
		assertEquals(2, results.size());
		assertEquals(3, results.get(0));
		assertEquals(5, results.get(1));
		
		results = test.convert(Arrays.asList(
				new ArooaObject("apples"), new ArooaObject("oranges")));
		
		assertEquals(2, results.size());
		assertEquals("apples", results.get(0));
		assertEquals("oranges", results.get(1));
	}
	
   @Test
	public void testConvertToListOfString() 
	throws ArooaConversionException {

		ListType.ToListConverter<String> test = 
			new ListType.ToListConverter<String>(
					String.class, new DefaultConverter());
		
		List<String> results;
		
		results = test.convert(new String[] { "apples", "oranges" });
		
		assertEquals(2, results.size());
		assertEquals("apples", results.get(0));
		assertEquals("oranges", results.get(1));
		
		
		results = test.convert(Arrays.asList("apples", "oranges"));
		
		assertEquals(2, results.size());
		assertEquals("apples", results.get(0));
		assertEquals("oranges", results.get(1));
		
		results = test.convert("apples");
		
		assertEquals(1, results.size());
		assertEquals("apples", results.get(0));
		
		results = test.convert(null);
		
		assertEquals(1, results.size());
		assertEquals(null, results.get(0));
		
		results = test.convert(new int[] { 3, 5});
		
		assertEquals(2, results.size());
		assertEquals("3", results.get(0));
		assertEquals("5", results.get(1));
		
		results = test.convert(Arrays.asList(
				new ArooaObject("apples"), new ArooaObject("oranges")));
		
		assertEquals(2, results.size());
		assertEquals("apples", results.get(0));
		assertEquals("oranges", results.get(1));
	}
	
   @Test
	public void testConvertDelimitedTextToListOfArrays() throws ClassNotFoundException, NoConversionAvailableException, ConversionFailedException {
		
		logger.info(String[].class.getName());
		
		ListType test = new ListType();
		test.setValues(0, new ArooaObject("a, b, c"));
		test.setValues(1, new ArooaObject("d, e, f"));
		test.setElementType(String[].class);
		
		ArooaConverter converter = new DefaultConverter();
		
		@SuppressWarnings("unchecked")
		List<String[]> list = converter.convert(
				test, List.class);
		
		String[] e1 = list.get(0);
		
		assertEquals("a", e1[0]);
		assertEquals("b", e1[1]);
		assertEquals("c", e1[2]);
		
		assertEquals(3, e1.length);
		
		String[] e2 = list.get(1);
		
		assertEquals("d", e2[0]);
		assertEquals("e", e2[1]);
		assertEquals("f", e2[2]);
		
		assertEquals(3, e2.length);
		
		assertEquals(2, list.size());
	}
	
   @Test
	public void testMergeList() throws Exception {
		List<String> list1 = new ArrayList<String>();
		list1.add("a");
		list1.add("b");
		
		ValueType content1 = new ValueType();
		content1.setValue(new ArooaObject(list1));

		List<String> list2 = new ArrayList<String>();
		list2.add("c");
		list2.add("d");
		
		ValueType content2 = new ValueType();
		content2.setValue(new ArooaObject(list2));
		
		ListType test = new ListType();
		test.setMerge(true);
		
		test.setValues(0, content1);
		test.setValues(1, content2);
		
		ArooaConverter converter = new DefaultConverter();
		
		List<?> listResult = converter.convert(
				test, List.class);

		assertEquals(4, listResult.size());
		
		assertEquals("a", listResult.get(0));
		assertEquals("b", listResult.get(1));
		assertEquals("c", listResult.get(2));
		assertEquals("d", listResult.get(3));
		
		String[] arrayResult = converter.convert(
				test, String[].class);
		
		assertEquals(4, arrayResult.length);
		
		assertEquals("a", arrayResult[0]);
		assertEquals("b", arrayResult[1]);
		assertEquals("c", arrayResult[2]);
		assertEquals("d", arrayResult[3]);
	}
	
   @Test
	public void testMergeUniqueArray() throws Exception {
		ValueType content1 = new ValueType();
		content1.setValue(
				new ArooaObject(new String[] { "a", "b" } ));
		
		ValueType content2 = new ValueType();
		content2.setValue(
				new ArooaObject(new String[] { "c", "a" } ));
		
		ListType test = new ListType();
		test.setMerge(true);
		test.setUnique(true);
		
		test.setValues(0, content1);
		test.setValues(1, content2);
		
		DefaultConverter converter = new DefaultConverter();
		
		List<?> listResult = converter.convert(
				test, List.class);

		assertEquals(3, listResult.size());
		
		assertEquals("a", listResult.get(0));
		assertEquals("b", listResult.get(1));
		assertEquals("c", listResult.get(2));
		
		Object[] arrayResult = converter.convert(
				test, Object[].class);

		assertEquals(3, arrayResult.length);
		
		assertEquals("a", arrayResult[0]);
		assertEquals("b", arrayResult[1]);
		assertEquals("c", arrayResult[2]);
	}

   @Test
	public void testMergeNulls() throws NoConversionAvailableException, ConversionFailedException {
		ValueType content1 = new ValueType();
		content1.setValue(
				new ArooaObject(new String[] { null } ));
		
		ValueType content2 = new ValueType();
		content2.setValue(null);
		
		ListType test = new ListType();
		test.setMerge(true);
		test.setUnique(true);
		
		test.setValues(0, content1);
		test.setValues(1, content2);

		DefaultConverter converter = new DefaultConverter();
		
		Object[] result = converter.convert(test, Object[].class);

		assertEquals(1, result.length);
		
		assertNull(result[0]);		
	}
	
   @Test
	public void testNoMergeArray() throws Exception {
		ValueType content1 = new ValueType();
		content1.setValue(
				new ArooaObject(new String[] { "a", "b" } ));
		
		ValueType content2 = new ValueType();
		content2.setValue(
				new ArooaObject(new String[] { "c", "d" } ));
		
		ListType test = new ListType();
		test.setMerge(false);

		test.setValues(0, content1);
		test.setValues(1, content2);
		
		DefaultConverter converter = new DefaultConverter();
		
		Object[] result = converter.convert(test, Object[].class);

		assertEquals(2, result.length);
		
		assertEquals(String[].class, result[0].getClass());
		
		assertEquals("a", ((String[]) result[0])[0]);
	}
	
   @Test
	public void testElementTypeSingle() throws NoConversionAvailableException, ConversionFailedException, ClassNotFoundException {
		
		ValueType element1 = new ValueType();
		element1.setValue(
				new ArooaObject("3"));
		
		ValueType element2 = new ValueType();
		element2.setValue(
				new ArooaObject("7"));
		
		ListType test = new ListType();
		test.setMerge(false);
		test.setUnique(false);
		test.setElementType(Integer.class);
		test.setValues(0, element1);
		test.setValues(1, element2);
		
		DefaultConverter converter = new DefaultConverter();
		
		List<?> listResult = converter.convert(
				test, List.class);

		assertEquals(2, listResult.size());
		
		assertEquals(new Integer(3), listResult.get(0));
		assertEquals(new Integer(7), listResult.get(1));
		
		Number[] arrayResult = converter.convert(
				test, Number[].class);

		assertEquals(2, arrayResult.length);
		
		assertEquals(3, arrayResult[0]);
		assertEquals(7, arrayResult[1]);
		
		try {
			converter.convert(
					test, String[].class);
			fail("This isn't possilbe.");
		} catch (ArooaConversionException e) {
			// expected
		}
	}
	
	
	public static class Root1 {
		public List<Object> results;
		public void setResults(List<Object> results) {
			this.results = results;
		}
	}
	
	public static class Root2 {
		public String[] results;
		public void setResults(String[] results) {
			this.results = results;
		}
	}

	public static class Root3 {
		public int[] results;
		public void setResults(int[] results) {
			this.results = results;
		}
	}

	public static class A {
	}
	
	
	/**
	 * Test that a list of mixed types gets resolved.
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
			"  <list>" + EOL +
			"   <values>" + EOL +
			"    <a/>\n" + EOL +
			"    <value value='apple'/>\n" +
			"   </values>" + EOL +
			"  </list>\n" + EOL +
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
	    	    
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new CollectionConvertlets().registerWith(registry);
		
	    Object[] o = (Object[]) new DefaultConverter(registry).convert(
	    		root.results, Object[].class);

	    assertEquals("The object.", A.class, o[0].getClass());
	    assertEquals("The string.", "apple", o[1]);
	}

   @Test
	public void testStrings() throws Exception {

		String xml= "<root>\n" +
			"<results>" +
			" <list>" +
			"   <values>" +
			"    <value value='orange'/>" +
			"    <value value='apple'/>" +
			"  </values>" +
			" </list>" +
			"</results>" +
			"</root>";
	
		Root2 root = new Root2();
		
		ArooaConfiguration config = new XMLConfiguration("Test", xml);
		
		StandardArooaParser parser = new StandardArooaParser(root);
	    
		parser.parse(config);
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(root);
		
	    assertNotNull(root.results);
	    assertEquals("Num results", 2, root.results.length);
	    assertEquals("Orange.", "orange", root.results[0]);
	    assertEquals("The string.", "apple", root.results[1]);	    
	}
	
   @Test
	public void testUniqueInts() throws Exception {
		String xml= "<root>" +
			"<results>" +
			" <list unique='true'>" +
			"  <values>" +
			"   <value value='1'/>" +
			"   <value value='2'/>" +
			"   <value value='1'/>" +
			"  </values>" +
			" </list>" +
			"</results>" +
			"</root>";

		Root3 root = new Root3();

		ArooaConfiguration config = new XMLConfiguration("Test", xml);

		StandardArooaParser parser = new StandardArooaParser(root);

		parser.parse(config);
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(root);
		
	    assertNotNull(root.results);
	    assertEquals("Num results", 2, root.results.length);
	    assertEquals("Orange.", 1, root.results[0]);
	    assertEquals("The string.", 2, root.results[1]);	    
	}

	/**
	 * Configure/Destroy sets null values. How does list cope?
	 * 
	 * @throws Exception
	 */
   @Test
	public void testNullsAndReconfiguring() throws Exception {
		String EOL = System.getProperty("line.separator");
		
		String xml= 
			"<root>" + EOL +
			" <results>" + EOL +
			"  <list>" + EOL +
			"   <values>" + EOL +
			"    <value/>\n" + EOL +
			"    <value value='apple'/>\n" +
			"   </values>" + EOL +
			"  </list>\n" + EOL +
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
	    	    
	    List<Object> results =  root.results;

	    assertEquals(2, results.size());
	    
	    assertEquals(null, results.get(0));
	    assertEquals("apple", results.get(1));
	    
		session.getComponentPool().contextFor(
				root).getRuntime().destroy();
		
	    assertNull(root.results);
	}
	
   @Test
	public void testAddingValues() throws NoConversionAvailableException, ConversionFailedException {
		
		ListType test = new ListType();
		test.setAdd(new ArooaObject("Apple"));
		test.setAdd(new ArooaObject("Pear"));
		
		DefaultConverter converter = new DefaultConverter();
		
		List<?> listResult = converter.convert(
				test, List.class);

		assertEquals(2, listResult.size());
		
		assertEquals("Apple", listResult.get(0));
		assertEquals("Pear", listResult.get(1));		
		
		test.configured();
		
		listResult = converter.convert(
				test, List.class);

		assertEquals(0, listResult.size());
		
		test.setAdd(new ArooaObject("Orange"));
		
		listResult = converter.convert(
				test, List.class);

		assertEquals(1, listResult.size());
		
		assertEquals("Orange", listResult.get(0));
	}
}
