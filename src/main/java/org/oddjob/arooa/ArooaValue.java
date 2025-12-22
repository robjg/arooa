/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;


/**
 * A configuration value that provides some other value at runtime via a conversion.
 * An Arooa Value will not be converted by being assignable by the framework, and so
 * an appropriate conversion will always be used instead.
 * <p>
 * Conversions are provided with a {@link org.oddjob.arooa.convert.ConversionProvider} that will register an appropriate
 * {@link org.oddjob.arooa.convert.Convertlet} which will often be a method on the Arooa Value implementation.
 * an Arooa Value can provide multiple conversions. If they provide only one, then they can implement
 * {@link org.oddjob.arooa.types.ValueFactory} and conversion will be automatic.
 *
 * @see org.oddjob.arooa.types.ValueFactory
 * @see org.oddjob.arooa.types.ArooaObject
 *
 * @author Rob Gordon.
 */
public interface ArooaValue {
	
}
