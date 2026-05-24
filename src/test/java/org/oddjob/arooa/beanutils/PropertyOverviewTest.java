package org.oddjob.arooa.beanutils;

import org.apache.commons.beanutils.MappedPropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.utils.TypeToken;

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Test PropertyOverview factory methods for simple, indexed and mapped properties.
 */
class PropertyOverviewTest {

    public static class TestBeanWithIndexedAndMapped {
        private String simpleProp;
        private List<String> genericProp;
        private List<String> args = new ArrayList<>();
        private List<Optional<String>> genericArgs = new ArrayList<>();
        private Map<String, Integer> mappedMap = new HashMap<>();
        private Map<String, Set<Integer>> genericMaped = new HashMap<>();

        public String getSimpleProp() {
            return simpleProp;
        }

        public void setSimpleProp(String simpleProp) {
            this.simpleProp = simpleProp;
        }

        public List<String> getGenericProp() {
            return genericProp;
        }

        public void setGenericProp(List<String> genericProp) {
            this.genericProp = genericProp;
        }

        // Indexed property methods
        public String getArgs(int index) {
            return args.get(index);
        }

        public void setArgs(int index, String value) {
            args.set(index, value);
        }

        public Optional<String> getGenericArgs(int index) {
            return genericArgs.get(index);
        }

        public void setGenericArgs(int index, Optional<String> value) {
            genericArgs.set(index, value);
        }

        // Mapped property methods
        public Integer getMapped(String key) {
            return mappedMap.get(key);
        }

        public void setMapped(String key, Integer value) {
            mappedMap.put(key, value);
        }

        public Set<Integer> getGenericMapped(String key) {
            return genericMaped.get(key);
        }

        public void setGenericMapped(String key, Set<Integer> value) {
            genericMaped.put(key, value);
        }
    }

    public static class TestBeanReadOnly {
        private String simpleProp;
        private List<String> args = new ArrayList<>();
        private Map<String, Integer> mappedMap = new HashMap<>();

        public String getSimpleProp() {
            return simpleProp;
        }

        // Indexed property methods
        public String getArgs(int index) {
            return args.get(index);
        }

        // Mapped property methods
        public Integer getMapped(String key) {
            return mappedMap.get(key);
        }
    }

    public static class TestBeanWriteOnly {
        private String simpleProp;
        private List<String> args = new ArrayList<>();
        private Map<String, Integer> mappedMap = new HashMap<>();

        public void setSimpleProp(String simpleProp) {
            this.simpleProp = simpleProp;
        }

        // Indexed property methods
        public void setArgs(int index, String value) {
            args.set(index, value);
        }

        // Mapped property methods
        public void setMapped(String key, Integer value) {
            mappedMap.put(key, value);
        }

    }

    @Test
    void simpleProperty() throws Exception {

        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                new TestBeanWithIndexedAndMapped(), "simpleProp");

