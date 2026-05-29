package org.oddjob.arooa.types;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.convert.*;

public class FactoryValueTest extends Assert {

    static class MyFactory implements ValueFactory<String> {
        public String toValue() {
            return "apple";
        }
    }

    @Test
    public void testMyFactory() throws NoConversionAvailableException, ConversionFailedException {

        ArooaConverter converter = new DefaultConverter();

        String string = converter.convert(new MyFactory(), String.class);

        assertEquals("apple", string);
    }

    @Test
    public void testMaskedJokerConversion() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        registry.registerJoker(MyFactory.class, new Joker<>() {
            public <T> ConversionStep<MyFactory, T> lastStep(
                    Class<? extends MyFactory> from,
                    Class<T> to,
                    ConversionLookup conversions) {
                return null;
            }
        });

        ConversionLookup lookup = registry.get();

        DefaultConverter converter = new DefaultConverter(lookup);

        String string = converter.convert(new MyFactory(), String.class);

        assertEquals("apple", string);
    }
}
