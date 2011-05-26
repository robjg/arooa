package org.oddjob.arooa.convert;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaValue;

/**
 * Test DefaultConvertletRegistry with ArooaValues.
 * 
 * @author rob
 *
 */
public class DefaultConvertletAVTest extends TestCase {

	class Apples {
		
	}
	
	class OurArooaValue implements ArooaValue {
		
		public Apples toApples() {
			return new Apples();
		}
	}
	
	class OurConvertlet implements Convertlet<OurArooaValue, Apples> {
		
		public Apples convert(OurArooaValue from) throws ConvertletException {
			return from.toApples();
		}
	}
	
	public void testToObject() throws NoConversionAvailableException, ConversionFailedException {
				
		DefaultConversionRegistry test = new DefaultConversionRegistry();
		
		test.register(OurArooaValue.class, Apples.class, new OurConvertlet());
		
		DefaultConverter converter = new DefaultConverter(test);
		
		Object result = converter.convert(
				new OurArooaValue(), Object.class);
		
		assertEquals(Apples.class, result.getClass());
	}
	
	class DaddyArooaValue implements ArooaValue {
		
		public OurArooaValue toValue() {
			return new OurArooaValue();
		}
	}

	class DaddyConvertlet implements Convertlet<DaddyArooaValue, OurArooaValue> {
		
		public OurArooaValue convert(DaddyArooaValue from) throws ConvertletException {
			return from.toValue();
		}
	}
	
	/**
	 * Test ArooaValue.
	 * 
	 * @throws NoConversionAvailableException
	 * @throws ConversionFailedException
	 */
	public void testToArooaValue() throws NoConversionAvailableException, ConversionFailedException {
		
		DefaultConversionRegistry test = new DefaultConversionRegistry();
		
		test.register(OurArooaValue.class, Apples.class, new OurConvertlet());
		test.register(DaddyArooaValue.class, OurArooaValue.class, new DaddyConvertlet());
		
		DefaultConverter converter = new DefaultConverter(test);
		
		Object result = converter.convert(
				new DaddyArooaValue(), Object.class);
		
		assertEquals(Apples.class, result.getClass());
	}
	
}
