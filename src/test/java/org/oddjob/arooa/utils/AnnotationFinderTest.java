package org.oddjob.arooa.utils;

import org.junit.Test;
import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.Initialised;
import org.oddjob.arooa.standard.StandardArooaSession;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AnnotationFinderTest {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ReallySpecial {

    }

    public static class Foo {

        @Initialised
        public void wow() {

        }

        @ReallySpecial
        public void setStuff(String stuff) {
        }
    }

    @Test
    public void testWithAnnotation() {

        ArooaSession session = new StandardArooaSession();

        ArooaAnnotations annotations = AnnotationFinder.forSession(session)
                .findFor(Foo.class);

        assertThat(annotations.propertyFor(ReallySpecial.class.getName()),
                is( "stuff" ));

        assertThat(annotations.methodFor(Initialised.class.getName()).getName(),
                is("wow" ));
    }
}