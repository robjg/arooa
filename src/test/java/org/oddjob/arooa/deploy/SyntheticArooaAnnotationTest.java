package org.oddjob.arooa.deploy;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.life.Configured;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class SyntheticArooaAnnotationTest {

    @Test
    void realAnnotation() throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {

        SyntheticArooaAnnotation test = SyntheticArooaAnnotation.named(
                Configured.class.getName());

        assertThat(test.getName(), is(Configured.class.getName()));
        assertThat(test.realAnnotation(Configured.class), instanceOf(Configured.class));


    }
}