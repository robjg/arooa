package org.oddjob.arooa.forms;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.parsing.NamespaceMappings;

import java.net.URI;
import java.net.URISyntaxException;

public interface FormsLookup {

    static NamespaceMappings formsNamespaces() {

        final URI formsUri;
        try {
            formsUri = new URI("arooa:forms");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        final String formsPrefix = "forms";

        return new NamespaceMappings() {
            @Override
            public String[] getPrefixes() {
                return new String[] { formsPrefix };
            }

            @Override
            public URI getUriFor(String prefix) {
                return formsPrefix.equals(prefix) ? formsUri : null;
            }

            @Override
            public String getPrefixFor(URI uri) {
                return formsUri.equals(uri) ? formsPrefix : null;
            }
        };
    }

    ArooaConfiguration formFor(Object component);

    ArooaConfiguration blankForm(ArooaType arooaType, String elementTag, String propertyClass);


}
