package org.oddjob.arooa.types;

import org.junit.Test;

import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ConversionFailedException;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.convert.NoConversionAvailableException;

import org.junit.Assert;

public class ValueFactoryTest extends Assert {

	class LongFactory implements ValueFactory<Long> {
		
		@Override
		public Long toValue() throws ArooaConversionException {
			return new Long(42);
		}
	}
	
	
   @Test
	public void testOnwardConversion() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConverter converter = new DefaultConverter();
		
		Double result = converter.convert(new LongFactory(), Double.class);
		
		assertEquals(42.0, result, 0.01);
	}
	
}
