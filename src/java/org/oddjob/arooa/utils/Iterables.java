package org.oddjob.arooa.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper for working with Iterables.
 * 
 * @author rob
 *
 */
public class Iterables {

	/**
	 * Convert an Iterable into a list.
	 * 
	 * @param iterable The iterable.
	 * @return The list.
	 */
	public static <T> List<T> toList(Iterable<T> iterable) {
		
		List<T> list = new ArrayList<T>();
		
		for (T t : iterable) {
			list.add(t);
		}
		
		return list;
	}
	
	/**
	 * Convert an Iterable into an array.
	 * 
	 * @param iterable The iterable.
	 * 
	 * @return An array.
	 */
	public static Object[] toArray(Iterable<?> iterable) {

		return toList(iterable).toArray();
	}
	
	/**
	 * Convert an Iterable into an array.
	 * 
	 * @param iterable The iterable.
	 * @param type The type of the array to create.
	 * 
	 * @return An array of the given type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] toArray(Iterable<T> iterable, Class<?> type) {

		List<T> list = toList(iterable);
				
		return list.toArray((T[]) Array.newInstance(type, list.size()));
	}
	
	/**
	 * Convert an Iterable to a String.
	 * 
	 * @param iterable The Iterable.
	 * @return The String.
	 */
	public static String toString(Iterable<?> iterable) {

		return Arrays.toString(toArray(iterable));
	}
	
}
