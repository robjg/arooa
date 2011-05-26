/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.beanutils;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.oddjob.arooa.ArooaConfigurationException;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.types.ArooaObject;

/**
 * Tests for BeanUtilsBeanHelper
 */
public class BeanUtilsPropertyAcessorTest extends TestCase {

	private static final ArooaConverter CONVERTER = new DefaultConverter();
	
	/**
	 * These tests were inherited from ant. They 
	 * test lots of different types of setters.
	 * 
	 * @throws ArooaException
	 * @throws ConversionFailedException 
	 * @throws NoConversionAvailableException 
	 */
    public void testAttributeSetters() throws ArooaException, NoConversionAvailableException, ConversionFailedException {
		
    	PropertyAccessor bubh = 
    		new BeanUtilsPropertyAccessor().accessorWithConversions(CONVERTER);
    	ThingWithAttributes subject = new ThingWithAttributes();
    	
        try {
            bubh.setProperty(subject, "one", "test");
            fail("setOne doesn't exist");
        } catch (ArooaPropertyException be) {
        }
        try {
        	bubh.setProperty(subject, "two", "test");
            fail("setTwo returns non void");
        } catch (ArooaPropertyException be) {
        }
        try {
        	bubh.setProperty(subject, "three", "test");
            fail("setThree takes no args");
        } catch (ArooaPropertyException be) {
        }
        
        try {
        	bubh.setProperty(subject, "four", "test");
            fail("setFour takes two args");
        } catch (ArooaConfigurationException be) {
        }
    
        // test setting null
        subject.five = "Not null";
        bubh.setProperty(subject, "five", null);
        assertNull(subject.five);
        
        // set string on String
        bubh.setProperty(subject, "five", "test");
        assertEquals("test", subject.five);
        
        // set File on String
        bubh.setProperty(subject, "five", new File("hello.txt"));        
        assertEquals("hello.txt", subject.five);
        
        // test setting null
        try {
        	bubh.setProperty(subject, "six", null);
        	fail("can't set an int null");
        } catch (ArooaConfigurationException e) {
        	// null
        }
        
        // set Integer on int
        bubh.setProperty(subject, "six", new Integer(2));
        assertEquals(2, subject.six);
        
        // set Integer on Integer
        bubh.setProperty(subject, "seven", new Integer(2));        
        assertEquals(new Integer(2), subject.seven);
        
        // set String on File
        bubh.setProperty(subject, "eight", "hello.txt");

        // set Object[] on File[]
        try {
        	bubh.setProperty(subject, "nine", new Object[] { "hello.txt" } );
        	fail("Should be no conversion between Object and File.");
        } catch (Exception e) {
        	// expected.
        }
        
        // set String[] on File[]
        bubh.setProperty(subject, "nine", new String[] { "hello.txt" } );        
    	assertEquals( new File("hello.txt"), subject.nine[0]);
        
	    // nine2 are extra tests because BeanUtils messes with String arrays.
    	// since 0.23.0 we do our own conversions so these aren't so applicable
    	// but the more tests the better.
    	
    	
        // set Object[] on String[]
        bubh.setProperty(subject, "nine2", new Object[] { "hello.txt" } );        
    	assertEquals( "hello.txt", subject.nine2[0]);
        
        // set File[] on String[]
        bubh.setProperty(subject, "nine2", new File[] { new File("hello.txt") } );        
    	assertEquals( "hello.txt", subject.nine2[0]);
        
        // set String[] on in[]
        bubh.setProperty(subject, "ten", new String[] { "1", "2", "3" });
        assertEquals(1, subject.ten[0]);
        assertEquals(2, subject.ten[1]);
        assertEquals(3, subject.ten[2]);
        
        bubh.setProperty(subject, "eleven[0]", "apples");
        bubh.setProperty(subject, "eleven[1]", "pairs");
        assertEquals("apples", subject.eleven[0]);
        assertEquals("pairs", subject.eleven[1]);
                
        // set File on an AV.
        bubh.setProperty(subject, "twelve", new File("hello.txt"));        
        assertEquals(new File("hello.txt"), CONVERTER.convert(subject.twelve, Object.class));
        
        bubh.setProperty(subject, "thirteen",
        		new String[] { "apples", "pairs" });
        assertEquals("apples", CONVERTER.convert(subject.thirteen[0], Object.class));
        assertEquals("pairs", CONVERTER.convert(subject.thirteen[1], Object.class));
    }

