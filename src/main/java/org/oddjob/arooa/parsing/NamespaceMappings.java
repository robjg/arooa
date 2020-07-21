package org.oddjob.arooa.parsing;

import java.net.URI;

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

    static NamespaceMappings empty() {
        return new NamespaceMappings() {
            @Override
            public String[] getPrefixes() {
                return new String[0];
            }

            @Override
            public URI getUriFor(String prefix) {
                return null;
            }

            @Override
            public String getPrefixFor(URI uri) {
                return null;
            }
        };
    }
}
