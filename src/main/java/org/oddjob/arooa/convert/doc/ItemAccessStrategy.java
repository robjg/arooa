package org.oddjob.arooa.convert.doc;

import org.oddjob.arooa.convert.ConversionRegistry;

/**
 * A strategy for accessing whatever is provided. Intended for documentation of conversions where
 * access depends on the type of conversion. The strategy uses the context to register an
 * {@link ConversionItemAccess} type which will handle access to what was created by the
 * {@link ConversionItemProvider}.
 */
public interface ItemAccessStrategy {

    <T> ConversionRegistry processIn(StrategyContext<T> context,
                                     ConversionItemProvider<T> factory);

}
