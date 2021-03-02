package org.oddjob.arooa.utils;

import org.hamcrest.collection.ArrayMatching;
import org.junit.Test;
import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.life.Initialised;
import org.oddjob.arooa.standard.StandardArooaSession;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AnnotationFinderTest {

    public static class Foo {

        @Initialised
        public void wow() {

        }

        @ArooaAttribute
        public void setStuff(String stuff) {
        }
    }

    @Test
    public void testWithAnnotation() {

        ArooaSession session = new StandardArooaSession();

        ArooaAnnotations annotations = AnnotationFinder.forSession(session)
                .findFor(Foo.class);

        assertThat(annotations.annotatedProperties(),
                ArrayMatching.hasItemInArray( "stuff" ));

        assertThat(annotations.methodFor(Initialised.class.getName()).getName(),
                is("wow" ));
    }
}