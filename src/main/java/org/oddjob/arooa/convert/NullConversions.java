package org.oddjob.arooa.convert;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper to provide null conversions.
 * 
 * @author rob
 *
 */
public class NullConversions {

	private static final Map<Type, Object> NULL_CONVERSIONS =
			new HashMap<>(8);
	
	static {
		NULL_CONVERSIONS.put(boolean.class, false);
		NULL_CONVERSIONS.put(byte.class, (byte) 0);
		NULL_CONVERSIONS.put(char.class, '\0');
		NULL_CONVERSIONS.put(short.class, (short) 0);
		NULL_CONVERSIONS.put(int.class, 0);
		NULL_CONVERSIONS.put(long.class, 0L);
		NULL_CONVERSIONS.put(float.class, 0.0F);
		NULL_CONVERSIONS.put(double.class, 0.0);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T nullConversionFor(Type required) {
		return (T) NULL_CONVERSIONS.get(required);
	}
}
