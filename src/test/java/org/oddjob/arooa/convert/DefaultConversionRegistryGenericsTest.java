package org.oddjob.arooa.convert;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.function.Function;

public class DefaultConversionRegistryGenericsTest {

    static class FunctionSupplier {

        public Function<String, Integer> intParser() {
            return Integer::parseInt;
        }

        public Function<String, Double> doubleParser() {
            return Double::parseDouble;
        }
    }

    Function<? super String, ? extends Integer> intParser;

    Function<? super String, ? extends Double> doubleParser;

    public void setIntParser(Function<? super String, ? extends Integer> intParser) {
        this.intParser = intParser;
    }

    public void setDoubleParser(Function<? super String, ? extends Double> doubleParser) {
        this.doubleParser = doubleParser;
    }

    @Test
    void correctFunctionSet() throws NoSuchMethodException {

        final Type retType1 = FunctionSupplier.class.getDeclaredMethod("intParser")
                .getGenericReturnType();
        final Type retType2 = FunctionSupplier.class.getDeclaredMethod("doubleParser")
                .getGenericReturnType();

        final Type intParserParam = getClass().getDeclaredMethod("setIntParser", Function.class)
                .getGenericParameterTypes()[0];

        DefaultConversionRegistry conversionRegistry = new DefaultConversionRegistry();

        conversionRegistry.register(TypeArooa.of(FunctionSupplier.class), TypeArooa.of(intParserParam),
                FunctionSupplier::intParser);

        ConversionPath<?, ?> conversionPath =
                conversionRegistry.findConversion(TypeArooa.of(FunctionSupplier.class), intParserParam);

    }
}
