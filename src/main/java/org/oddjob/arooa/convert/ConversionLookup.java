/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;

import java.lang.reflect.Type;

/**
 * Something that finds a {@link ConversionPath} between two classes.
 * 
 * @author Rob Gordon.
 *
 */
public interface ConversionLookup {

	/**
	 * Attempt to find a ConversionPath.
	 * 
	 * @param from The convert from class.
	 * @param to The convert to class.
	 * 
	 * @return The ConversionPath or null if one can't be found.
	 */
    <F, T> ConversionPath<F, T> findConversion(Type from, Type to);
}
