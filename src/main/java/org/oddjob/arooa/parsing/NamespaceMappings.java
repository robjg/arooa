package org.oddjob.arooa.parsing;

/**
 * Provide mappings for XML namespaces.
 */
public interface NamespaceMappings extends PrefixMapping, UriMapping {

    /**
     * Get all the prefixes mapped.
     *
     * @return The prefixes. Maybe be empt but never null.
     */
    String[] getPrefixes();

}
