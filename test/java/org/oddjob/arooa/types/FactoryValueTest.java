package org.oddjob.arooa.types;

import junit.framework.TestCase;

import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.ConversionLookup;
import org.oddjob.arooa.convert.ConversionStep;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.DefaultConversionProvider;
import org.oddjob.arooa.convert.DefaultConversionRegistry;
import org.oddjob.arooa.convert.Joker;
import org.oddjob.arooa.convert.NoConversionAvailableException;

public class FactoryValueTest extends TestCase {

	class MyFactory implements ValueFactory<String> {
		public String toValue() {
			return "apple";
		}
	}
	
	public void testMyFactory() throws NoConversionAvailableException, ConversionFailedException {
	
		ArooaConverter converter = new DefaultConverter();
		
		String string = converter.convert(new MyFactory(), String.class);
		
		assertEquals("apple", string);
	}
	
	public void testMaskedJokerConversion() throws NoConversionAvailableException, ConversionFailedException {

		DefaultConversionRegistry registry = new DefaultConversionRegistry();
		new DefaultConversionProvider().registerWith(registry);

		registry.registerJoker(MyFactory.class, new Joker<MyFactory>() {
			public <T> ConversionStep<MyFactory, T> lastStep(
					Class<? extends MyFactory> from, 
					Class<T> to, 
					ConversionLookup conversions) {
				return null;
			}
		});
				
		DefaultConverter converter = new DefaultConverter(registry);
		
		String string = converter.convert(new MyFactory(), String.class);
		
		assertEquals("apple", string);
	}
}
