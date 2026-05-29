package org.oddjob.arooa.convert.jokers;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.convert.*;

public class EnumConversionsTest extends Assert {

    enum Deed {
        GOOD,
        BAD
    }

    @Test
    public void testStringConversions() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();

        new EnumConversions().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ArooaConverter converter = new DefaultConverter(lookup);

        assertEquals(Deed.GOOD, converter.convert("GOOD", Deed.class));

        assertEquals("BAD", converter.convert(Deed.BAD, String.class));
    }

    /**
     * What's a Shadow Joker? I think this means something the masks the
     * default conversion.
     *
     */
    @Test
    public void testShadowJoker() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();

        new EnumConversions().registerWith(registry);
        registry.registerJoker(Deed.class, new Joker<>() {
            public <T> ConversionStep<Deed, T> lastStep(
                    final Class<? extends Deed> from,
                    final Class<T> to,
                    ConversionLookup conversions) {

                if (String.class.isAssignableFrom(to)) {
                    return new ConversionStep<>() {
                        public Class<Deed> getFromClass() {
                            return Deed.class;
                        }

                        public Class<T> getToClass() {
                            return to;
                        }

                        @SuppressWarnings("unchecked")
                        public T convert(Deed from, ArooaConverter converter) {
                            return switch (from) {
                                case GOOD -> (T) "Have A Medal";
                                case BAD -> (T) "Go To Jail";
                            };
                        }
                    };
                }
                return null;
            }
        });

        ConversionLookup lookup = registry.get();

        ArooaConverter converter = new DefaultConverter(lookup);

        assertEquals("Have A Medal", converter.convert(Deed.GOOD, String.class));
        assertEquals("Go To Jail", converter.convert(Deed.BAD, String.class));

    }

    @Test
    public void testShadowConversion() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();

        new EnumConversions().registerWith(registry);
        registry.register(Deed.class, String.class,
                from -> switch (from) {
                    case GOOD -> "Have A Medal";
                    case BAD -> "Go To Jail";
                });

        ConversionLookup lookup = registry.get();

        ArooaConverter converter = new DefaultConverter(lookup);

        assertEquals("Have A Medal", converter.convert(Deed.GOOD, String.class));
        assertEquals("Go To Jail", converter.convert(Deed.BAD, String.class));

    }

    enum GradedDeed {
        ANGELIC(10),
        GOOD(5),
        BAD(-5),
        EVIL(-10);

        final int score;

        GradedDeed(int score) {
            this.score = score;
        }

        @Override
        public String toString() {
            return super.toString() + ": " + score;
        }
    }

    @Test
    public void testComplicatedDeed() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();

        new EnumConversions().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ArooaConverter converter = new DefaultConverter(lookup);

        assertEquals(GradedDeed.GOOD, converter.convert("GOOD", GradedDeed.class));

        assertEquals("BAD: -5", converter.convert(GradedDeed.BAD, String.class));
    }

    interface Colour {
    }

    enum Colours implements Colour {

        RED,
        BLUE,
        GREEN,
    }

    static class ColourConversions implements ConversionProvider {

        @Override
        public void registerWith(ConversionRegistry registry) {
            registry.register(String.class, Colours.class, Colours::valueOf);
        }
    }

    /**
     * Showing special conversion is necessary otherwise the default converter
     * has no idea which enum to convert to, to match the interface.
     */
    @Test
    public void testAnEnumThatImplementsAnInterface() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();

        new ColourConversions().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ArooaConverter converter = new DefaultConverter(lookup);

        ConversionPath<String, Colour> path = converter.findConversion(
                String.class, Colour.class);

        assertEquals("String-Colours", path.toString());

        assertEquals(Colours.RED, converter.convert("RED", Colour.class));
    }
}
