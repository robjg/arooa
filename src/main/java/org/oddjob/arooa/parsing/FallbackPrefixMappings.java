package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Provide Prefix Mappings from a {@link NamespaceMappings} such as an {@link ArooaDescriptor}, but
 * also fallback to Mappings remembered from Parsing.
 * This is a bit of a bodge because unknown prefixes need be remembered for the
 * {@link org.oddjob.arooa.design.etc.UnknownInstance} to work. {@code UnknownInstance} needs to change
 * to cope with invalid XML.
 */
public class FallbackPrefixMappings implements PrefixMappings {

    private static final Logger logger = LoggerFactory.getLogger(FallbackPrefixMappings.class);

    private final NamespaceMappings namespaceMappings;

    private final PrefixMappings fallbackMappings = new SimplePrefixMappings();

    public FallbackPrefixMappings(NamespaceMappings namespaceMappings) {
        this.namespaceMappings = namespaceMappings;
    }

    @Override
    public void add(NamespaceMappings otherMappings) throws DuplicateMappingsException {
        for (String prefix : otherMappings.getPrefixes()) {
            put(prefix, otherMappings.getUriFor(prefix));
        }
    }

    @Override
    public void put(String prefix, URI uri) throws DuplicateMappingsException {
        URI already = Optional.ofNullable(namespaceMappings)
                .map(d -> d.getUriFor(prefix))
                .orElse(null);
        if (already == null) {
            logger.warn("Unknown namespace prefix [" + prefix + "], uri [" + uri + "]");
            fallbackMappings.put(prefix, uri);
        } else if (!already.equals(uri)) {
            throw new DuplicateMappingsException("Prefix already mapped to [" + already + "] not [" + uri + "]");
        }
    }

    @Override
    public String[] getPrefixes() {
        return Stream.<Supplier<String[]>>of(
                () -> Optional.ofNullable(namespaceMappings)
                        .map(NamespaceMappings::getPrefixes)
                        .orElse(new String[0]),
                fallbackMappings::getPrefixes)
                .flatMap(supplier -> Arrays.stream(supplier.get()))
                .toArray(String[]::new);
    }

    @Override
    public URI getUriFor(String prefix) {
        return Optional.ofNullable(namespaceMappings)
                .map(d -> d.getUriFor(prefix))
                .orElseGet(() -> fallbackMappings.getUriFor(prefix));
    }

    @Override
    public String getPrefixFor(URI uri) {
        return Optional.ofNullable(namespaceMappings)
                .map(d -> d.getPrefixFor(uri))
                .orElseGet(() -> fallbackMappings.getPrefixFor(uri));
    }
}
