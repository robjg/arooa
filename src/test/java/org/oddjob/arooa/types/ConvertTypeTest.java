package org.oddjob.arooa.types;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.standard.StandardArooaSession;

public class ConvertTypeTest extends Assert {

   @Test
	public void testConvertStringToInteger() throws ArooaConversionException {
		
		ConvertType test = new ConvertType();
		test.setArooaSession(new StandardArooaSession());
		test.setValue(new ArooaObject("42"));
		test.setTo(Integer.class);
				
		assertEquals(Integer.valueOf(42), test.convert());
	}
	
	/**
	 * How ForEach would see convert.
	 *
     */
   @Test
	public void testConvertStringToObjectArray() throws ArooaConversionException {
		
		ArooaSession session = new StandardArooaSession();
		
		ConvertType test = new ConvertType();
		test.setArooaSession(session);
		test.setValue(new ArooaObject("a, b, c"));
		test.setTo(String[].class);
				
		ArooaConverter converter = session.getTools().getArooaConverter();

		Object[] result = converter.convert(test, Object[].class);

		assertEquals("a", result[0]);
		assertEquals("b", result[1]);
		assertEquals("c", result[2]);
		
		assertEquals(3, result.length);
	}
}
