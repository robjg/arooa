package org.oddjob.arooa.standard;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.annotations.ArooaText;
import org.oddjob.arooa.life.ClassLoaderClassResolver;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.types.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StandardArooaDescriptorTest extends Assert {

    public static class Bean {

        @ArooaText
        public void setMyText(Object ignored) {
        }
    }

    @Test
    public void testUsesAnnotated() {

        StandardArooaDescriptor test = new StandardArooaDescriptor();

        ArooaBeanDescriptor beanDescriptor = test.getBeanDescriptor(
                new SimpleArooaClass(Bean.class),
                new StandardTools().getPropertyAccessor());

        assertEquals("myText", beanDescriptor.getTextProperty());
    }

    @Test
    public void testDefaultValueMappings() {

        StandardArooaDescriptor descriptor = new StandardArooaDescriptor();

        ElementMappings mappings;

        mappings = descriptor.getElementMappings();

        assertNotNull(mappings.mappingFor(IsType.ELEMENT,
                new InstantiationContext(ArooaType.COMPONENT,
                        new SimpleArooaClass(Object.class))));

        assertNotNull(mappings.mappingFor(BeanType.ELEMENT,
                new InstantiationContext(ArooaType.COMPONENT,
                        new SimpleArooaClass(Object.class),
                        new ClassLoaderClassResolver(
                                getClass().getClassLoader()))));

        assertNotNull(mappings.mappingFor(ClassType.ELEMENT,
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class))));

        assertNotNull(mappings.mappingFor(ConvertType.ELEMENT,
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class))));

        assertNotNull(mappings.mappingFor(ListType.ELEMENT,
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class))));

        assertNotNull(mappings.mappingFor(ValueType.ELEMENT,
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class))));

        assertNotNull(mappings.mappingFor(ImportType.ELEMENT,
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class))));

        assertNotNull(mappings.mappingFor(IdentifiableValueType.ELEMENT,
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class))));

        assertNotNull(mappings.mappingFor(XMLType.ELEMENT,
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class))));

        assertNotNull(mappings.mappingFor(IsType.ELEMENT,
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class))));

        assertNotNull(mappings.mappingFor(BeanType.ELEMENT,
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class),
                        new ClassLoaderClassResolver(
                                getClass().getClassLoader()))));
    }

    @Test
    public void testAllElements() {

        StandardArooaDescriptor descriptor = new StandardArooaDescriptor();

        ElementMappings mappings = descriptor.getElementMappings();

        ArooaElement[] elements = mappings.elementsFor(
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class),
                        null,
                        new DefaultConverter()));

        assertEquals(11, elements.length);

        Set<ArooaElement> set = new HashSet<>(Arrays.asList(elements));

        assertTrue(set.contains(new ArooaElement("is")));
        assertTrue(set.contains(new ArooaElement("bean")));
        assertTrue(set.contains(new ArooaElement("class")));
        assertTrue(set.contains(new ArooaElement("identify")));
        assertTrue(set.contains(new ArooaElement("import")));
        assertTrue(set.contains(new ArooaElement("inline")));
        assertTrue(set.contains(new ArooaElement("value")));
        assertTrue(set.contains(new ArooaElement("xml")));
        assertTrue(set.contains(new ArooaElement("list")));
        assertTrue(set.contains(new ArooaElement("map")));
    }
}