        PropertyOverview overview = PropertyOverview.ofSimple(descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(String.class));
        assertThat(overview.isReadable(), is(true));
        assertThat(overview.isWritable(), is(true));
        assertThat(overview.isIndexed(), is(false));
        assertThat(overview.isMapped(), is(false));
    }

    @Test
    void genericProperty() throws Exception {

        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                new TestBeanWithIndexedAndMapped(), "genericProp");

        PropertyOverview overview = PropertyOverview.ofSimple(descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(new TypeToken<List<String>>() {}.getType()));
        assertThat(overview.isReadable(), is(true));
        assertThat(overview.isWritable(), is(true));
        assertThat(overview.isIndexed(), is(false));
        assertThat(overview.isMapped(), is(false));
    }

    @Test
    void indexedProperty() throws Exception {

        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                new TestBeanWithIndexedAndMapped(), "args");

        PropertyOverview overview = PropertyOverview.ofIndexed(
                (IndexedPropertyDescriptor) descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(String.class));
        assertThat(overview.isReadable(), is(true));
        assertThat(overview.isWritable(), is(true));
        assertThat(overview.isIndexed(), is(true));
        assertThat(overview.isMapped(), is(false));
    }

    @Test
    void genericIndexedProperty() throws Exception {

        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                new TestBeanWithIndexedAndMapped(), "genericArgs");

        PropertyOverview overview = PropertyOverview.ofIndexed(
                (IndexedPropertyDescriptor) descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(new TypeToken<Optional<String>>() {}.getType()));
        assertThat(overview.isReadable(), is(true));
        assertThat(overview.isWritable(), is(true));
        assertThat(overview.isIndexed(), is(true));
        assertThat(overview.isMapped(), is(false));
    }

    @Test
    void mappedProperty() throws Exception {
        MappedPropertyDescriptor descriptor = new MappedPropertyDescriptor(
                "mapped", TestBeanWithIndexedAndMapped.class);

        PropertyOverview overview = PropertyOverview.ofMapped(descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(Integer.class));
        assertThat(overview.isReadable(), is(true));
        assertThat(overview.isWritable(), is(true));
        assertThat(overview.isIndexed(), is(false));
        assertThat(overview.isMapped(), is(true));
    }

    @Test
    void genericMappedProperty() throws Exception {
        MappedPropertyDescriptor descriptor = new MappedPropertyDescriptor(
                "genericMapped", TestBeanWithIndexedAndMapped.class);

        PropertyOverview overview = PropertyOverview.ofMapped(descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(new TypeToken<Set<Integer>>() {}.getType()));
        assertThat(overview.isReadable(), is(true));
        assertThat(overview.isWritable(), is(true));
        assertThat(overview.isIndexed(), is(false));
        assertThat(overview.isMapped(), is(true));
    }

    @Test
    void simpleReadOnlyProperty() throws Exception {

        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                new TestBeanReadOnly(), "simpleProp");

        PropertyOverview overview = PropertyOverview.ofSimple(descriptor);
        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(String.class));
        assertThat(overview.isReadable(), is(true));
        assertThat(overview.isWritable(), is(false));
        assertThat(overview.isIndexed(), is(false));
        assertThat(overview.isMapped(), is(false));
    }

    @Test
    void simpleWriteOnlyProperty() throws Exception {

        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                new TestBeanWriteOnly(), "simpleProp");

        PropertyOverview overview = PropertyOverview.ofSimple(descriptor);
        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(String.class));
        assertThat(overview.isReadable(), is(false));
        assertThat(overview.isWritable(), is(true));
        assertThat(overview.isIndexed(), is(false));
        assertThat(overview.isMapped(), is(false));
    }

    @Test
    void indexedReadOnlyProperty() throws Exception {

        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                new TestBeanReadOnly(), "args");

        PropertyOverview overview = PropertyOverview.ofIndexed(
                (IndexedPropertyDescriptor) descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(String.class));
        assertThat(overview.isReadable(), is(true));
        assertThat(overview.isWritable(), is(false));
        assertThat(overview.isIndexed(), is(true));
        assertThat(overview.isMapped(), is(false));
    }

    @Test
    void indexedWriteOnlyProperty() throws Exception {

        PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                new TestBeanWriteOnly(), "args");

        PropertyOverview overview = PropertyOverview.ofIndexed(
                (IndexedPropertyDescriptor) descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(String.class));
        assertThat(overview.isReadable(), is(false));
        assertThat(overview.isWritable(), is(true));
        assertThat(overview.isIndexed(), is(true));
        assertThat(overview.isMapped(), is(false));
    }

    @Test
    void mappedReadOnlyProperty() throws Exception {
        MappedPropertyDescriptor descriptor = new MappedPropertyDescriptor(
                "mapped", TestBeanReadOnly.class);

        PropertyOverview overview = PropertyOverview.ofMapped(descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(Integer.class));
        assertThat(overview.isReadable(), is(true));
        assertThat(overview.isWritable(), is(false));
        assertThat(overview.isIndexed(), is(false));
        assertThat(overview.isMapped(), is(true));
    }

    @Test
    void mappedWriteOnlyProperty() throws Exception {
        MappedPropertyDescriptor descriptor = new MappedPropertyDescriptor(
                "mapped", TestBeanWriteOnly.class);

        PropertyOverview overview = PropertyOverview.ofMapped(descriptor);

        assertThat(overview, notNullValue());

        assertThat(overview.getPropertyType(), is(Integer.class));
        assertThat(overview.isReadable(), is(false));
        assertThat(overview.isWritable(), is(true));
        assertThat(overview.isIndexed(), is(false));
        assertThat(overview.isMapped(), is(true));
    }

}

