package org.oddjob.arooa.convert.convertlets;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.Convertlet;
import org.oddjob.arooa.convert.ConvertletException;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.MockConvertletRegistry;
import org.oddjob.arooa.convert.NoConversionAvailableException;

public class CollectionConvertletsTest extends TestCase {
	
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

	public void testCollection2ObjectArray() throws ConvertletException {
		OurConvertletRegistry reg = new OurConvertletRegistry();
		new CollectionConvertlets().registerWith(reg);
		
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
	
	public void testArray2List() throws ConvertletException {
		OurConvertletRegistry reg = new OurConvertletRegistry();
		new CollectionConvertlets().registerWith(reg);
		
		Convertlet test = reg.getConvertlet(1);
		
		String[] from = { "a", "b" };
		
		List<String> result = (List<String>) test.convert(from);
		
		assertEquals("a", result.get(0));
		assertEquals("b", result.get(1));
	}
	
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
	
}
