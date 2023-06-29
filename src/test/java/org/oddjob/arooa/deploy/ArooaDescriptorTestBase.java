/*
 * (c) Rob Gordon 2005
 */
package org.oddjob.arooa.deploy;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.convert.ConversionRegistry;
import org.oddjob.arooa.convert.NoConversionAvailableException;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.standard.ExtendedTools;
import org.oddjob.arooa.standard.StandardTools;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 *
 */
abstract public class ArooaDescriptorTestBase {

    public static class Week {

    }

    public static class Monday {

    }

    public static class Tuesday {

    }

    public static class HealthySnack {

    }

    abstract ArooaDescriptor getTest(ClassLoader loader);

    @Test
    public void testMappings() throws URISyntaxException {


        ElementMappings mapping =
                getTest(getClass().getClassLoader()).getElementMappings();

        ArooaClass result = mapping.mappingFor(
                new ArooaElement(
                        new URI("http://rgordon.co.uk/test"), "tuesday"),
                new InstantiationContext(ArooaType.COMPONENT, null));

        assertThat(result.forClass(), is(Tuesday.class));
    }

    @Test
    public void testBeanDescriptor() {

        ArooaDescriptor test = getTest(getClass().getClassLoader());

        ArooaBeanDescriptor appleDescriptor =
                test.getBeanDescriptor(
                        new SimpleArooaClass(Apple.class),
                        new BeanUtilsPropertyAccessor());

        assertThat(appleDescriptor.getTextProperty(), is("description"));

        ArooaBeanDescriptor weekDescriptor =
                test.getBeanDescriptor(
                        new SimpleArooaClass(Week.class),
                        new BeanUtilsPropertyAccessor());

        assertThat(weekDescriptor.getComponentProperty(), is("days"));

        ArooaBeanDescriptor orangeDescriptor = test.getBeanDescriptor(
                new SimpleArooaClass(Orange.class),
                new BeanUtilsPropertyAccessor());

        assertThat(orangeDescriptor, notNullValue());
        assertThat(orangeDescriptor.getTextProperty(), nullValue());
        assertThat(orangeDescriptor.getComponentProperty(), nullValue());
    }

    @Test
    public void testElements() throws URISyntaxException {

        ArooaElement[] elements = getTest(
                getClass().getClassLoader()).getElementMappings().elementsFor(
                new InstantiationContext(ArooaType.VALUE,
                        new SimpleArooaClass(Object.class)));

        assertThat(elements, is(new ArooaElement[]{
                new ArooaElement(new URI("http://rgordon.co.uk/test"), "snack"),
                new ArooaElement(new URI("http://rgordon.co.uk/test"), "orange")}));

        elements = getTest(
                getClass().getClassLoader()).getElementMappings().elementsFor(
                new InstantiationContext(ArooaType.COMPONENT,
                        new SimpleArooaClass(Object.class)));

        assertThat(elements, is(new ArooaElement[]{
                new ArooaElement(new URI("http://rgordon.co.uk/test"), "week"),
                new ArooaElement(new URI("http://rgordon.co.uk/test"), "monday"),
                new ArooaElement(new URI("http://rgordon.co.uk/test"), "tuesday")}));

    }

    public static class Apple implements ArooaValue {

    }

    public static class Orange {
    }

    public static class FruitConversions implements ConversionProvider {
        public void registerWith(ConversionRegistry registry) {
            registry.register(Apple.class, Orange.class, from -> new Orange());
        }
    }

    @Test
    public void testConversions() throws NoConversionAvailableException, ConversionFailedException {

        ArooaTools tools = new ExtendedTools(new StandardTools(),
                getTest(getClass().getClassLoader()));

        Object result = tools.getArooaConverter().convert(
                new Apple(), Orange.class);

        assertThat(result, instanceOf(Orange.class));
    }
}