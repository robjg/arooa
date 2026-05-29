/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert.convertlets;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.DefaultConversionRegistry;

import java.math.BigDecimal;

public class FloatConvertletsTest extends Assert {

    @Test
    public void testNumberToFloat() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new FloatConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Number, Float> path = lookup.findConversion(
                Number.class, Float.class);

        Float result = path.convert(new BigDecimal("42.24"), null);

        assertEquals(42.24, result, 0.001);
    }

    @Test
    public void testStringToFloat() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new FloatConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<String, Float> path = lookup.findConversion(
                String.class, Float.class);

        Float result = path.convert("42.24", null);

        assertEquals(42.24, result, 0.001);
    }

}