    /**
     * More complicated cases.
     *
     */
    public void testAttributeSetters2() {
    	PropertyAccessor bubh = 
    		new BeanUtilsPropertyAccessor().accessorWithConversions(CONVERTER);
    	Object subject = new ThingWithAttributes();
    	
        // set Object[] on File[]
        bubh.setSimpleProperty(subject, "nine", new ArooaObject[] { 
        		new ArooaObject(new File("hello.txt")), 
        		new ArooaObject(new File("goodbye.txt")) } );
    	
    }
    	
	public void testNestedSet() {
		OuterBean subject = new OuterBean();
		PropertyAccessor test = 
			new BeanUtilsPropertyAccessor().accessorWithConversions(CONVERTER);
		
        // test setting null
        subject.nested.five = "Not null";
        test.setProperty(subject, "nested.five", null);
        assertNull(subject.nested.five);
        
        // set string on String
        test.setProperty(subject, "nested.five", "test");
        assertEquals("test", subject.nested.five);
        
        
        // set File on String
        test.setProperty(subject, "nested.five", new File("hello.txt"));
        assertEquals("hello.txt", subject.nested.five);
        
        
        // set Integer on int
        test.setProperty(subject, "nested.six", new Integer(2));
        assertEquals(2, subject.nested.six);
        
        // set Integer on Integer
        test.setProperty(subject, "nested.seven", new Integer(2));        
        assertEquals(new Integer(2), subject.nested.seven);
        
        test.setProperty(subject, "nested.ten", new String[] { "1", "2", "3" });
        assertEquals(1, subject.nested.ten[0]);
        assertEquals(2, subject.nested.ten[1]);
        assertEquals(3, subject.nested.ten[2]);
		
	}	
    
	public void testNestedGet() {
		OuterBean subject = new OuterBean();
		PropertyAccessor test = 
			new BeanUtilsPropertyAccessor().accessorWithConversions(CONVERTER);
		
        // test setting null
        subject.nested.five = "five";
        
        assertEquals("five", test.getProperty(subject, "nested.five"));
        
        subject.nested.nine = new File[] { new File("nine.txt") };
        
        assertEquals(new File("nine.txt"), 
        		test.getProperty(subject, "nested.nine[0]"));
	}
	
	/**
	 * Test the setMappedProperty method.
	 * 
	 * @throws ArooaException
	 */
    public void testMappedProperties() throws ArooaException {
		
    	PropertyAccessor test = 
    		new BeanUtilsPropertyAccessor().accessorWithConversions(CONVERTER);
    	
    	ThingWithMappedProperties subject = new ThingWithMappedProperties();
    	
        try {
            test.setMappedProperty(subject, "one", "x", "test");
            fail("setOne doesn't exist");
        } catch (ArooaConfigurationException be) {
        }
        // test fails BeanUtils will use setter event if not return type void.
        // as it should do
//        try {
//        	bubh.setMappedProperty(subject, "two", "x", "test");
//            fail("setTwo returns non void");
//        } catch (ArooaException be) {
//        }
        try {
        	test.setMappedProperty(subject, "three", "x", "test");
            fail("setThree takes no args");
        } catch (ArooaConfigurationException be) {
        }
        
        try {
        	test.setMappedProperty(subject, "four", "x", "test");
            fail("setFour takes three args");
        } catch (ArooaConfigurationException be) {
        }
    
        // setting null
        subject.five.put("x", "test");
        test.setMappedProperty(subject, "five", "x", null);
        assertNull(subject.five.get("x"));
        
        // set string on String
        test.setMappedProperty(subject, "five", "x", "test");
        assertEquals("test", subject.five.get("x"));
        
        // set File on String
        test.setMappedProperty(subject, "five", "x", new File("hello.txt"));        
        assertEquals("hello.txt", subject.five.get("x"));
        
        // set Integer on int
        test.setMappedProperty(subject, "six", "x", new Integer(2));
        assertEquals(new Integer(2), subject.six.get("x"));
        
    }
    
    
    /**
     * Fixture - For test nested properties.
     * 
     */
	public static class OuterBean {
		ThingWithAttributes nested = new ThingWithAttributes();
		public ThingWithAttributes getNested() {
			return nested; 
		}
	}

    
    /**
     * Fixture - Object with lots of different types
     * of simple properties (and a few set methods
     * that aren't).
     *
     */
	public static class ThingWithAttributes {
		String five; 
		int six;
		Integer seven;
		File eight;
		File[] nine;
		String[] nine2;
		int[] ten;
		String[] eleven = new String[2];
		ArooaValue twelve;
		ArooaValue[] thirteen;
		
