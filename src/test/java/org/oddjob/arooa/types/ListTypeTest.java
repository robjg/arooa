/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.types;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.convert.convertlets.CollectionConvertlets;
import org.oddjob.arooa.deploy.ConfigurationDescriptorFactory;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for ListType.
 */
public class ListTypeTest {

    private static final Logger logger = LoggerFactory.getLogger(ListTypeTest.class);

    @Rule
    public TestName name = new TestName();

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

        assertThat(o, Matchers.instanceOf(List.class));

        // Check Conversion to Iterable.
        @SuppressWarnings("rawtypes")
        ConversionPath<ListType, Iterable> conversion =
                converter.findConversion(ListType.class, Iterable.class);
        assertThat(conversion.toString(), is("ListType-Iterable"));

        Iterable<?> iterable = converter.convert(test, Iterable.class);
        Iterator<?> it = iterable.iterator();
        assertThat(it.hasNext(), is(false));

        // Is lack of conversion to String a problem?
        ConversionPath<ListType, String> conversionToString =
                converter.findConversion(ListType.class, String.class);
        assertThat(conversionToString, Matchers.nullValue());
    }

    @Test
    public void testConvertContents() throws Exception {
        ListType test = new ListType();


        test.convertContents(new ArooaConverter() {
            @SuppressWarnings("unchecked")
            public <F, T> T convert(F from, Class<T> required) {
                assertThat(required, Matchers.instanceOf(String[].class));
                return (T) new String[]{(String) from};
            }

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

        assertThat(result, contains("test"));
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

        assertThat(result, is( new Object[] {"test"}));
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

        assertThat(result, is( new String[] {"test"}));
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

        int[] result = new DefaultConverter().convert(
                test, int[].class);

        assertThat(result, is( new int[] { 1, 2}));
    }

    @Test
    public void testConvertToListOfObjects()
            throws ArooaConversionException {

        ListType.ToListConverter<Object> test =
                new ListType.ToListConverter<>(
                        Object.class, new DefaultConverter());

        List<Object> results;

        results = test.convert(new String[]{"apples", "oranges"});

        assertThat(results, contains("apples", "oranges"));

        results = test.convert(Arrays.asList("apples", "oranges"));

        assertThat(results, contains("apples", "oranges"));

        results = test.convert("apples");

        assertThat(results, contains("apples"));

        results = test.convert(null);

        assertThat(results, contains(Matchers.nullValue()));

        results = test.convert(new int[] {3, 5});

        assertThat(results, contains(3 , 5));

        results = test.convert(Arrays.asList(
                new ArooaObject("apples"), new ArooaObject("oranges")));

        assertThat(results, contains("apples", "oranges"));
    }

    @Test
    public void testConvertToListOfString()
            throws ArooaConversionException {

        ListType.ToListConverter<String> test =
                new ListType.ToListConverter<>(
                        String.class, new DefaultConverter());

        List<String> results;

        results = test.convert(new String[]{"apples", "oranges"});

        assertThat(results, contains("apples", "oranges"));

        results = test.convert(Arrays.asList("apples", "oranges"));

        assertThat(results, contains("apples", "oranges"));

        results = test.convert("apples");

        assertThat(results, contains("apples"));

        results = test.convert(null);

        assertThat(results, contains(Matchers.nullValue()));

        results = test.convert(new int[]{3, 5});

        assertThat(results, contains("3", "5"));

        results = test.convert(Arrays.asList(
                new ArooaObject("apples"), new ArooaObject("oranges")));

        assertThat(results, contains("apples", "oranges"));
    }

    @Test
    public void testConvertDelimitedTextToListOfArrays() throws NoConversionAvailableException, ConversionFailedException {

        logger.info(String[].class.getName());

        ListType test = new ListType();
        test.setValues(0, new ArooaObject("a, b, c"));
        test.setValues(1, new ArooaObject("d, e, f"));
        test.setElementType(String[].class);

        ArooaConverter converter = new DefaultConverter();

        @SuppressWarnings("unchecked")
        List<String[]> list = converter.convert(
                test, List.class);

        assertThat(list, contains(
                new String[] {"a", "b", "c"}, new String[] {"d", "e", "f"}));
    }

    @Test
    public void testMergeList() throws Exception {
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");

        ValueType content1 = new ValueType();
        content1.setValue(new ArooaObject(list1));

        List<String> list2 = new ArrayList<>();
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

        assertThat(listResult, contains(
                "a", "b", "c", "d" ));

        String[] arrayResult = converter.convert(
                test, String[].class);

        assertThat(arrayResult, is(
                new String[] { "a", "b", "c", "d" }));
    }

    @Test
    public void testMergeUniqueArray() throws Exception {
        ValueType content1 = new ValueType();
        content1.setValue(
                new ArooaObject(new String[]{"a", "b"}));

        ValueType content2 = new ValueType();
        content2.setValue(
                new ArooaObject(new String[]{"c", "a"}));

        ListType test = new ListType();
        test.setMerge(true);
        test.setUnique(true);

        test.setValues(0, content1);
        test.setValues(1, content2);

        DefaultConverter converter = new DefaultConverter();

        List<?> listResult = converter.convert(
                test, List.class);

        assertThat(listResult, contains(
                "a", "b", "c" ));

        Object[] arrayResult = converter.convert(
                test, Object[].class);

        assertThat(arrayResult, is(
                new String[] { "a", "b", "c" }));
    }

    @Test
    public void testMergeNulls() throws NoConversionAvailableException, ConversionFailedException {
        ValueType content1 = new ValueType();
        content1.setValue(
                new ArooaObject(new String[]{null}));

        ValueType content2 = new ValueType();
        content2.setValue(null);

        ListType test = new ListType();
        test.setMerge(true);
        test.setUnique(true);

        test.setValues(0, content1);
        test.setValues(1, content2);

        DefaultConverter converter = new DefaultConverter();

        List<?> listResult = converter.convert(test, List.class);

        assertThat(listResult, contains(Matchers.nullValue()));

        Object[] arrayResult = converter.convert(test, Object[].class);

        assertThat(arrayResult, is(
                new Object[] { null }));
    }

    @Test
    public void testNoMergeArray() throws Exception {
        ValueType content1 = new ValueType();
        content1.setValue(
                new ArooaObject(new String[]{"a", "b"}));

        ValueType content2 = new ValueType();
        content2.setValue(
                new ArooaObject(new String[]{"c", "d"}));

        ListType test = new ListType();
        test.setMerge(false);

        test.setValues(0, content1);
        test.setValues(1, content2);

        DefaultConverter converter = new DefaultConverter();

        Object[] result = converter.convert(test, Object[].class);

        assertThat(result, is(
                new Object[] {
                        new String[] { "a", "b" },
                        new String[] { "c", "d" } } ));
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

        assertThat(listResult, contains(3, 7));

        Number[] arrayResult = converter.convert(
                test, Number[].class);

        assertThat(arrayResult, is(new Number[] {3, 7}));

        try {
            converter.convert(
                    test, String[].class);
            assertThat("This isn't possilbe.", false);
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

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            return obj != null && obj.getClass() == A.class;
        }
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

        String xml =
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

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new CollectionConvertlets().registerWith(registry);

        Object[] o = new DefaultConverter(registry).convert(
                root.results, Object[].class);

        assertThat(o, is(new Object[] { new A(), "apple" }));
    }

    @Test
    public void testStrings() throws Exception {

        String xml = "<root>\n" +
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

        assertThat(root.results, is(new Object[] { "orange", "apple" }));
    }

    @Test
    public void testUniqueInts() throws Exception {
        String xml = "<root>" +
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

        assertThat(root.results, is(new int[] { 1, 2 }));
    }

    /**
     * Configure/Destroy sets null values. How does list cope?
     *
     * @throws Exception
     */
    @Test
    public void testNullsAndReconfiguring() throws Exception {
        String EOL = System.getProperty("line.separator");

        String xml =
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

        assertThat(root.results, Matchers.nullValue());

        ArooaSession session = parser.getSession();
        session.getComponentPool().configure(root);

        assertThat(root.results, Matchers.notNullValue());

        List<Object> results = root.results;

        assertThat(results, contains(Matchers.nullValue(), is("apple")));

        session.getComponentPool().contextFor(
                root).getRuntime().destroy();

        assertThat(root.results, Matchers.nullValue());
    }

    @Test
    public void testAddingValues() throws NoConversionAvailableException, ConversionFailedException {

        ListType test = new ListType();
        test.setAdd(new ArooaObject("Apple"));
        test.setAdd(new ArooaObject("Pear"));

        DefaultConverter converter = new DefaultConverter();

        List<?> listResult = converter.convert(
                test, List.class);

        assertThat(listResult, contains("Apple", "Pear"));

        test.configured();

        listResult = converter.convert(
                test, List.class);

        assertThat(listResult, empty());

        test.setAdd(new ArooaObject("Orange"));

        listResult = converter.convert(
                test, List.class);

        assertThat(listResult, contains("Orange"));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAsConsumer() throws NoConversionAvailableException, ConversionFailedException {

        ListType test = new ListType();
        test.configured();

        DefaultConverter converter = new DefaultConverter();

        Consumer<String> consumer = converter.convert(
                test, Consumer.class);

        consumer.accept("Apple");
        consumer.accept("Pear");

        List<String> listResult = converter.convert(
                test, List.class);

        assertThat(listResult, contains("Apple", "Pear"));

        PropertyAccessor propertyAccessor = new BeanUtilsPropertyAccessor();

        // Check consumer values and value properties.

        List<?> consumerList = converter.convert(
                propertyAccessor.getProperty(consumer, "values"), List.class);

        assertThat(consumerList, contains("Apple", "Pear"));

        assertThat(propertyAccessor.getProperty(consumer, "values.[0]"), is("Apple"));
        assertThat(propertyAccessor.getProperty(consumer, "values.[1]"), is("Pear"));
        assertThat(propertyAccessor.getProperty(consumer, "value[0]"), is("Apple"));
        assertThat(propertyAccessor.getProperty(consumer, "value[1]"), is("Pear"));

        test.configured();

        listResult = converter.convert(
                test, List.class);

        assertThat(listResult, empty());

        consumer.accept("Orange");

        listResult = converter.convert(
                test, List.class);

        assertThat(listResult, contains("Orange"));

        consumerList = converter.convert(
                propertyAccessor.getProperty(consumer, "values"), List.class);

        assertThat(consumerList, contains("Orange"));
    }
}
