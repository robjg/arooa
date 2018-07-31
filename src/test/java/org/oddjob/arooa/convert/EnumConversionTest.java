package org.oddjob.arooa.convert;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Assert;

/**
 * Possible options for Enum conversion.
 * 
 * @author rob
 *
 */
public class EnumConversionTest extends Assert {

	enum Colours {
		RED,
		BLUE,
		GREEN
	}
	
   @Test
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
	
   @Test
	public void testStringToEnumConversion2() {
	
		Class<Colours> enumClass = Colours.class;
		
		Colours colour = (Colours) Enum.valueOf(enumClass, "RED");
		
		assertEquals(Colours.RED, colour);
	}
}