	    public int setTwo(String s) {
	        return 0;
	    }

	    public void setThree() {}

	    public void setFour(String s1, String s2) {}

	    public void setFive(String s) {
	    	five = s;
	    }
	    public String getFive() {
			return five;
		}
	    
	    public void setSix(int i) {
	    	six = i;
	    }
	    
	    public void setSeven(Integer i) {
	    	seven = i;
	    }
	    
	    public void setEight(File f) { 
	    	eight = f;
	    }
	    
	    public void setNine(File[] f) { 
	    	nine = f;
	    }
	    public File[] getNine() {
			return nine;
		}
	    
	    public void setNine2(String[] f) { 
	    	nine2 = f;
	    }
	    
	    public void setTen(int[] i) { 
	    	ten = i;
	    }
	    
	    public void set(Object o) { }
	    
	    public void setEleven(int index, String s) {
	    	eleven[index] = s;
	    }
	    
	    public void setTwelve(ArooaValue av) {
	    	this.twelve = av;
	    }
	    
	    public void setThirteen(ArooaValue[] ava) {
	    	this.thirteen = ava;
	    }

	}
	

    /**
     * Fixture - an object with mapped properties (as
     * apposed to properties that are a map).
     *
     */
	public static class ThingWithMappedProperties {
		Map<String, String> five = new HashMap<String, String>();
		Map<String, Integer> six = new HashMap<String, Integer>();
		Map<String, ArooaValue> seven = new HashMap<String, ArooaValue>();
		
	    public int setTwo(String s, String s2) {
	        return 0;
	    }
	    
	    public void setThree() {}

	    public void setFour(String s1, String s2, String s3) {}

	    public void setFive(String s, String v) {
	    	if (v == null) {
	    		// for the set null;
	    		five.remove(s);
	    	}
	    	else {
	    		five.put(s, v);
	    	}
	    }

	    public void setSix(String s, int i) {
	    	six.put(s, new Integer(i));
	    }
	    
	    public void setSeven(String key, ArooaValue av) {
	    	seven.put(key, av);
	    }
	}

	// Not sure when this might be useful.
	public void testMapProperties() {
		
		PropertyAccessor test = 
			new BeanUtilsPropertyAccessor();
		
		ThingWithMapProperties bean = new ThingWithMapProperties();
		
		test.setProperty(bean, "properties(a.b.c)", "apple");
		
		assertEquals("apple", test.getProperty(bean, "properties(a.b.c)"));
	}
	
	public static class ThingWithMapProperties {
		
		Properties properties = new Properties();
		
		public Properties getProperties() {
			return properties;
		}		
	}
	
	/**
	 * Test getting the type of a property.
	 *
	 */
	public void testPropertyType() {
    	BeanUtilsPropertyAccessor bubh = new BeanUtilsPropertyAccessor();
    	
		ThingWithAttributes t = new ThingWithAttributes();

		// [] property
		assertEquals(File[].class, bubh.getPropertyType(t, "nine"));
		
    	// none existent dyna property
		DynaBean db = new LazyDynaBean();
		assertEquals(Object.class, bubh.getPropertyType(db, "foo"));

	}
	
	public static class ObjectWithIndexedProperty {
		public void setArgs(String[] args) {
			
		}
		public String[] getArgs() {
			return null;
		}
		public void setArgs(int i, String arg) {
			
		}
		public String getArgs(int i) {
			return null;
		}
	}
	
