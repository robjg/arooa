package org.oddjob.arooa.convert;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.doc.ElementIdentifier;
import org.oddjob.arooa.convert.doc.TypeIdentifier;

import java.lang.reflect.Method;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ElementIdentifierTest {

    @Test
    void ofClass() {

        ElementIdentifier test = ElementIdentifier.ofClass(ElementIdentifier.class);

        assertThat(test.getTypeIdentifier(), sameInstance(test));
        assertThat(test.getName(), is(ElementIdentifier.class.getTypeName()));
        assertThat(test.toString(), is("ClassTypeIdentifier{name='"
                + ElementIdentifier.class.getTypeName() + "'}"));

        ElementIdentifier test2 = ElementIdentifier.ofClass(ElementIdentifier.class);

        assertThat(test, is(test2));
    }

    static class Nested {
    }

    @Test
    void ofNestedType() {

        ElementIdentifier test = ElementIdentifier.ofClass(ElementIdentifierTest.Nested.class);

        assertThat(test.getTypeIdentifier(), sameInstance(test));
        assertThat(test.getName(), is(ElementIdentifierTest.Nested.class.getCanonicalName()));
        assertThat(test.toString(), is("ClassTypeIdentifier{name='"
                + ElementIdentifierTest.Nested.class.getCanonicalName() + "'}"));

        ElementIdentifier test2 = ElementIdentifier.ofClass(ElementIdentifierTest.Nested.class);

        assertThat(test, is(test2));
    }

    @Test
    void ofLambda() {

        Function<?, ?> foo = x -> x;

        Class<?> lambdaClass = foo.getClass();

        assertThat(lambdaClass.getCanonicalName(), nullValue());

        ElementIdentifier test = ElementIdentifier.ofClass(foo.getClass());

        assertThat(test, nullValue());
    }

    @Test
    void ofMethod() throws NoSuchMethodException {

        Method method = ElementIdentifierTest.class.getDeclaredMethod("ofMethod");

        String methodName = method.getName();

        ElementIdentifier test = ElementIdentifier.ofMethod(method);

        String expectedName = ElementIdentifierTest.class.getTypeName() + "#" + methodName;

        assertThat(test.getTypeIdentifier(), is(TypeIdentifier.ofClass(ElementIdentifierTest.class)));
        assertThat(test.getName(), is(expectedName));
        assertThat(test.toString(), is("AsMethod{name='"
                + expectedName + "'}"));

        ElementIdentifier test2 = ElementIdentifier.ofClass(ElementIdentifierTest.class);

        assertThat(test, not(is(test2)));

        ElementIdentifier test3 = ElementIdentifier.ofMethod(method);

        assertThat(test, is(test3));
    }
}