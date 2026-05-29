/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.*;

import java.math.BigDecimal;

public class LongConvertletsTest extends Assert {

    @Test
    public void testNumberToLong() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new LongConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Number, Long> path = lookup.findConversion(
                Number.class, Long.class);

        Long result = path.convert(new BigDecimal("4.2E10"), null);

        assertEquals(Long.valueOf(42000000000L), result);
    }

    @Test
    public void testStringToLong() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new LongConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<String, Long> path = lookup.findConversion(
                String.class, Long.class);

        assertEquals("String-Long", path.toString());

        Long result = path.convert("200909091234567890", null);

        assertEquals(Long.valueOf(200909091234567890L), result);
    }

    @Test
    public void testLongToString() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Long, String> path = lookup.findConversion(
                Long.class, String.class);

        assertEquals("Long-String", path.toString());

        String result = path.convert(200909091234567890L, null);

        assertEquals("200909091234567890", result);
    }

    @Test
    public void testLongToObject() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Long, Object> path = lookup.findConversion(
                Long.class, Object.class);

        assertEquals(0, path.length());

        Object result = path.convert(200909091234567890L, null);

        assertEquals(200909091234567890L, result);
    }

    @Test
    public void testLongToArooaValue() throws ConversionFailedException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Long, ArooaValue> path = lookup.findConversion(
                Long.class, ArooaValue.class);

        assertEquals("Long-Number-Object-ArooaValue", path.toString());

        ArooaValue result = path.convert(200909091234567890L, null);

        assertEquals("200909091234567890", result.toString());
    }
}
