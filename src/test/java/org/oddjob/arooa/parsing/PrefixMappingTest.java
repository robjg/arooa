package org.oddjob.arooa.parsing;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PrefixMappingTest {

    @Test
    public void tesElementForNoPrefix() {

        PrefixMapping prefixMapping = prefix -> null;

        ArooaElement result = prefixMapping.elementFor("apple");

        assertThat(result, is(new ArooaElement("apple")));

    }

    @Test
    public void testElementForWithPrefix() throws URISyntaxException {

        URI uri = new URI("arooa:foo");

        PrefixMapping prefixMapping = prefix -> "fruit".equals(prefix) ? uri : null;

        ArooaElement result = prefixMapping.elementFor("fruit:apple");

        assertThat(result, is(new ArooaElement(uri, "apple")));
    }

    @Test
    public void testElementForNoMapping() {

        PrefixMapping prefixMapping = prefix -> null;

        try {
            prefixMapping.elementFor("fruit:apple");
        }
        catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), Matchers.containsString("fruit"));
        }

    }
}