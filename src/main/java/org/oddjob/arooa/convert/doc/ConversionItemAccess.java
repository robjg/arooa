package org.oddjob.arooa.convert.doc;

import java.util.List;

/**
 * Something that manages access to an item. Intended for managing access to the documentation
 * of a conversion so a doclet can contribute documentation by type or method.
 *
 * @param <I> The type item.
 */
public interface ConversionItemAccess<I> {

    I getForType(String canonicalTypeName);

    I getForMethod(String canonicalTypeName, String methodName);

    boolean containsForType(String canonicalTypeName);

    List<I> getAll();
}
