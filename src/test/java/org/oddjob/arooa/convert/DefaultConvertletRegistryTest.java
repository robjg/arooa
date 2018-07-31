/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;

import org.junit.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.convertlets.ArooaValueConvertlets;
import org.oddjob.arooa.convert.convertlets.FileConvertlets;
import org.oddjob.arooa.convert.convertlets.URLConvertlets;

public class DefaultConvertletRegistryTest extends Assert {
	private static final Logger logger = LoggerFactory.getLogger(DefaultConvertletRegistryTest.class);
	
	/**
	 * Test that the order of registration is used when finding
	 * conversions.
	 * <p>
	 * Even though java.util.Date is a better match, java.sql.Date 
	 * was registered first so is chosen for the conversion first.
	 */
   @Test
	public void testRegistrationOrder() {
		class CP implements ConversionProvider {
			public void registerWith(ConversionRegistry registry) {
				registry.register(Long.class, java.sql.Date.class, 
						new Convertlet<Long, java.sql.Date>() {
					public java.sql.Date convert(Long from) { return null; }
				});
				registry.register(Long.class, java.util.Date.class, 
						new Convertlet<Long, java.util.Date>() {
					public java.util.Date convert(Long from) { return null; }
				});
			}
		}
		
		DefaultConversionRegistry test = new DefaultConversionRegistry();
		new CP().registerWith(test);
		
		ConversionPath<Long, java.util.Date> result = 
				test.findConversion(Long.class, java.util.Date.class);

		assertEquals(1, result.length());
		
		assertEquals(Long.class, result.getFromClass());
		assertEquals(java.sql.Date.class, result.getToClass());
		
	}
	
   @Test
	public void testShortestWins() {
		
		// this allows us to go Long -> String > Date or Long -> Date.
		// Despite the fact that Long -> String and String -> Date are
		// the first registerd. Long -> Date should win.
		class CP implements ConversionProvider {
			public void registerWith(ConversionRegistry registry) {
				registry.register(Long.class, String.class, 
						new Convertlet<Long, String>() {
					public String convert(Long from) { 
						return null; 
					}
				});
				registry.register(String.class, java.util.Date.class, 
						new Convertlet<String, java.util.Date>() {
					public java.util.Date convert(String from) { 
						return null; 
					}
				});
				registry.register(Long.class, java.util.Date.class, 
						new Convertlet<Long, java.util.Date>() {
					public java.util.Date convert(Long from) { 
						return null; 
					}
				});
			}
		}
		
		DefaultConversionRegistry test = new DefaultConversionRegistry();
		new CP().registerWith(test);
		
		ConversionPath<Long, java.util.Date> result = 
				test.findConversion(Long.class, java.util.Date.class);

		assertEquals(1, result.length());
		
		assertEquals(Long.class, result.getFromClass());
		assertEquals(java.util.Date.class, result.getToClass());
		
	}
	
   @Test
	public void testDoubleToLong() {
		DefaultConversionRegistry test = new DefaultConversionRegistry();
		
		new DefaultConversionProvider().registerWith(test);
		
		ConversionPath<?, ?> result = test.findConversion(Double.TYPE, Long.TYPE);

		// No primatives supported.
		assertNull(result);
	}

   @Test
	public void testFloatToDouble() {
		DefaultConversionRegistry test = new DefaultConversionRegistry();
		
		new DefaultConversionProvider().registerWith(test);
		
		ConversionPath<Float, Double> result = 
			test.findConversion(Float.class, Double.class);

		assertEquals(2, result.length());
		
		assertEquals(Float.class, result.getStep(0).getFromClass());
		assertEquals(Number.class, result.getStep(0).getToClass());
		
		assertEquals(Number.class, result.getStep(1).getFromClass());
		assertEquals(Double.class, result.getStep(1).getToClass());

	}
	
	/**
	 * Test that if something is already an instance of
	 * something else the registry realises this and does
	 * not attempt to find a path.
	 *
	 */
   @Test
	public void testAlreadyInstance() {
		ConversionLookup test = new DefaultConversionRegistry();
		
		// sql.Date is already an instanceof util.data
		ConversionPath<?, ?> result = 
				test.findConversion(java.sql.Date.class, java.util.Date.class);
		assertEquals(0, result.length());
		
		// but the converse is not true.
		result = test.findConversion(java.util.Date.class, java.sql.Date.class);
		assertNull(result);
		
		// another obvious example.
		result = test.findConversion(String.class, Object.class);
		assertEquals(0, result.length());
	}		
	
   @Test
	public void testExtendsAndImplements() {
		Class<?>[] results = DefaultConversionRegistry.extendsAndImplements(URL.class);
		
		assertEquals(2, results.length);
		
		assertEquals(Object.class, results[0]);
		assertEquals(Serializable.class, results[1]);
	}
	
	/**
	 * Test that a classes subclasses are also used to search
	 * for a conversion.
	 *
	 */
   @Test
	public void testSubClassLookup() {
		DefaultConversionRegistry test = new DefaultConversionRegistry();

		new ArooaValueConvertlets().registerWith(test);

		ConversionPath<?, ?> result = test.findConversion(String.class, ArooaValue.class);
		assertEquals(2, result.length());
		
	}
	
	class ConversionTracker extends DefaultConversionRegistry {
		int index = 0;
		Class<?> [] froms = { 
				String.class,
					File.class, 
						InputStream.class,
					URL.class, 
					Object.class,
					Serializable.class,
					Comparable.class,
					CharSequence.class
					};
		int[] levels = {
				0, 
					-1,
						-2,
					1,
					1,
					1,
					1,
					1
				};
		int[] conversions = {
				0,
					1,
						2,
					1,
					1,
					1,
					1,
					1
				};
		
