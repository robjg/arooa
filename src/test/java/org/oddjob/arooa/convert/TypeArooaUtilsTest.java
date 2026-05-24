package org.oddjob.arooa.convert;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TypeArooaUtilsTest {

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

        assertThat(TypeArooaUtils.arooaEquivalent(thingType), is(Object.class));

        Type thingsType = container.getClass().getMethod("getThings").getGenericReturnType();

        assertThat(TypeArooaUtils.arooaEquivalent(thingsType),
                is(ParameterizedTypeArooa.of(Iterable.class, Object.class)));

        Type thingArrayType = container.getClass().getMethod("getThingArray").getGenericReturnType();

        assertThat(TypeArooaUtils.arooaEquivalent(thingArrayType), is(Object[].class));

        ParameterizedType wildcardFuncType = (ParameterizedType) container.getClass()
                .getMethod("getWildcardFunc").getGenericReturnType();

        assertThat(TypeArooaUtils.arooaEquivalent(wildcardFuncType),
                is(ParameterizedTypeArooa.of(Function.class, Object.class, Object.class)));
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

        Type thingType = TypeArooaUtils.arooaEquivalent(
                container.getClass().getMethod("getThing").getGenericReturnType());

        assertThat(thingType, is(Object.class));
        assertThat(TypeArooaUtils.isAssignable(thingType, String.class), is(true));

        Type thingsType = TypeArooaUtils.arooaEquivalent(
                container.getClass().getMethod("getThings").getGenericReturnType());

        assertThat(thingsType, is(ParameterizedTypeArooa.of(Iterable.class, Object.class)));
        assertThat(TypeArooaUtils.isAssignable(thingsType, List.class), is(true));
        assertThat(TypeArooaUtils.isAssignable(thingsType, Consumer.class), is(false));
        assertThat(TypeArooaUtils.isAssignable(thingsType, thingsType), is(true));
        assertThat(TypeArooaUtils.isAssignable(thingsType,
                        ParameterizedTypeArooa.of(List.class, String.class)), is(true));

        Type thingArrayType = container.getClass().getMethod("getThingArray").getGenericReturnType();

        assertThat(TypeArooaUtils.arooaEquivalent(thingArrayType), is(Object[].class));

        ParameterizedType wildcardFuncType = (ParameterizedType) container.getClass()
                .getMethod("getWildcardFunc").getGenericReturnType();

        assertThat(TypeArooaUtils.arooaEquivalent(wildcardFuncType),
                is(ParameterizedTypeArooa.of(Function.class, Object.class, Object.class)));
    }

    public static class MoreWildcards {

        public List<? extends String> listExtendsString() {
            return null;
        }

        public Consumer<? super String> consumerSuperString() {
            return null;
        }

        public Function<?, ?> funcOfAnything() {
            return null;
        }

        public Function<?, ?>[] arrayOfAnything() {
            return null;
        }
    }

    @Test
    void wildCardExamples() throws NoSuchMethodException {

        Type listExtendsString = TypeArooaUtils.arooaEquivalent(MoreWildcards.class
                .getMethod("listExtendsString").getGenericReturnType());

        assertThat(listExtendsString.getTypeName(), is("java.util.List<java.lang.String>"));

        assertThat(listExtendsString,
                is(ParameterizedTypeArooa.of(List.class, String.class)));

        assertThat(TypeArooaUtils.isAssignable(listExtendsString,
                ParameterizedTypeArooa.of(List.class, String.class)), is(true));

        Type consumerSuperString = TypeArooaUtils.arooaEquivalent(MoreWildcards.class
                .getMethod("consumerSuperString").getGenericReturnType());

        assertThat(consumerSuperString.getTypeName(), is("java.util.function.Consumer<java.lang.Object>"));

        assertThat(consumerSuperString, is(ParameterizedTypeArooa.of(Consumer.class, Object.class)));

        Type funcOfAnything = TypeArooaUtils.arooaEquivalent(MoreWildcards.class
                .getMethod("funcOfAnything").getGenericReturnType());

        assertThat(funcOfAnything.getTypeName(), is("java.util.function.Function<java.lang.Object, java.lang.Object>"));

        assertThat(funcOfAnything, is(ParameterizedTypeArooa.of(Function.class, Object.class, Object.class)));

        Type arrayOfAnything = TypeArooaUtils.arooaEquivalent(MoreWildcards.class
                .getMethod("arrayOfAnything").getGenericReturnType());

        assertThat(arrayOfAnything.getTypeName(), is("java.util.function.Function[]"));

        assertThat(arrayOfAnything, is(Function[].class));

        assertThat(TypeArooaUtils.isAssignable(arrayOfAnything, Object[].class), is(false));
        assertThat(TypeArooaUtils.isAssignable(Object[].class, arrayOfAnything), is(true));
    }
}