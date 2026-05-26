package org.oddjob.arooa.beanutils;

import org.junit.jupiter.api.Test;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.TypeArooa;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.utils.TypeToken;

import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class BeanUtilsPropertyAccessorGenericsTest {

    static class ThingWithFunctions {

        public Function<String, Integer> stringToInt() {
            return Integer::parseInt;
        }

        public Function<String, Double> stringToDouble() {
            return Double::parseDouble;
        }
    }


    @Test
    void genericSetters() {

        DefaultConversionRegistry conversionRegistry = new DefaultConversionRegistry();
        conversionRegistry.register(
                TypeArooa.ofArooaValue(ThingWithFunctions.class),
                TypeArooa.of(new TypeToken<Function<String, Integer>>(){}.getType()),
                ThingWithFunctions::stringToInt);
        conversionRegistry.register(
                TypeArooa.ofArooaValue(ThingWithFunctions.class),
                TypeArooa.of(new TypeToken<Function<String, Double>>(){}.getType()),
                ThingWithFunctions::stringToDouble);

        ArooaConverter converter = new DefaultConverter(conversionRegistry);

        PropertyAccessor test = new BeanUtilsPropertyAccessor()
                .accessorWithConversions(converter);

        ThingWithGenericSetters subject = new ThingWithGenericSetters();

        test.setProperty(subject, "functionStringToInteger", new ThingWithFunctions());

        assertThat(subject.func1.apply("42"), is(42));

        test.setProperty(subject, "functionStringToDouble", new ThingWithFunctions());

        assertThat(subject.func2.apply("4.2"), is(4.2));
    }

    public static class ThingWithGenericSetters {

        Function<? super String, ? extends Integer> func1;

        Function<? super String, ? extends Double> func2;

        public void setFunctionStringToInteger(Function<? super String, ? extends Integer> func) {
            this.func1 = func;
        }

        public void setFunctionStringToDouble(Function<? super String, ? extends Double> func) {
            this.func2 = func;
        }
    }
}
