/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.types.ArooaObject;
import org.oddjob.arooa.types.ValueType;

public class DefaultConverterTest extends TestCase {

	public void testAssumptions() {
		
		// This is slightly confusing, and also a awkward, otherwise
		// we could have an array convertlet for all Object[] classes.
		assertTrue(Object[].class.isAssignableFrom(String[].class));				       
		assertEquals(Object.class, String[].class.getSuperclass());
		
		// this is as expected
		assertFalse(Object[].class.isAssignableFrom(int[].class));
		
		String[] sa = { "a", "b" };
		Object[] oa = sa;
		sa = (String[]) oa;
		
		assertEquals(sa, oa);
		
		// didn't know this!
		assertTrue(Cloneable.class.isAssignableFrom(Object[].class));
	}
	
	public void testPrimitiveAssumptions() throws ConvertletException {

		// doesn't take into account auto-boxing
		assertFalse(boolean.class.isAssignableFrom(Boolean.class));
		
		Convertlet<String, Boolean> convertlet = new Convertlet<String, Boolean>() {
			
			@Override
			public Boolean convert(String from) throws ConvertletException {
				return true;
			}
		};
		
		boolean converted1 = convertlet.convert("whatever");
		
		assertEquals(true, converted1);
		
		Boolean converted2 = convertlet.convert("whatever");
		
		assertEquals(Boolean.TRUE, converted2);
	}
	
	/**
	 * Test that the Converter chooses the possibility
	 * with the shortest conversion path.
	 * 
	 * @throws NoConversionAvailableException
	 */
	public void testAroaValuePaths() throws ArooaConversionException {
		
		class MockArooaValue implements ArooaValue {
			int used;
		};

		class Conversions implements ConversionProvider {
			
			public void registerWith(ConversionRegistry registry) {
				registry.register(MockArooaValue.class, String.class, 
						new Convertlet<MockArooaValue, String>() {
					public String convert(MockArooaValue from) throws ConvertletException {
						from.used = 1;
						return null;
					}
				});
				registry.register(MockArooaValue.class, Long.class, 
						new Convertlet<MockArooaValue, Long>() {
					public Long convert(MockArooaValue from) throws ConvertletException {
						from.used = 2;
						return new Long(42);
					}
				});
			}
		}
		
		MockArooaValue v = new MockArooaValue();

		DefaultConversionRegistry reg = new DefaultConversionRegistry();		
		reg.register(String.class, Short.class, 
				new Convertlet<String, Short>() {
			public Short convert(String from) throws ConvertletException {
				throw new UnsupportedOperationException("Unexpected.");
			}
		});
		reg.register(Short.class, Integer.class, 
				new Convertlet<Short, Integer>() {
			public Integer convert(Short from) throws ConvertletException {
				throw new UnsupportedOperationException("Unexpected.");
			}
		});
		reg.register(Long.class, Integer.class, 
				new Convertlet<Long, Integer>() {
			public Integer convert(Long from) throws ConvertletException {
				return new Integer(42);
			}
		});
		new Conversions().registerWith(reg);
		
		DefaultConverter test = new DefaultConverter(reg);
		
		Object result = test.convert(v, Integer.class); 

		assertEquals(new Integer(42), result);
		
		assertEquals(2, v.used);
		
	}
	
	/**
	 * Test that null in is null out, except for primatives.
	 * 
	 * @throws NoConversionAvailableException
	 */
	public void testNullFrom() throws ArooaConversionException {
		DefaultConverter test = new DefaultConverter(null);

		assertNull(test.convert(null, String.class));
		
		assertEquals(false, (boolean) test.convert(null, boolean.class));
		assertEquals((byte) 0, (byte) test.convert(null, byte.class));
		assertEquals('\0', (char) test.convert(null, char.class));
		assertEquals(0, (short) test.convert(null, short.class));
		assertEquals(0, (int) test.convert(null, int.class));
		assertEquals(0L, (long) test.convert(null, long.class));
		assertEquals(0.0F, (float) test.convert(null, float.class));
		assertEquals(0.0, (double) test.convert(null, double.class));
	}
	
	public void testEmptyStringConversions() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConverter test = new DefaultConverter();
		
