package org.oddjob.arooa.deploy;

import org.junit.Test;
import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.reflect.ArooaClass;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;

public class BeanDescriptorBuilderTest {

    @Test
    public void testAllTypes() {

        ArooaClass arooaClass = mock(ArooaClass.class);

        ParsingInterceptor parsingInterceptor = mock(ParsingInterceptor.class);

        ArooaAnnotations arooaAnnotations = mock(ArooaAnnotations.class);

        BeanDescriptorBuilder test = new BeanDescriptorBuilder(arooaClass);
        test.addAttributeProperty("someAttribute");
        test.addElementProperty("someElement");
        test.setAuto("someElement");
        test.setComponentProperty("someComponent");
        test.setTextProperty("someText");
        test.addHiddenProperty("someHidden");
        test.setFlavour("someElement", "blue");
        test.setParsingInterceptor(parsingInterceptor);
        test.setArooaAnnotations(arooaAnnotations);

        ArooaBeanDescriptor beanDescriptor = test.build();

        assertThat(beanDescriptor.getConfiguredHow("someAttribute"), is(ConfiguredHow.ATTRIBUTE));
        assertThat(beanDescriptor.getConfiguredHow("someElement"), is(ConfiguredHow.ELEMENT));
        assertThat(beanDescriptor.getConfiguredHow("someComponent"), is(ConfiguredHow.ELEMENT));
        assertThat(beanDescriptor.getConfiguredHow("someText"), is(ConfiguredHow.TEXT));
        assertThat(beanDescriptor.getConfiguredHow("someHidden"), is(ConfiguredHow.HIDDEN));

        assertThat(beanDescriptor.getComponentProperty(), is("someComponent"));
        assertThat(beanDescriptor.getTextProperty(), is("someText"));

        assertThat(beanDescriptor.isAuto("someElement"), is(true));
        assertThat(beanDescriptor.getFlavour("someElement"), is("blue"));

        assertThat(beanDescriptor.getParsingInterceptor(), sameInstance(parsingInterceptor));
        assertThat(beanDescriptor.getAnnotations(), sameInstance(arooaAnnotations));
    }

    @Test
    public void testSameProperties() {

        BeanDescriptorBuilder test = new BeanDescriptorBuilder(
                new SimpleArooaClass(Object.class));

        test.addElementProperty("apple");
        test.addAttributeProperty("apple");


        ArooaBeanDescriptor beanDescriptor = test.build();

        assertThat(beanDescriptor.getConfiguredHow("apple"), is(ConfiguredHow.ATTRIBUTE));

    }

}
