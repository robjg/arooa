/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ValueType;

import java.lang.reflect.Type;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

public class DefaultConverterTest {

    @SuppressWarnings("ConstantValue")
    @Test
    public void testAssumptions() {

        // This is slightly confusing, and also a awkward, otherwise
        // we could have an array convertlet for all Object[] classes.
        assertTrue(Object[].class.isAssignableFrom(String[].class));
        assertEquals(Object.class, String[].class.getSuperclass());

        // this is as expected
        assertFalse(Object[].class.isAssignableFrom(int[].class));

        String[] sa = {"a", "b"};
        Object[] oa = sa;
        sa = (String[]) oa;

        assertArrayEquals(sa, oa);

        // didn't know this!
        assertTrue(Cloneable.class.isAssignableFrom(Object[].class));
    }

    @SuppressWarnings("ConstantValue")
    @Test
    public void testPrimitiveAssumptions() throws ArooaConversionException {

        // doesn't take into account auto-boxing
        assertThat(boolean.class.isAssignableFrom(Boolean.class), is(false));

        Convertlet<String, Boolean> convertlet = from -> true;

        Object converted1 = convertlet.convert("whatever");

        assertThat(converted1.getClass(), is(Boolean.class));
        assertThat(converted1, is(true));
        assertThat(converted1, is(Boolean.TRUE));

        boolean converted2 = convertlet.convert("whatever");

        assertThat(converted2, is(true));
        assertThat(converted2, is(Boolean.TRUE));
    }

    @SuppressWarnings({"SameParameterValue", "unchecked"})
    static <T> T fromClass(Object o, Class<T> cl) {
        T t = (T) o;
        assertThat(t.getClass(), is(Float.class));
        return t;
    }

    @SuppressWarnings({"SameParameterValue", "unchecked"})
    static <T> T fromType(Object o, Type ignored) {
        T t = (T) o;
        assertThat(t.getClass(), is(Float.class));
        return t;
    }

    @Test
    void primitiveInferences() {

        double d1 = fromClass(2.2F, float.class);
        assertThat(d1, closeTo(2.2, 0.001));

        assertThrows(ClassCastException.class, () -> {
            double d2 = fromType(2.2F, float.class);
            assertThat(d2, closeTo(2.2, 0.001));
        });

        // Intellij is telling us this is redundant, but it isn't!
        @SuppressWarnings("RedundantCast")
        double d3 = (float) fromType(2.2F, float.class);
        assertThat(d3, closeTo(2.2, 0.001));
    }



    /**
     * Test that the Converter chooses the possibility
     * with the shortest conversion path.
     */
    @Test
    public void testAroaValuePaths() throws ArooaConversionException {

        class MockArooaValue implements ArooaValue {
            int used;
        }

        class Conversions implements ConversionProvider {

            public void registerWith(ConversionRegistry registry) {
                registry.register(MockArooaValue.class, String.class,
                        from -> {
                            from.used = 1;
                            return null;
                        });
                registry.register(MockArooaValue.class, Long.class,
                        from -> {
                            from.used = 2;
                            return 42L;
                        });
            }
        }

        MockArooaValue v = new MockArooaValue();

        DefaultConversionRegistry reg = new DefaultConversionRegistry();
        reg.register(String.class, Short.class,
                from -> {
                    throw new UnsupportedOperationException("Unexpected.");
                });
        reg.register(Short.class, Integer.class,
                from -> {
                    throw new UnsupportedOperationException("Unexpected.");
                });
        reg.register(Long.class, Integer.class,
                from -> 42);
        new Conversions().registerWith(reg);

        DefaultConverter test = new DefaultConverter(reg);

        Object result = test.convert(v, Integer.class);

        assertEquals(42, result);

        assertEquals(2, v.used);

    }

    /**
     * Test that null in is null out, except for primatives.
     */
    @Test
    public void testNullFrom() throws ArooaConversionException {
        DefaultConverter test = new DefaultConverter(null);

        assertNull(test.convert(null, String.class));

        assertEquals(false, test.convert(null, boolean.class));
        assertEquals((byte) 0, (byte) test.convert(null, byte.class));
        assertEquals('\0', (char) test.convert(null, char.class));
        assertEquals(0, (short) test.convert(null, short.class));
        assertEquals(0, (int) test.convert(null, int.class));
        assertEquals(0L, (long) test.convert(null, long.class));
        float f = test.convert(null, float.class);
        assertThat(f  == 0.0F, is(true));
        assertEquals(0.0, test.convert(null, double.class), 0.01);
    }

