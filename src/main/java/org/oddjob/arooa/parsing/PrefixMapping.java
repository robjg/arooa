package org.oddjob.arooa.parsing;

import java.net.URI;

/**
 * Provides a mapping from a namespace prefix to the URI.
 */
@FunctionalInterface
public interface PrefixMapping {

    URI getUriFor(String prefix);

    /**
     * Use this mappings to find the uri for an {@link QTag}.
     *
     * @param qName A qualified name.
     * @return An element. Never null.
     */
    default QTag qTagFor(String qName) {

        int colonPos = qName.indexOf(':');
        if (colonPos < 0) {
            return new QTag("", new ArooaElement(qName));
        }
        else {
            String prefix = qName.substring(0, colonPos);
            URI uri = getUriFor(prefix);
            if (uri == null) {
                throw new IllegalArgumentException("No URI for prefix " + prefix);
            }
            return new QTag(prefix, new ArooaElement(uri, qName.substring(colonPos + 1)));
        }
    }

    /**
     * Use this mappings to find the uri for an {@link ArooaElement}.
     *
     * @param qName A qualified name.
     * @return An element. Never null.
     */
    default ArooaElement elementFor(String qName) {

        return qTagFor(qName).getElement();
    }
}
