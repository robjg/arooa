package org.oddjob.arooa.convert.doc;

import java.lang.reflect.Method;

/**
 * Wraps a Class or Method as something that provides documentation for a conversion.
 *
 * @see org.oddjob.arooa.convert.doc.StandardItemAccess
 */
public interface ElementIdentifier {

    static TypeIdentifier ofClass(Class<?> type) {
        return TypeIdentifier.ofClass(type);
    }

    static MethodIdentifier ofMethod(Method method) {
        return MethodIdentifier.ofMethod(method);
    }

    TypeIdentifier getTypeIdentifier();

    /**
     * Provide human-readable name such that could be used in a documentations index or title.
     *
     * @return A name for the identifier. Never null.
     */
    String getName();

}