    @Test
    public void testEmptyStringConversions() throws NoConversionAvailableException, ConversionFailedException {

        DefaultConverter test = new DefaultConverter();

        assertThat(test.convert(" ", boolean.class), is(false));
        assertEquals((byte) 0, (byte) test.convert(" ", byte.class));
        assertEquals('\0', (char) test.convert(" ", char.class));
        assertEquals(0, (short) test.convert(" ", short.class));
        assertEquals(0, (int) test.convert(" ", int.class));
        assertEquals(0L, (long) test.convert(" ", long.class));
        float f = test.convert(" ", float.class);
        assertThat(f  == 0.0F, is(true));
        assertEquals(0.0, test.convert(" ", double.class), 0.01);
    }

    /**
     * Test a non ArooaValue can be converted to an ArooaValue.
     */
    @Test
    public void testToArooaValue() throws ArooaConversionException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ArooaConverter test = new DefaultConverter(registry);

        ArooaValue result = test.convert("Test", ArooaValue.class);

        assertNotNull(result);
    }

    /**
     * Convert fromArooaValue to an object.
     */
    @Test
    public void testFromArooaValueConvertObject() throws ArooaConversionException {
        class AV implements ArooaValue {
        }

        DefaultConverter test = new DefaultConverter(
                new ConversionLookup() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public <F, T> ConversionPath<F, T> findConversion(Type from, Type to) {
                        assertEquals(AV.class, from);
                        assertEquals(String.class, to);
                        ConversionPath<AV, AV> conversion = DefaultConversionPath.instance(AV.class);
                        ConversionPath<AV, String> conversion2 = conversion.append(new ConversionStep<>() {
                            public Class<AV> getFromClass() {
                                return AV.class;
                            }

                            public Class<String> getToClass() {
                                return String.class;
                            }

                            public String convert(AV from, ArooaConverter converter) {
                                return "test";
                            }

                        });
                        return (ConversionPath<F, T>) conversion2;
                    }
                });

        String result = test.convert(new AV(), String.class);

        assertEquals("test", result);
    }

    /**
     * Exactly the same test as above but not with real types.
     * <p>
     * Sanity check when tracking down a bug - conversion from ValueType
     * to Object is Object. (even though ValueType is an instance of Object)
     */
    @Test
    public void testFromArooaValueConvertObject2() throws ArooaConversionException {
        ValueType vt = new ValueType();
        vt.setValue(new ArooaObject("test"));

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ArooaConverter test = new DefaultConverter(registry);

        String result = test.convert(vt, Object.class);

        assertEquals("test", result);
    }

    /**
     * Check that a valueFor can convert null.
     */
    @Test
    public void testArooaValueConvertNull() throws ArooaConversionException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ArooaConverter test = new DefaultConverter(registry);

        class AV implements ArooaValue {
        }

        registry.register(AV.class, String.class, from -> null);

        Integer result = test.convert(
                new AV(), Integer.class);

        assertNull(result);
    }

    /**
     * Test that implemenation of an ArooaValue is not converted when
     * an ArooaValue is required.
     *
     */
    @Test
    public void testFromArooaValueToArooaValue() throws ArooaConversionException {

        ValueType vt = new ValueType();

        DefaultConverter test = new DefaultConverter();

        Object result = test.convert(vt, ArooaValue.class);

        assertSame(vt, result);
    }

    /**
     * Test some everyday conversions.
     *
     */
    @Test
    public void testSomeConversions() throws ArooaConversionException {

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ArooaConverter converter = new DefaultConverter(registry);

        assertThat(converter.convert("1", Integer.class), is(1));

        assertThat(converter.convert(42.24F, Double.class),
                Matchers.closeTo(42.24, 0.0001));
    }


    @Test
    public void testArooaValueArrayConvert() throws Exception {
        ValueType vt1 = new ValueType();
        vt1.setValue(new ArooaObject("a"));

        ValueType vt2 = new ValueType();
        vt2.setValue(new ArooaObject("b"));

        ValueType[] vta = {vt1, vt2};

        DefaultConversionRegistry registry = new DefaultConversionRegistry();
        new DefaultConversionProvider().registerWith(registry);

        ArooaConverter test = new DefaultConverter(registry);

        String[] result = test.convert(vta, String[].class);

        assertEquals("a", result[0]);
        assertEquals("b", result[1]);
    }
}
