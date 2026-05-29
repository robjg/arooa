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

public class ShortConvertletsTest extends Assert {

    @Test
    public void testNumberToShort() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new ShortConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Number, Short> path = lookup.findConversion(
                Number.class, Short.class);

        Short result = path.convert(42.24f, null);

        assertEquals(Short.valueOf((short) 42), result);
    }

    @Test
    public void testStringToShort() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new ShortConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<String, Short> path = lookup.findConversion(
                String.class, Short.class);

        assertEquals("String-Short", path.toString());

        assertEquals(Short.valueOf((short) 42), path.convert("42", null));

        try {
            path.convert("32769", null);
            fail("ValueOutOfRange excpetion expected.");

        } catch (ConversionFailedException e) {
            // expected.
        }
    }

}
