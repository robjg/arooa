/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.convert.*;

public class BooleanConvertletsTest extends Assert {

    @Test
    public void testNumberToBoolean() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new BooleanConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Number, Boolean> path = lookup.findConversion(
                Number.class, Boolean.class);

        assertEquals(Boolean.TRUE,
                path.convert(42.24, null));

        assertEquals(Boolean.FALSE,
                path.convert((short) 0, null));
    }

    @Test
    public void testBooleanToNumber() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new BooleanConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Boolean, Number> path = lookup.findConversion(
                Boolean.class, Number.class);

        assertEquals(1, path.convert(Boolean.TRUE, null));

        assertEquals(0, path.convert(Boolean.FALSE, null));
    }

    @Test
    public void testStringToBoolean() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new BooleanConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<String, Boolean> path = lookup.findConversion(
                String.class, Boolean.class);

        assertEquals(Boolean.TRUE,
                path.convert("yes", null));

        assertEquals(Boolean.FALSE,
                path.convert("no", null));
    }

    @Test
    public void testBooleanToString() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Boolean, String> path = lookup.findConversion(
                Boolean.class, String.class);

        assertEquals("true",
                path.convert(Boolean.TRUE, null));

        assertEquals("false",
                path.convert(Boolean.FALSE, null));
    }

}
