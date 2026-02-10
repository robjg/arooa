package org.oddjob.arooa.convert.doc;

import java.lang.reflect.Type;

/**
 * Provides a conversion item for a conversion. Intended for documentation. This reduces coupling
 * between the type used for documentation and the strategy for managing it.
 *
 * @param <I> The type of Item.
 *
 * @see ItemAccessStrategy
 */
public interface ConversionItemProvider<I> {

    I create(Type from, Type to, TypeIdentifier typeIdentifier);

}
