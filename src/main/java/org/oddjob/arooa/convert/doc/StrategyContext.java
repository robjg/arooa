package org.oddjob.arooa.convert.doc;

import java.util.function.Supplier;

/**
 * Provides a way managing a collection of {@link ConversionItemAccess} objects created by
 * various {@link ItemAccessStrategy} implementations.
 *
 * @param <I> The type of item being managed.
 */
public interface StrategyContext<I> {

    <C extends ConversionItemAccess<I>> C supplyIfAbsent(Object key,
                                                         Supplier<C> supplier);

}
