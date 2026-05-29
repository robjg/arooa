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

public class ByteConvertletsTest extends Assert {

    @Test
    public void testNumberToByte() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new ByteConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<Number, Byte> path = lookup.findConversion(
                Number.class, Byte.class);

        Byte result = path.convert(42.24f, null);

        assertEquals(Byte.valueOf((byte) 42), result);
    }

    @Test
    public void testStringToByte() throws ConversionFailedException {
        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new ByteConvertlets().registerWith(registry);

        ConversionLookup lookup = registry.get();

        ConversionPath<String, Byte> path = lookup.findConversion(
                String.class, Byte.class);

        assertEquals("String-Byte", path.toString());

        assertEquals(Byte.valueOf((byte) 42), path.convert("42", null));

        try {
            path.convert("257", null);
            fail("ValueOutOfRange exception expected.");

        } catch (ConversionFailedException e) {
            // expected.
        }
    }

}
