package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.convert.ArooaConverter;
import org.oddjob.arooa.standard.StandardArooaSession;

import junit.framework.TestCase;

public class ConvertTypeTest extends TestCase {

	public void testConvertStringToInteger() throws ArooaConversionException {
		
		ConvertType<Integer> test = new ConvertType<Integer>();
		test.setArooaSession(new StandardArooaSession());
		test.setValue(new ArooaObject("42"));
		test.setTo(Integer.class);
				
		assertEquals(new Integer(42), test.convert());
	}
	
	/**
	 * How ForEach would see convert.
	 * 
	 * @throws ArooaConversionException
	 */
	public void testConvertStringToObjectArray() throws ArooaConversionException {
		
		ArooaSession session = new StandardArooaSession();
		
		ConvertType<String[]> test = new ConvertType<String[]>();
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
