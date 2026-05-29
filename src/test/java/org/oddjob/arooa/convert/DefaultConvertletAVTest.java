package org.oddjob.arooa.convert;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaValue;

/**
 * Test DefaultConvertletRegistry with ArooaValues.
 *
 * @author rob
 *
 */
public class DefaultConvertletAVTest extends Assert {

    static class Apples {

    }

    static class OurArooaValue implements ArooaValue {

        public Apples toApples() {
            return new Apples();
        }
    }

    static class OurConvertlet implements Convertlet<OurArooaValue, Apples> {

        public Apples convert(OurArooaValue from) throws ConvertletException {
            return from.toApples();
        }
    }

    @Test
    public void testToObject() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConversionRegistry test = new DefaultConversionRegistry();

        test.register(OurArooaValue.class, Apples.class, new OurConvertlet());

        ConversionLookup lookup = test.get();

        DefaultConverter converter = new DefaultConverter(lookup);

        Object result = converter.convert(
                new OurArooaValue(), Object.class);

        assertEquals(Apples.class, result.getClass());
    }

    static class DaddyArooaValue implements ArooaValue {

        public OurArooaValue toValue() {
            return new OurArooaValue();
        }
    }

    static class DaddyConvertlet implements Convertlet<DaddyArooaValue, OurArooaValue> {

        public OurArooaValue convert(DaddyArooaValue from) throws ConvertletException {
            return from.toValue();
        }
    }

    /**
     * Test ArooaValue.
     */
    @Test
    public void testToArooaValue() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConversionRegistry test = new DefaultConversionRegistry();

        test.register(OurArooaValue.class, Apples.class, new OurConvertlet());
        test.register(DaddyArooaValue.class, OurArooaValue.class, new DaddyConvertlet());

        ConversionLookup lookup = test.get();

        DefaultConverter converter = new DefaultConverter(lookup);

        Object result = converter.convert(
                new DaddyArooaValue(), Object.class);

        assertEquals(Apples.class, result.getClass());
    }

}
