package org.oddjob.arooa.parsing;

import java.net.URI;

/**
 * Provides a mapping from the name space URI to the prefix.
 */
@FunctionalInterface
public interface UriMapping {

    String getPrefixFor(URI uri);

    /**
     * Use these mappings to find the prefix for a {@link QTag}.
     *
     * @param element An element
     * @return A qualified tag. never null.
     */
    default QTag getQName(ArooaElement element) {
        if (element.getUri() == null) {
            return new QTag(element.getTag());
        }

        String prefix = getPrefixFor(element.getUri());

        if (prefix == null) {
            throw new NullPointerException("No prefix for " + element.getUri());
        }

        // default NS.
        if ("".equals(prefix)) {
            return new QTag(element.getTag());
        }

        return new QTag(prefix, element);
    }

}
