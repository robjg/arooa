package org.oddjob.arooa.deploy;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.life.Configured;

import javax.inject.Named;
import java.lang.annotation.Annotation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class SyntheticAnnotationTest {


    @Configured
    public void foo() {}

    @Test
    void basicMethods() throws NoSuchMethodException, ClassNotFoundException {

        Annotation realAnnotation = SyntheticAnnotationTest.class
                .getMethod("foo")
                .getAnnotation(Configured.class);

        assertThat(realAnnotation, instanceOf(Configured.class));

        Configured syntheticAnnotation = SyntheticAnnotation
                .named(Configured.class.getName());

        assertThat(syntheticAnnotation.toString(), is(realAnnotation.toString()));
        assertThat(syntheticAnnotation.equals(realAnnotation), is(true));
        assertThat(realAnnotation.equals(syntheticAnnotation), is(true));
        assertThat(syntheticAnnotation.hashCode(), is(realAnnotation.hashCode()));
        assertThat(syntheticAnnotation.getClass().getName(), startsWith("jdk.proxy"));
    }

    @Named("Foo")
    public void bar() {
    }


    @Test
    void withValue() throws ClassNotFoundException, NoSuchMethodException {

        Annotation realAnnotation = SyntheticAnnotationTest.class
                .getMethod("bar")
                .getAnnotation(Named.class);

        Named syntheticAnnotation = (Named) SyntheticAnnotation
                .with()
                .value("Foo")
                .named(Named.class.getName());

        assertThat(syntheticAnnotation.toString(), is(realAnnotation.toString()));
        assertThat(syntheticAnnotation.equals(realAnnotation), is(true));
        assertThat(realAnnotation.equals(syntheticAnnotation), is(true));
        assertThat(syntheticAnnotation.hashCode(), is(realAnnotation.hashCode()));
    }


}