	/**
	 * Tracking down a bug where args property of 
	 * Oddjob wasn't being set properly.
	 */
	public void testPropertyType2() throws Exception{
		// first look at an array without a indexed acessor.
		ThingWithAttributes thing = new ThingWithAttributes();
		
		PropertyDescriptor pd1 = PropertyUtils.getPropertyDescriptor(thing, "nine");
		assertNotNull("descriptor 1", pd1);
		assertFalse(pd1 instanceof IndexedPropertyDescriptor);
		
		// now look at Oddjob args that have an indexed setter.
		ObjectWithIndexedProperty oj = new ObjectWithIndexedProperty();
		
		BeanUtilsPropertyAccessor test = new BeanUtilsPropertyAccessor();

		PropertyDescriptor pd2 = PropertyUtils.getPropertyDescriptor(oj, "args");
		assertEquals(String[].class, pd2.getPropertyType());
		
		assertTrue(pd2 instanceof IndexedPropertyDescriptor);
		assertEquals(String.class, ((IndexedPropertyDescriptor) pd2).getIndexedPropertyType());

		// remember property type is content type
		// for indexed (and mapped) properties.
		Class<?> result = test.getPropertyType(oj, "args");
		assertEquals(String.class, result);		
	}
	
	static class MockDynaClass implements DynaClass {
		DynaProperty simple = new DynaProperty("simple", String.class);
		DynaProperty indexed = new DynaProperty("indexed", String[].class, String.class);
		DynaProperty mapped = new DynaProperty("mapped", Map.class, String.class);
		
		public DynaProperty[] getDynaProperties() {
			return new DynaProperty[] { simple, indexed, mapped };
		}
		public DynaProperty getDynaProperty(String name) {
			if (("simple").equals(name)) {
				return simple;
			}
			if (("indexed").equals(name)) {
				return indexed;
			}
			if (("mapped").equals(name)) {
				return mapped;
			}
			return null;
		}
		public String getName() {
			return toString();
		}
		public DynaBean newInstance() throws IllegalAccessException, InstantiationException {
			throw new RuntimeException("Unsupported");
		}
	}
    
    public static class MockDynaBean implements DynaBean {
    	String simple;
    	Map<String, Object> mapped = new HashMap<String, Object>();
    	String[] indexed = new String[2];
    	
    	DynaClass dynaClass = new MockDynaClass();
    	
    	public boolean contains(String name, String key) {
    		throw new RuntimeException("Unexpected.");
    	}
    	public Object get(String name) {
    		throw new RuntimeException("Unexpected.");
    	}
    	public Object get(String name, int index) {
    		throw new RuntimeException("Unexpected.");
    	}
    	public Object get(String name, String key) {
    		throw new RuntimeException("Unexpected.");
    	}
    	public DynaClass getDynaClass() {
    		return dynaClass;
    	}
    	public void remove(String name, String key) {
    		throw new RuntimeException("Unexpected.");
    	}
    	public void set(String name, int index, Object value) {
    		if (! "indexed".equals(name)) {
    			throw new RuntimeException("No index property");
    		}
    		indexed[index] = (String) value;
    	}
    	public void set(String name, Object value) {
    		if (! "simple".equals(name)) {
    			throw new RuntimeException("No simple property");
    		}
    		simple = (String) value;
    	}
    	public void set(String name, String key, Object value) {
    		if (! "mapped".equals(name)) {
    			throw new RuntimeException("No mapped property");
    		}
    		mapped.put(key,  value);
    	}
    }
    
    public void testGetProperty() {
    	BeanUtilsPropertyAccessor test = new BeanUtilsPropertyAccessor();
    	
    	MockDynaBean subject = new MockDynaBean();
    	
    	assertEquals(String.class, test.getPropertyType(subject, "simple"));
    	assertEquals(String.class, test.getPropertyType(subject, "indexed"));
    	assertEquals(String.class, test.getPropertyType(subject, "mapped"));
    }
    
    public void testDynaBeanSetters() {
    	BeanUtilsPropertyAccessor test = new BeanUtilsPropertyAccessor();
    	
    	MockDynaBean subject = new MockDynaBean();
    	
    	test.setProperty(subject, "simple", "Hello");
    	assertEquals("Hello", subject.simple);
    	
    	test.setProperty(subject, "indexed[0]", "Hello");
    	assertEquals("Hello", subject.indexed[0]);
    	
    	test.setProperty(subject, "mapped(greeting)", "Hello");
    	assertEquals("Hello", subject.mapped.get("greeting"));   	
    }
}
