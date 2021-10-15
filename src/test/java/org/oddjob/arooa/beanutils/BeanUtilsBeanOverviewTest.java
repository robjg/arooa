package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.junit.Before;
import org.junit.Test;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class BeanUtilsBeanOverviewTest {

    @Before
    public void setUp() throws Exception {
        Class.forName(DynaArooaClass.class.getName());
        Class.forName(SimpleArooaClass.class.getName());
    }

    public static class BasicBean {
        public String getA() {
            return null;
        }

        public void setB(int i) {
        }

        public Number getC() {
            return null;
        }

        public void setC(Number n) {
        }

        public Double getD() {
            return null;
        }

        public void setD(Integer i) {
        }
    }


    @Test
    public void testBasics() throws ArooaPropertyException {
        PropertyAccessor propertyAccessor =
                new BeanUtilsPropertyAccessor();

        ArooaClass arooaClass = propertyAccessor.getClassName(new BasicBean());

        BeanOverview test = arooaClass.getBeanOverview(
                propertyAccessor);

        assertThat(Arrays.asList(test.getProperties()),
                contains("a", "b", "c", "d", "class"));

        assertThat(test.getPropertyType("a"), is(String.class));
        assertThat(test.hasReadableProperty("a"), is(true));
        assertFalse(test.hasWriteableProperty("a"));
        assertFalse(test.isIndexed("a"));
        assertFalse(test.isMapped("a"));

        assertEquals(Integer.TYPE, test.getPropertyType("b"));
        assertFalse(test.hasReadableProperty("b"));
        assertTrue(test.hasWriteableProperty("b"));
        assertFalse(test.isIndexed("b"));
        assertFalse(test.isMapped("b"));

        assertEquals(Number.class, test.getPropertyType("c"));
        assertTrue(test.hasReadableProperty("c"));
        assertTrue(test.hasWriteableProperty("c"));
        assertFalse(test.isIndexed("c"));
        assertFalse(test.isMapped("c"));

        // tbd: D

        try {
            test.getPropertyType("x");
            fail("Should be exception.");
        } catch (ArooaNoPropertyException e) {
            // expected
        }
        assertFalse(test.hasReadableProperty("x"));
        assertFalse(test.hasWriteableProperty("x"));
        try {
            test.isIndexed("x");
            fail("Should be exception.");
        } catch (ArooaNoPropertyException e) {
            // expected
        }
        try {
            assertFalse(test.isMapped("x"));
            fail("Should be exception.");
        } catch (ArooaNoPropertyException e) {
            // expected
        }
    }

    public static class IndexedBean {
        public String getA(int i) {
            return null;
        }

        public void setA(int i, String s) {
        }

        public String[] getB() {
            return null;
        }

        public void setC(String[] sa) {
        }

        public void setC(int i, String s) {
        }
    }

    @Test
    public void testIndexed() throws ArooaPropertyException {
        PropertyAccessor propertyAccessor =
                new BeanUtilsPropertyAccessor();

        ArooaClass arooaClass = propertyAccessor.getClassName(
                new IndexedBean());

        BeanOverview test = arooaClass.getBeanOverview(
                propertyAccessor);

        assertEquals(String.class, test.getPropertyType("a"));
        assertTrue(test.hasReadableProperty("a"));
        assertTrue(test.hasWriteableProperty("a"));
        assertTrue(test.isIndexed("a"));
        assertFalse(test.isMapped("a"));

        assertEquals(String[].class, test.getPropertyType("b"));
        assertTrue(test.hasReadableProperty("b"));
        assertFalse(test.hasWriteableProperty("b"));
        assertFalse(test.isIndexed("b"));
        assertFalse(test.isMapped("b"));

        assertEquals(String.class, test.getPropertyType("c"));
        assertFalse(test.hasReadableProperty("c"));
        assertTrue(test.hasWriteableProperty("c"));
        assertTrue(test.isIndexed("c"));
        assertFalse(test.isMapped("c"));
    }

    public static class MappedBean {
        public String getA(String key) {
            return null;
        }

        public void setA(String key, String s) {
        }

        public Map<Object, Object> getB() {
            return null;
        }

        public void setC(Map<Object, Object> sa) {
        }

        public void setC(String key, String s) {
        }
    }

    @Test
    public void testMapped() throws ArooaPropertyException {
        PropertyAccessor propertyAccessor =
                new BeanUtilsPropertyAccessor();

        ArooaClass arooaClass = propertyAccessor.getClassName(
                new MappedBean());

        BeanOverview test = arooaClass.getBeanOverview(propertyAccessor);

        Set<String> properties = new HashSet<>(
                Arrays.asList(test.getProperties()));
        assertTrue(properties.contains("a"));
        assertTrue(properties.contains("b"));
        assertTrue(properties.contains("c"));

        assertEquals(String.class, test.getPropertyType("a"));
        assertTrue(test.hasReadableProperty("a"));
        assertTrue(test.hasWriteableProperty("a"));
        assertFalse(test.isIndexed("a"));
        assertTrue(test.isMapped("a"));

        assertEquals(Map.class, test.getPropertyType("b"));
        assertTrue(test.hasReadableProperty("b"));
        assertFalse(test.hasWriteableProperty("b"));
        assertFalse(test.isIndexed("b"));
        assertFalse(test.isMapped("b"));

        assertEquals(String.class, test.getPropertyType("c"));
        assertFalse(test.hasReadableProperty("c"));
        assertTrue(test.hasWriteableProperty("c"));
        assertFalse(test.isIndexed("c"));
        assertTrue(test.isMapped("c"));
    }

    public static class MappedWithJustASetter {
        public void setA(String key, String s) {
        }
    }

    @Test
    public void testMappedWithJustASetter() {
        PropertyAccessor propertyAccessor =
                new BeanUtilsPropertyAccessor();

        ArooaClass arooaClass = propertyAccessor.getClassName(
                new MappedWithJustASetter());

        BeanOverview test = arooaClass.getBeanOverview(
                propertyAccessor);

        assertEquals(String.class, test.getPropertyType("a"));
        assertFalse(test.hasReadableProperty("a"));
        assertTrue(test.hasWriteableProperty("a"));
        assertFalse(test.isIndexed("a"));
        assertTrue(test.isMapped("a"));
    }


    public static class MyDynaBean implements DynaBean {

        public boolean contains(String name, String key) {
            throw new RuntimeException("Unexpected!");
        }

        public Object get(String name) {
            throw new RuntimeException("Unexpected!");
        }

        public Object get(String name, int index) {
            throw new RuntimeException("Unexpected!");
        }

        public Object get(String name, String key) {
            throw new RuntimeException("Unexpected!");
        }

        public DynaClass getDynaClass() {
            return new DynaClass() {

                public DynaProperty[] getDynaProperties() {
                    return new DynaProperty[0];
                }

                public DynaProperty getDynaProperty(String name) {
                    return null;
                }

                public String getName() {
                    return "Fred";
                }

                public DynaBean newInstance() {
                    throw new RuntimeException("Unexpected!");
                }

            };
        }

        public void remove(String name, String key) {
            throw new RuntimeException("Unexpected!");
        }

        public void set(String name, Object value) {
            throw new RuntimeException("Unexpected!");
        }

        public void set(String name, int index, Object value) {
            throw new RuntimeException("Unexpected!");
        }

        public void set(String name, String key, Object value) {
            throw new RuntimeException("Unexpected!");
        }

    }

    @Test
    public void testDynaBean() {

        PropertyAccessor propertyAccessor =
                new BeanUtilsPropertyAccessor();

        BeanOverview test = propertyAccessor.getClassName(new MyDynaBean()
        ).getBeanOverview(propertyAccessor);

        assertEquals(DynaBeanOverview.class, test.getClass());

        assertEquals(0, test.getProperties().length);
        assertFalse(test.hasReadableProperty("fruit"));
        assertFalse(test.hasWriteableProperty("fruit"));
    }

    @Test
    public void testDynaBeanByClass() {
        PropertyAccessor propertyAccessor =
                new BeanUtilsPropertyAccessor();

        MyDynaBean bean = new MyDynaBean();
        DynaClass dynaClass = bean.getDynaClass();

        ArooaClass arooaClass = new DynaArooaClass(dynaClass, bean.getClass());

        BeanOverview test = arooaClass.getBeanOverview(
                propertyAccessor);

        assertEquals(0, test.getProperties().length);
    }
}
