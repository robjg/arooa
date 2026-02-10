package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.convert.ArooaConversionException;


/**
 * An interface for simple {@link ArooaValue}s that only resolve to a
 * thing of a single type. 
 * <p>
 * Implementing this interface saves the new Type the trouble of needing 
 * to register a conversion.
 * 
 * @author rob
 *
 * @param <T> The type that the value will resolve to.
 */
public interface ValueFactory<T> extends ArooaValue {

	/**
	 * Provide an instance of the type this is a factory for.
	 * 
	 * @return An instance of the type. May be null.
	 * 
	 * @throws ArooaConversionException If unable to provide a value.
	 */
	T toValue() throws ArooaConversionException;
	
}
