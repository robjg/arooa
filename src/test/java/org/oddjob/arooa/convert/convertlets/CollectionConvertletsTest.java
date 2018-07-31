package org.oddjob.arooa.convert.convertlets;

import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionPath;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.MockConvertletRegistry;

public class CollectionConvertletsTest extends Assert {
	
	class OurConvertletRegistry extends MockConvertletRegistry {
		List<Convertlet<?, ?>> convertlets = 
			new ArrayList<Convertlet<?, ?>>();
		
		public <F, T> void register(Class<F> from, Class<T> to, 
				Convertlet<F, T> convertlet) {
			convertlets.add(convertlet);
		}
		
		public <F> void registerJoker(Class<F> from, Joker<F> joker) {
			throw new RuntimeException("Unexpected");
		}
		
		Convertlet<?, ?> getConvertlet(int index) {
			return convertlets.get(index);
		}
	}

    @Test
	public void testCollection2ObjectArray() throws ArooaConversionException {
		OurConvertletRegistry reg = new OurConvertletRegistry();
		new CollectionConvertlets().registerWith(reg);
		
		@SuppressWarnings("unchecked")
		Convertlet<List<String>, Object[]> test = 
			(Convertlet<List<String>, Object[]>) reg.getConvertlet(0);

		List<String> from = new ArrayList<String>();
		from.add("a");
		from.add("b");
		
		Object[] result = (Object[]) test.convert(from);
		
		assertNotSame(String[].class, result.getClass());
		
		assertEquals("a", result[0]);
		assertEquals("b", result[1]);
	}
	
    @Test
	public void testArray2List() throws ArooaConversionException {
		OurConvertletRegistry reg = new OurConvertletRegistry();
		new CollectionConvertlets().registerWith(reg);
		
		@SuppressWarnings("unchecked")
		Convertlet<String[], List<String>> test = 
				(Convertlet<String[], List<String>>) reg.getConvertlet(1);
		
		String[] from = { "a", "b" };
		
		List<String> result = (List<String>) test.convert(from);
		
		assertEquals("a", result.get(0));
		assertEquals("b", result.get(1));
	}
	
    @Test
	public void testInStandardConverter() throws Exception {
		List<String> from = new ArrayList<String>();
		from.add("a");
		from.add("b");

		DefaultConversionRegistry reg = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(reg);
		
		Object[] result = (Object[]) new DefaultConverter(reg).convert(
				from, Object[].class);
		
		assertEquals("a", result[0]);
		assertEquals("b", result[1]);		
	}
	
    @Test
	public void testMapToIterableConversion() throws ConversionFailedException {
		
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("apples", new Integer(12));
		
		ArooaConverter converter = new DefaultConverter();
		
		@SuppressWarnings("rawtypes")
		ConversionPath<Map, Iterable> conversion = 
				converter.findConversion(Map.class, Iterable.class);
		
		assertNotNull(conversion);
		
		@SuppressWarnings("unchecked")
		Iterable<Map.Entry<String, Integer>> iterable = 
				(Iterable<Map.Entry<String, Integer>>) conversion.convert(map, converter);
		
		Iterator<Map.Entry<String, Integer>> it = iterable.iterator();

		assertTrue(it.hasNext());
	}
    
    @Test
    public void testIterableToStream() throws ConversionFailedException {
    	
    	
		ArooaConverter converter = new DefaultConverter();
		
		@SuppressWarnings("rawtypes")
		ConversionPath<Iterable, Stream> conversion = 
				converter.findConversion(Iterable.class, Stream.class);
		
		assertNotNull(conversion);

		Stream<?> result = conversion.convert(
				Arrays.asList("red", "blue", "green"), 
				converter);
		
		
		List<?> rList = result.collect(Collectors.toList());
		
		assertThat(rList, is( Arrays.asList("red", "blue", "green")));
    }
    
    @Test
    public void testStreamToIterable() throws ConversionFailedException {
    	
    	
		ArooaConverter converter = new DefaultConverter();
		
		@SuppressWarnings("rawtypes")
		ConversionPath<Stream, Iterable> conversion = 
				converter.findConversion(Stream.class, Iterable.class);
		
		assertNotNull(conversion);

		Iterable<?> result = conversion.convert(
				Stream.of("red", "blue", "green"), 
				converter);
		
		
		List<String> rList = new ArrayList<>();
		
		result.forEach(r -> rList.add((String) r));
		
		assertThat(rList, is( Arrays.asList("red", "blue", "green")));
    }
    
    @Test
    public void testStreamToList() throws ConversionFailedException {
    	
    	
		ArooaConverter converter = new DefaultConverter();
		
		@SuppressWarnings("rawtypes")
		ConversionPath<Stream, List> conversion = 
				converter.findConversion(Stream.class, List.class);
		
		assertNotNull(conversion);

		List<?> result = conversion.convert(
				Stream.of("red", "blue", "green"), 
				converter);
		
		
		assertThat(result, is( Arrays.asList("red", "blue", "green")));
    }
    
    @Test
    public void testStreamToSet() throws ConversionFailedException {
    	
    	
		ArooaConverter converter = new DefaultConverter();
		
		@SuppressWarnings("rawtypes")
		ConversionPath<Stream, Set> conversion = 
				converter.findConversion(Stream.class, Set.class);
		
		assertNotNull(conversion);

		Set<?> result = conversion.convert(
				Stream.of("red", "blue", "green"), 
				converter);
		
		
		assertThat(result, is( new HashSet<>(Arrays.asList("red", "blue", "green"))));
    }
}
