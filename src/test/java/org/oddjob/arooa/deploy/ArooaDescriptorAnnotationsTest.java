package org.oddjob.arooa.deploy;

import org.junit.Test;
import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beanutils.BeanUtilsPropertyAccessor;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ArooaDescriptorAnnotationsTest {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface DoStuff {

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface MoreStuff {

    }

    public static class MyBean {

        @DoStuff
        public void myDoStuff() {

        }

        public void myDestroy() {

        }

        public void setColour(String colour) {

        }

        public void acceptSpecial(String special, int factor) {

        }

        public void acceptSpecial(String special, String other) {

        }
    }

    @Test
    public void testDescriptorAndAnnotations() {

        ArooaDescriptor descriptor =
                new ConfigurationDescriptorFactory(
                        new XMLConfiguration(
                                "org/oddjob/arooa/deploy/ArooaDescriptorAnnotationsTest.xml",
                                getClass().getClassLoader())).createDescriptor(
                        getClass().getClassLoader());

        BeanUtilsPropertyAccessor accessor = new BeanUtilsPropertyAccessor();

        ArooaAnnotations test = descriptor.getBeanDescriptor(
                new SimpleArooaClass(MyBean.class),
                accessor).getAnnotations();

        Method doStuff = test.methodFor(DoStuff.class.getName());
        assertThat(doStuff.getName(), is("myDoStuff"));

        Method destroy = test.methodFor("org.oddjob.arooa.life.ArooaDestroy");
        assertThat(destroy.getName(), is("myDestroy"));

        Method acceptSpecial = test.methodFor(
                "org.oddjob.arooa.deploy.test.Special");
        assertThat(acceptSpecial.getName(), is("acceptSpecial"));
        assertThat(acceptSpecial.getParameterTypes().length, is(2));

        assertThat(test.methodFor("idontexist"), nullValue());
    }

    @Test
    public void testPropertyAnnotations() {

        ArooaDescriptor descriptor =
                new ConfigurationDescriptorFactory(
                        new XMLConfiguration(
                                "org/oddjob/arooa/deploy/ArooaDescriptorAnnotationsTest2.xml",
                                getClass().getClassLoader())).createDescriptor(
                        getClass().getClassLoader());

        BeanUtilsPropertyAccessor accessor = new BeanUtilsPropertyAccessor();

        ArooaBeanDescriptor beanDescriptor = descriptor.getBeanDescriptor(
                new SimpleArooaClass(MyBean.class),
                accessor);

        ArooaAnnotations test = beanDescriptor.getAnnotations();

        assertThat(test.propertyFor("org.oddjob.arooa.deploy.test.Special"), is("colour"));

        assertThat(beanDescriptor.getComponentProperty(), is("colour"));

        assertThat(beanDescriptor.getConfiguredHow("colour"), is(ConfiguredHow.ELEMENT));
    }
}
