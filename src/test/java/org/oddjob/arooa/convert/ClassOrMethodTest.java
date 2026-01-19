package org.oddjob.arooa.convert;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class ClassOrMethodTest {

    @Test
    void ofClass() {

        ClassOrMethod test = ClassOrMethod.ofClass(ClassOrMethod.class);

        assertThat(test.getCanonicalClassName(), is(ClassOrMethod.class.getTypeName()));
        assertThat(test.getName(), is(ClassOrMethod.class.getTypeName()));
        assertThat(test.toString(), is("AsClass{name='"
                + ClassOrMethod.class.getTypeName() + "'}"));

        ClassOrMethod test2 = ClassOrMethod.ofClass(ClassOrMethod.class);

        assertThat(test, is(test2));
    }

    static class Nested {
    }

    @Test
    void ofNestedType() {

        ClassOrMethod test = ClassOrMethod.ofClass(ClassOrMethodTest.Nested.class);

        assertThat(test.getCanonicalClassName(), is(ClassOrMethodTest.Nested.class.getCanonicalName()));
        assertThat(test.getName(), is(ClassOrMethodTest.Nested.class.getCanonicalName()));
        assertThat(test.toString(), is("AsClass{name='"
                + ClassOrMethodTest.Nested.class.getCanonicalName() + "'}"));

        ClassOrMethod test2 = ClassOrMethod.ofClass(ClassOrMethodTest.Nested.class);

        assertThat(test, is(test2));
    }

    @Test
    void ofLambda() {

        Function<?, ?> foo = x -> x;

        ClassOrMethod test = ClassOrMethod.ofClass(foo.getClass());

        assertThat(test.getCanonicalClassName(), nullValue());
    }

    @Test
    void ofMethod() throws NoSuchMethodException {

        Method method = ClassOrMethodTest.class.getDeclaredMethod("ofMethod");

        String methodName = method.getName();

        ClassOrMethod test = ClassOrMethod.ofTypeAndMethodNames(
                ClassOrMethodTest.class.getTypeName(), methodName);

        String expectedName = ClassOrMethodTest.class.getTypeName() + "#" + methodName;

        assertThat(test.getCanonicalClassName(), is(ClassOrMethodTest.class.getTypeName()));
        assertThat(test.getName(), is(expectedName));
        assertThat(test.toString(), is("AsMethod{name='"
                + expectedName + "'}"));

        ClassOrMethod test2 = ClassOrMethod.ofClass(ClassOrMethodTest.class);

        assertThat(test, not(is(test2)));

        ClassOrMethod test3 = ClassOrMethod.ofMethod(method);

        assertThat(test, is(test3));
    }
}