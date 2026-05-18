package org.oddjob.arooa.convert;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.types.ValueType;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TypeArooasTest {

    @Test
    void simpleClasses() {
        
        TypeArooa<Number> numberType = TypeArooas.of(Number.class);
        TypeArooa<Integer> integerType = TypeArooas.of(Integer.class);

        assertThat(numberType.isAssignableFrom(integerType), is(true));
        assertThat(integerType.isAssignableFrom(numberType), is(false));
    }

    static class FunctionSupplier {

        public Function<String, Integer> intParser() {
            return Integer::parseInt;
        }

        public Function<String, Double> doubleParser() {
            return Double::parseDouble;
        }

        @SuppressWarnings("rawtypes")
        public Function rawThing() {
            return x -> null; }
    }

    public void setIntParser(Function<? super String, ? extends Integer> intParser) {

    }

    public void setDoubleParser(Function<? super String, ? extends Double> doubleParser) {

    }

    @SuppressWarnings("rawtypes")
    public void setRawThing(Function rawThing) {

    }

    @Test
    void parameterisedTypes() throws NoSuchMethodException {

        TypeArooa<?> retType1 =  TypeArooas.of(FunctionSupplier.class.getDeclaredMethod("intParser")
                .getGenericReturnType());
        TypeArooa<?> retType2 = TypeArooas.of(FunctionSupplier.class.getDeclaredMethod("doubleParser")
                .getGenericReturnType());
        TypeArooa<?> retType3 = TypeArooas.of(FunctionSupplier.class.getDeclaredMethod("rawThing")
                .getGenericReturnType());

        TypeArooa<?> intParserParam = TypeArooas.of(getClass().getDeclaredMethod("setIntParser", Function.class)
                .getGenericParameterTypes()[0]);
        TypeArooa<?> doubleParserParam = TypeArooas.of(getClass().getDeclaredMethod("setDoubleParser", Function.class)
                .getGenericParameterTypes()[0]);
        TypeArooa<?> rawThingParam = TypeArooas.of(getClass().getDeclaredMethod("setRawThing", Function.class)
                .getGenericParameterTypes()[0]);

        assertThat(intParserParam.isAssignableFrom(retType1), is(true));
        assertThat(intParserParam.isAssignableFrom(retType2), is(false));
        assertThat(intParserParam.isAssignableFrom(retType3), is(true));

        assertThat(doubleParserParam.isAssignableFrom(retType1), is(false));
        assertThat(doubleParserParam.isAssignableFrom(retType2), is(true));
        assertThat(doubleParserParam.isAssignableFrom(retType3), is(true));

        assertThat(rawThingParam.isAssignableFrom(retType1), is(true));
        assertThat(rawThingParam.isAssignableFrom(retType2), is(true));
        assertThat(rawThingParam.isAssignableFrom(retType3), is(true));
    }

    @Test
    void isArooaType() {

        assertThat(TypeArooas.of(Number.class).isArooaValue(), is(false));
        assertThat(TypeArooas.of(ValueType.class).isArooaValue(), is(true));

        assertThat(TypeArooas.of(Number.class)
                .isAssignableFrom(TypeArooas.ofArooaValue(Integer.class)), is(false));

        assertThat(TypeArooas.of(ArooaValue.class)
                .isAssignableFrom(TypeArooas.ofArooaValue(Integer.class)), is(false));
    }
}