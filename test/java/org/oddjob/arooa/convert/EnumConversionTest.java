package org.oddjob.arooa.convert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;

/**
 * Possible options for Enum conversion.
 * 
 * @author rob
 *
 */
public class EnumConversionTest extends TestCase {

	enum Colours {
		RED,
		BLUE,
		GREEN
	}
	
	public void testStringToEnumConversion() 
	throws 
			IllegalArgumentException, 
			IllegalAccessException, 
			InvocationTargetException, 
			SecurityException, 
			NoSuchMethodException {
		
		String red = "RED";
		
// 		What's wrong with this????
//		Class<Enum<? extends Enum<?>>> enumClass = Colours.class;
		
		Class<?> enumClass = Colours.class;
		
		Method m = enumClass.getMethod(
				"valueOf", 
				new Class<?>[] { String.class });

		Colours colour = (Colours) m.invoke(null, red);
		
		assertEquals(Colours.RED, colour);
	}
	
	public void testStringToEnumConversion2() {
	
		Class<Colours> enumClass = Colours.class;
		
		Colours colour = (Colours) Enum.valueOf(enumClass, "RED");
		
		assertEquals(Colours.RED, colour);
	}
}