		assertEquals(false, (boolean) test.convert(" ", boolean.class));
		assertEquals((byte) 0, (byte) test.convert(" ", byte.class));
		assertEquals('\0', (char) test.convert(" ", char.class));
		assertEquals(0, (short) test.convert(" ", short.class));
		assertEquals(0, (int) test.convert(" ", int.class));
		assertEquals(0L, (long) test.convert(" ", long.class));
		assertEquals(0.0F, (float) test.convert(" ", float.class));
		assertEquals(0.0, (double) test.convert(" ", double.class));
	}
	
	/**
	 * Test a non ArooaValue can be converted to an ArooaValue.
	 * 
	 * @throws NoConversionAvailableException
	 */
	public void testToArooaValue() throws ArooaConversionException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ArooaConverter test = new DefaultConverter(registry);

		Object result = test.convert("Test", ArooaValue.class);
		
		assertTrue(result instanceof ArooaValue);
	}

	/**
	 * Convert fromArooaValue to an object.
	 * 
	 * @throws NoConversionAvailableException
	 */
	public void testFromArooaValueConvertObject() throws ArooaConversionException {
		class AV implements ArooaValue {
		}

		DefaultConverter test = new DefaultConverter(
				new ConversionLookup() {
					@SuppressWarnings("unchecked")
					public <F, T> ConversionPath<F, T> findConversion(
							Class<F> from, Class<T> to) {
						assertEquals(AV.class, from);
						assertEquals(String.class, to);
						ConversionPath<AV, AV> conversion = DefaultConversionPath.instance(AV.class);
						ConversionPath<AV, String> conversion2 = conversion.append(new ConversionStep<AV, String>() {
							public Class<AV> getFromClass() {
								return AV.class;
							}
							public Class<String> getToClass() {
								return String.class;
							}
							public String convert(AV from, ArooaConverter converter) throws ArooaConversionException {
								return "test";
							};
						});
						return (ConversionPath<F, T>) conversion2;
					}
				});

		String result = test.convert(new AV(), String.class);
		
		assertEquals("test", result);
	}
	
	/**
	 * Exactly the same test as above but not with real types. 
	 * 
	 * Sanity check when tracking down a bug - conversion from ValueType
	 * to Object is Object. (even though ValueType is an instance of Object)
	 * 
	 * @throws NoConversionAvailableException
	 */
	public void testFromArooaValueConvertObject2() throws ArooaConversionException {
		ValueType vt = new ValueType();
		vt.setValue(new ArooaObject("test"));
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ArooaConverter test = new DefaultConverter(registry);
		
		String result = (String) test.convert(vt, Object.class);
		
		assertEquals("test", result);
	}
	
	/**
	 * Check that a valueFor can convert null.
	 * 
	 * @throws NoConversionAvailableException
	 */
	public void testArooaValueConvertNull() throws ArooaConversionException {
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ArooaConverter test = new DefaultConverter(registry);

		class AV implements ArooaValue {
		}

		registry.register(AV.class, String.class, new Convertlet<AV, String>() {
			public String convert(AV from) throws ConvertletException {
				return null;
			};
		});

		Integer result = (Integer) test.convert(
				new AV(), Integer.class);
		
		assertNull(result);		
	}
	
	/**
	 * Test that implemenation of an ArooaValue is not converted when
	 * an ArooaValue is required.
	 * 
	 * @throws NoConversionAvailableException
	 */
	public void testFromArooaValueToArooaValue() throws ArooaConversionException {
		
		ValueType vt = new ValueType();
		
		DefaultConverter test = new DefaultConverter();
		
		Object result = test.convert(vt, ArooaValue.class);
		
		assertTrue(vt == result);
	}
	
	/**
	 * Test some everyday conversions.
	 * 
	 * @throws NoConversionAvailableException
	 */
	public void testSomeConversions() throws ArooaConversionException {
	
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
//		ConversionPath<Float, Double> path = registry.findConversion(Float.class, Double.class);
//		System.out.println(path);
		
		ArooaConverter converter = new DefaultConverter(registry);
		
		assertEquals(new Integer(1), converter.convert("1", Integer.class));
		assertEquals(new Double(42.24).doubleValue(), 
				((Double) converter.convert(new Float(42.24), Double.class)).doubleValue(), 0.0001);
	}
	
	
	public void testArooaValueArrayConvert() throws Exception {
		ValueType vt1 = new ValueType();
		vt1.setValue(new ArooaObject("a"));
		
		ValueType vt2 = new ValueType();
		vt2.setValue(new ArooaObject("b"));
		
		ValueType[] vta = { vt1, vt2 };
		
		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);
		
		ArooaConverter test = new DefaultConverter(registry);
		
		String[] result = (String[]) test.convert(vta, String[].class);
		
		assertEquals("a", result[0]);
		assertEquals("b", result[1]);
	}
}
