/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.beanutils;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * Test our assumptions about BeanUtils.
 * <p>
 * Note that we are only using PropertyUtils as BeanUtils
 * performs conversions we don't want. 
 *
 */
public class BeanUtilsAssumptionsTest extends Assert {

	private static final Logger logger = LoggerFactory.getLogger(
			BeanUtilsAssumptionsTest.class);

	@Rule public TestName name = new TestName();

	public String getName() {
        return name.getMethodName();
    }

	    @Before
   public void setUp() throws Exception {

		
		logger.info("-------------------  " + getName() + 
				"  ------------------");
	}
	
	public static class BeanWithMappedProp {
		Map<String, File> map = new HashMap<String, File>();

		public void setMapped(String key, File value) {
			map.put(key, value);
		}
		
		public File getMapped(String key) {
			return (File) map.get(key);
		}
	}
	
	/** 
	 * A mapped type has a mapped descriptor whose type is the type
	 * of the map.
	 * 
	 * @throws Exception
	 */
   @Test
	public void testMappedType() throws Exception {
		BeanWithMappedProp bean = new BeanWithMappedProp();
		
		PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(bean, "mapped");
		assertTrue(descriptor instanceof MappedPropertyDescriptor);
		
		Class<?> result = PropertyUtils.getPropertyType(bean, "mapped");
		assertEquals(File.class, result);
	}

	/**
	 * Test setting a mapped type.
	 * 
	 * @throws Exception
	 */
   @Test
	public void testSetMapped() throws Exception {
		BeanWithMappedProp bean = new BeanWithMappedProp();

		// note: BeanUtils does conversion, PropertyUtils doesn't.
		BeanUtils.setProperty(bean, "mapped(foo)", "test.dat");
		
		assertEquals(new File("test.dat"), bean.map.get("foo"));

		bean.map.remove("foo");
		
		PropertyUtils.setProperty(bean, "mapped(foo)", new File("test.dat"));
		
		assertEquals(new File("test.dat"), bean.map.get("foo"));
	}

	
	/**
	 * Use setMappedProperty
	 * 
	 * @throws Exception
	 */
   @Test
	public void testSetMapped2() throws Exception {
		BeanWithMappedProp bean = new BeanWithMappedProp();

		PropertyUtils.setMappedProperty(bean, "mapped", "foo", new File("test.dat"));
		
		assertEquals(new File("test.dat"), bean.map.get("foo"));
	}
	

   @Test
	public void testSetMappedWithMap() throws Exception {
		BeanWithMappedProp bean = new BeanWithMappedProp();

		Map<String, File>map = new HashMap<String, File>();
		map.put("foo", new File("test.dat"));
		
		try {
			PropertyUtils.setProperty(bean, "mapped", map);
			fail("BeanUtils won't set the map - we have to do this.");
		}
		catch (NoSuchMethodException e) {
			// expected.
		}
	}
	
	public static class BeanWithMapProp {
		Map<?, ?> map = new HashMap<Object, Object>();
		
		public void setMap(Map<?, ?> map) {
			this.map = map;
		}
		
		public Map<?, ?> getMap() {
			return map;
		}
	}
	
	/**
	 * Show that a property that is a map does not have a mapped
	 * property descriptor.
	 * 
	 * @throws Exception
	 */
   @Test
	public void testMapType() throws Exception {
		BeanWithMapProp bean = new BeanWithMapProp();
		
		// a map is not an instance of a mapped property although
		// it can be set/got with the mapped property methods.
		PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(bean, "map");
		assertFalse(descriptor instanceof MappedPropertyDescriptor);
		
		Class<?> result = PropertyUtils.getPropertyType(bean, "map");
		assertEquals(Map.class, result);
	}
	
	/**
	 * Note that these methods rely on getMap not returning null.
	 * 
	 * @throws Exception
	 */
   @Test
	public void testSetMap() throws Exception {
		BeanWithMapProp bean = new BeanWithMapProp();

		// note: BeanUtils does conversion, PropertyUtils doesn't.
		BeanUtils.setProperty(bean, "map(foo)", "test.dat");
		
		// now this method doesn't no about conversion! (how could it?)
			assertEquals("test.dat", bean.map.get("foo"));

		bean.map.remove("foo");
		
		PropertyUtils.setProperty(bean, "map(foo)", new File("test.dat"));
		
		assertEquals(new File("test.dat"), bean.map.get("foo"));
	}

	/**
	 * Use setMappedProperty
	 * 
	 * @throws Exception
	 */
   @Test
	public void testSetMap2() throws Exception {
		BeanWithMapProp bean = new BeanWithMapProp();

		PropertyUtils.setMappedProperty(bean, "map", "foo", new File("test.dat"));
		
		assertEquals(new File("test.dat"), bean.map.get("foo"));
	}
	
	/**
	 * Show set the entire map still works as a simple property.
	 * 
	 * @throws Exception
	 */
   @Test
	public void testSetMap3() throws Exception {
		BeanWithMapProp bean = new BeanWithMapProp();

		bean.map = null;
		PropertyUtils.setSimpleProperty(bean, "map", 
				new HashMap<Object, Object>());
		
		assertNotNull(bean.map);
	}
	
	public static class ComplexBean {
		SimpleBean nested = new SimpleBean();
		
		public SimpleBean getNested() {
			return nested;
		}
	}
	
	public static class SimpleBean {
		int prop;
		
		public void setProp(int prop) {
			this.prop = prop;
		}
	}

	/**
	 * Test accessing nested properties using expressions.
	 * 
	 * @throws Exception
	 */
   @Test
	public void testSetNestedProperty() throws Exception {
		ComplexBean cb = new ComplexBean();
		
		Class<?> type = PropertyUtils.getPropertyType(cb, "nested.prop");
		assertEquals(Integer.TYPE, type);
	
		PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(cb, "nested.prop");
		assertEquals(Integer.TYPE, pd.getPropertyType());
		
		PropertyUtils.setProperty(cb, "nested.prop", new Integer(2));		
		assertEquals(2, cb.nested.prop);
	}

	public interface Thing1 {
		
		public String getThing1();
	}
	
	public interface Thing2 {
		
		public String getThing2();
	}
	
	class MyHandler implements InvocationHandler {
	
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			return method.getName();
		}
	}
	
	/**
	 * There's nothing special about proxies...
	 */
   @Test
	public void testWithProxy() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
	
		Object proxy = Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { Thing1.class, Thing2.class }, 
				new MyHandler());
		
		PropertyDescriptor[] descriptors = 
				PropertyUtils.getPropertyDescriptors(proxy);
		for (PropertyDescriptor descriptor : descriptors) {
			logger.info(descriptor.toString());
		}	
		
		PropertyDescriptor propertyDescriptor1 = 
				PropertyUtils.getPropertyDescriptor(proxy, "thing1");
		assertEquals(String.class, propertyDescriptor1.getPropertyType());
		
		assertEquals("getThing1", 
				PropertyUtils.getProperty(proxy, "thing1"));		
		
		PropertyDescriptor propertyDescriptor2 = 
				PropertyUtils.getPropertyDescriptor(proxy, "thing2");
		assertEquals(String.class, propertyDescriptor2.getPropertyType());
		
		assertEquals("getThing2", 
				PropertyUtils.getProperty(proxy, "thing2"));		
	}
}
