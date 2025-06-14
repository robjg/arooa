package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.*;
import org.junit.jupiter.api.Test;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WrapDynaBeanAssumptionsTest {

    public static class OurBean {

        public void setSimple(String simple) {
        }

        public void setIndexed(int i, String simple) {
        }

        public void setMapped(String key, String simple) {
        }
    }

    @Test
    public void testNoProperty() {

        WrapDynaClass test = (WrapDynaClass) new WrapDynaBean(
                new OurBean()).getDynaClass();

        assertNull(test.getDynaProperty("fruit"));
        assertNull(test.getPropertyDescriptor("fruit"));

    }

    @Test
    public void testProperties() {

        PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
        propertyUtilsBean.removeBeanIntrospector(SuppressPropertiesBeanIntrospector.SUPPRESS_CLASS);

        OurBean ourBean = new OurBean();

        WrapDynaClass test = WrapDynaClass.createDynaClass(ourBean.getClass(), propertyUtilsBean);

        DynaProperty[] properties = test.getDynaProperties();

        assertEquals(3, properties.length);

        Set<String> names = new HashSet<>();
        for (DynaProperty prop : properties) {
            names.add(prop.getName());
        }

        assertTrue(names.contains("class"));
        assertTrue(names.contains("simple"));
        assertTrue(names.contains("indexed"));

        // !!!
        assertFalse(names.contains("mapped"));
    }

    @Test
    public void testSimple() {

        WrapDynaClass test = (WrapDynaClass) new WrapDynaBean(
                new OurBean()).getDynaClass();

        DynaProperty dynaProperty = test.getDynaProperty("simple");

        assertFalse(dynaProperty.isIndexed());
        assertFalse(dynaProperty.isMapped());
        assertEquals(String.class, dynaProperty.getType());

        PropertyDescriptor propertyDescriptor = test.getPropertyDescriptor("simple");

        assertNull(propertyDescriptor.getReadMethod());
        assertNotNull(propertyDescriptor.getWriteMethod());

    }

    @Test
    public void testIndexed() {

        WrapDynaClass test = (WrapDynaClass) new WrapDynaBean(
                new OurBean()).getDynaClass();

        DynaProperty dynaProperty = test.getDynaProperty("indexed");

        //!!!
        assertFalse(dynaProperty.isIndexed());
        assertFalse(dynaProperty.isMapped());

        //!!!
        assertNull(dynaProperty.getType());
        assertNull(dynaProperty.getContentType());

        IndexedPropertyDescriptor propertyDescriptor =
                (IndexedPropertyDescriptor) test.getPropertyDescriptor("indexed");

        assertNull(propertyDescriptor.getReadMethod());
        assertNull(propertyDescriptor.getWriteMethod());
        assertNull(propertyDescriptor.getIndexedReadMethod());
        assertNotNull(propertyDescriptor.getIndexedWriteMethod());

    }

    @Test
    public void testMapped() {

        WrapDynaClass test = (WrapDynaClass) new WrapDynaBean(
                new OurBean()).getDynaClass();

        DynaProperty dynaProperty = test.getDynaProperty("mapped");

        assertNull(dynaProperty);

        MappedPropertyDescriptor propertyDescriptor =
                (MappedPropertyDescriptor) test.getPropertyDescriptor("mapped");

        assertNull(propertyDescriptor);

    }

}
