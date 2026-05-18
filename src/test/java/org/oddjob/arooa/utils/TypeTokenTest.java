package org.oddjob.arooa.utils;


import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

class TypeTokenTest {

    @Test
    void simpleListString() {

        TypeToken<List<String>> typeToken = new TypeToken<>() {};

        Type type = typeToken.getType();

        assertThat(type, instanceOf(ParameterizedType.class));

        ParameterizedType parameterizedType = (ParameterizedType) type;

        assertThat(parameterizedType.getRawType(), is(List.class));
        assertThat(parameterizedType.getActualTypeArguments()[0], is(String.class));
    }

    @Test
    void wildcardListString() {

        TypeToken<List<? extends String>> typeToken = new TypeToken<>() {};

        Type type = typeToken.getType();

        assertThat(type, instanceOf(ParameterizedType.class));

        ParameterizedType parameterizedType = (ParameterizedType) type;

        assertThat(parameterizedType.getRawType(), is(List.class));

        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];

        assertThat(actualTypeArgument, instanceOf(WildcardType.class));

        WildcardType wildcardType = (WildcardType) actualTypeArgument;

        assertThat(wildcardType.getLowerBounds().length, is(0));
        assertThat(wildcardType.getUpperBounds().length, is(1));

        assertThat(wildcardType.getUpperBounds()[0], is(String.class));
    }
}