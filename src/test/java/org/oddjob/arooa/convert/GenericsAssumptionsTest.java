package org.oddjob.arooa.convert;

import org.junit.jupiter.api.Test;

import java.lang.reflect.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class GenericsAssumptionsTest {

    public static class Container<T> {

        public T getThing() {
            return null;
        }

        public Iterable<T> getThings() {
            return null;
        }

        public T[] getThingArray() {
            return null;
        }

        public Function<? super T, ? extends T> getWildcardFunc() {
            return null;
        }
    }

    @Test
    void typeVariables() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        Container<?> container = Container.class.getConstructor().newInstance();

        Type thingType = container.getClass().getMethod("getThing").getGenericReturnType();

        assertThat(thingType, instanceOf(TypeVariable.class));

        Type thingsType = container.getClass().getMethod("getThings").getGenericReturnType();

        assertThat(thingsType, instanceOf(ParameterizedType.class));

        Type thingArrayType = container.getClass().getMethod("getThingArray").getGenericReturnType();

        assertThat(thingArrayType, instanceOf(GenericArrayType.class));

        ParameterizedType wildcardFuncType = (ParameterizedType) container.getClass()
                .getMethod("getWildcardFunc").getGenericReturnType();

        WildcardType wildcard1 = (WildcardType) wildcardFuncType.getActualTypeArguments()[0];
        assertThat(wildcard1.getTypeName(), is("? super T"));
        assertThat(wildcard1.getUpperBounds().length, is(1));
        assertThat(wildcard1.getUpperBounds()[0], is(Object.class));
        assertThat(wildcard1.getLowerBounds().length, is(1));
        assertThat(wildcard1.getLowerBounds()[0], instanceOf(TypeVariable.class));
        WildcardType wildcard2 = (WildcardType) wildcardFuncType.getActualTypeArguments()[1];
        assertThat(wildcard2.getTypeName(), is("? extends T"));
        assertThat(wildcard2.getUpperBounds().length, is(1));
        assertThat(wildcard2.getUpperBounds()[0], instanceOf(TypeVariable.class));
        assertThat(wildcard2.getLowerBounds().length, is(0));

    }

    public static class WildcardFoo<T extends String> {

        public T getThing() {
            return null;
        }

        public Iterable<T> getThings() {
            return null;
        }

        public T[] getThingArray() {
            return null;
        }

        public Function<? super T, ? extends T> getWildcardFunc() {
            return null;
        }
    }

    @Test
    void wildCardTypeParameter() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        WildcardFoo<?> container = WildcardFoo.class.getConstructor().newInstance();

        assertThat(container.getClass().getTypeParameters()[0], instanceOf(TypeVariable.class));

        Type thingType = container.getClass().getMethod("getThing").getGenericReturnType();

        assertThat(thingType, instanceOf(TypeVariable.class));

        Type thingsType = container.getClass().getMethod("getThings").getGenericReturnType();

        assertThat(thingsType, instanceOf(ParameterizedType.class));

        Type thingArrayType = container.getClass().getMethod("getThingArray").getGenericReturnType();

        assertThat(thingArrayType, instanceOf(GenericArrayType.class));

        Type wildcardFuncType = container.getClass().getMethod("getWildcardFunc").getGenericReturnType();

        assertThat(wildcardFuncType, instanceOf(ParameterizedType.class));
    }

    public static class MoreWildcards {

        public List<? extends String> listExtendsString() {
            return null;
        }

        public Consumer<? super String> consumerSuperString() {
            return null;
        }
    }

    @Test
    void wildCardExamples() throws NoSuchMethodException {

        ParameterizedType listExtendsString = (ParameterizedType) MoreWildcards.class
                .getMethod("listExtendsString").getGenericReturnType();

        assertThat(listExtendsString.getTypeName(), is("java.util.List<? extends java.lang.String>"));
        assertThat(listExtendsString.getActualTypeArguments().length, is(1));

        WildcardType wildcard1 = (WildcardType) listExtendsString.getActualTypeArguments()[0];
        assertThat(wildcard1.getTypeName(), is("? extends java.lang.String"));
        assertThat(wildcard1.getUpperBounds().length, is(1));
        assertThat(wildcard1.getUpperBounds()[0], is(String.class));
        assertThat(wildcard1.getLowerBounds().length, is(0));

        ParameterizedType consumerSuperString = (ParameterizedType) MoreWildcards.class
                .getMethod("consumerSuperString").getGenericReturnType();

        assertThat(consumerSuperString.getTypeName(), is("java.util.function.Consumer<? super java.lang.String>"));
        assertThat(consumerSuperString.getActualTypeArguments().length, is(1));

        WildcardType wildcard2 = (WildcardType) consumerSuperString.getActualTypeArguments()[0];
        assertThat(wildcard2.getTypeName(), is("? super java.lang.String"));
        assertThat(wildcard2.getUpperBounds().length, is(1));
        assertThat(wildcard2.getUpperBounds()[0], instanceOf(Object.class));
        assertThat(wildcard2.getLowerBounds().length, is(1));
        assertThat(wildcard2.getLowerBounds()[0], is(String.class));

    }
}