		<F, X, Y, T> ConversionPath<F, T> best(Class<X> from, Class<T> to, 
				ConversionPath<F, X> stepsSoFar, int maxLevels) {
			if (index >= froms.length) {
				fail(from.getName() + " to " + to.getName() + " unexpected.");
			}
			logger.debug(from.getName() + " to " + to.getName());
			
			assertEquals("classes index " + index, froms[index], from);
			assertEquals("levels index " + index, levels[index], maxLevels);
			assertEquals("conversions index " + index, conversions[index], stepsSoFar.length());
			
			++index;
			
			return super.best(from, from, to, stepsSoFar, maxLevels);
		}
	}

	private String pathToString(ConversionPath<?, ?> result) {
		StringBuilder build = new StringBuilder();
		
		Class<?> previousTo = null;
		for (int i = 0; i < result.length(); ++i) {
			Class<?> from = result.getStep(i).getFromClass();
			Class<?> to = result.getStep(i).getToClass();
			
			if (i == 0) {
				build.append(from.getSimpleName());
			}
			else {
				assertEquals(previousTo, from);
			}
			
			previousTo = to;
			
			build.append(' ');
			build.append(to.getSimpleName());
		}
		
		return build.toString();
	}
	
	private String classesToString(Class<?>... classes) {
		StringBuilder build = new StringBuilder();
		
		for (Class<?> next : classes) {
			if (build.length() > 0 ) {
				build.append(' ');
			}
			
			build.append(next.getSimpleName());
		}
		
		return build.toString();
	}
	
	/**
	 * Test order of best conversion search.
	 *
	 */
   @Test
	public void testOrder() {
		DefaultConversionRegistry test1 = new DefaultConversionRegistry();

		new FileConvertlets().registerWith(test1);
		new URLConvertlets().registerWith(test1);
				
		ConversionPath<?, ?> result = test1.findConversion(String.class, InputStream.class);

		assertEquals(
				classesToString(String.class, File.class, InputStream.class),
				pathToString(result));
		
		DefaultConversionRegistry test2 = new DefaultConversionRegistry();

		new URLConvertlets().registerWith(test2);
		new FileConvertlets().registerWith(test2);
				
		ConversionPath<?, ?> result2 = test2.findConversion(String.class, InputStream.class);

		assertEquals(
				classesToString(String.class, URL.class, InputStream.class),
				pathToString(result2));
	}

	class ConversionTracker2 extends DefaultConversionRegistry {
		int index = 0;
		Class<?> [] froms = { 
				RedApple.class,
					Object.class,
					Fruit.class,
						Colour.class,
				};
		int[] levels = {
				0, 
					-1, 
					-1, 
						-2,
				};
		int[] conversions = {
				0,
					1, 
					1, 
					2,
					};
		
		<F, X, Y, T> ConversionPath<F, T> best(Class<X> from, Class<T> to, 
				ConversionPath<F, X> stepsSoFar, int maxLevels) {
			
			if (index >= froms.length) {
				fail(from.getName() + " to " + to.getName() + " unexpected.");
			}
			logger.debug(from.getName() + " to " + to.getName());
			
			assertEquals("classes index " + index, froms[index], from);
			assertEquals("levels index " + index, levels[index], maxLevels);
			assertEquals("conversions index " + index, conversions[index], stepsSoFar.length());
			
			++index;
			
			ConversionPath<F, T> result = super.best(
					from, from, to, stepsSoFar, maxLevels);
			
			return result;
		}
	}

	public static interface Fruit {
		String getColour();
	}
	
	public static class RedApple implements Fruit {
		public String getColour() {
			return "red";
		}
	}
	
	public static class Colour {
		public Colour(String colour) {}
	}
	
	
	/**
	 * Test the conversion Order where the RedApple will have to 
	 * be converted to a fruit before a match can be found.
	 *
	 */
   @Test
	public void testOrderWithSuper() {
		ConversionTracker2 test = new ConversionTracker2();

		test.register(Fruit.class, Colour.class, 
				new Convertlet<Fruit, Colour>() {
			public Colour convert(Fruit from) throws ConvertletException {
				return new Colour(((Fruit) from).getColour());
			}
		});
				
		ConversionPath<?, ?> result = 
				test.findConversion(RedApple.class, Colour.class);
		
		assertEquals(2, result.length());
	}

	// Diagnotstic - more use for understanding than
	// as a test.
	
	class Diagnose extends DefaultConversionRegistry {
		int index = 0;

		int level = 0;
		
		<F, X, Y, T> ConversionPath<F, T> best(Class<X> from, Class<T> to, 
				ConversionPath<F, X> stepsSoFar, int maxLevels) {

			for (int i = 0; i < level; ++ i) {
				System.out.print('\t');
			}
			System.out.println("From: " + from.getName() + ", To: " + to.getName() +
					", stepsSoFar: " + stepsSoFar.length() + ", " + maxLevels);


			++index;
			++level;

			ConversionPath<F, T> result = super.best(
					from, from, to, stepsSoFar, maxLevels);

			--level;

			return result;
		}
	}
	
   @Test
	public void testDiagnose() {
		
		Diagnose diagnose = new Diagnose();
		
		new FileConvertlets().registerWith(diagnose);
		new URLConvertlets().registerWith(diagnose);
		new ArooaValueConvertlets().registerWith(diagnose);
		
		Class<String> from = String.class;
		ConversionPath<?, ?> result = diagnose.findConversion(from, ArooaValue.class);
		System.out.print("Result: " + from.getName());
		for (int i = 0; i < result.length(); ++i) {
			System.out.print("->" + result.getStep(i).getToClass().getName());
		}
		System.out.println();
	}
	
}
