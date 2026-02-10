package org.oddjob.arooa.convert.doc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Manages Documentation access for a conversion.
 *
 * @param <I> The type of item being managed. Initially Conversion Doc.
 */
public class ConversionOrganiser<I> implements StrategyContext<I>, ConversionItemAccess<I> {

    private final Map<Object, ConversionItemAccess<I>> containers = new HashMap<>();

    @Override
    public I getForType(TypeIdentifier typeIdentifier) {
        for (ConversionItemAccess<I> container: containers.values()) {
            I item = container.getForType(typeIdentifier);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Override
    public I getForMethod(MethodIdentifier methodIdentifier) {
        for (ConversionItemAccess<I> container: containers.values()) {
            I item = container.getForMethod(methodIdentifier);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    @Override
    public boolean containsForType(TypeIdentifier  typeIdentifier) {
        for (ConversionItemAccess<I> container: containers.values()) {
            boolean contains = container.containsForType(typeIdentifier);
            if (contains) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<I> getAll() {
        List<I> all = new ArrayList<>();
        for (ConversionItemAccess<I> container: containers.values()) {
            all.addAll(container.getAll());
        }

        return all;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends ConversionItemAccess<I>> C supplyIfAbsent(Object key, Supplier<C> supplier) {
        return (C) containers.computeIfAbsent(key, ignored -> supplier.get());
    }

}
