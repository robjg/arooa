package org.oddjob.arooa.parsing;

import org.junit.Test;
import org.oddjob.ArooaTestHelper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ArooaElementTest {

    @Test
    public void testEquals1() {

        ArooaElement a1 = new ArooaElement("a");
        ArooaElement a2 = new ArooaElement("a");

        assertThat(a1, is(a2));

        assertThat(a1.hashCode(), is(a2.hashCode()));
    }

    @Test
    public void testEquals2() throws URISyntaxException {

        ArooaElement a1 = new ArooaElement(
                new URI("urn:test:test"), "a");
        ArooaElement a2 = new ArooaElement(
                new URI("urn:test:test"),
                "a");

        assertThat(a1, is(a2));

        assertThat(a1.hashCode(), is(a2.hashCode()));
    }

    @Test
    public void testEquals3() throws URISyntaxException {

        ArooaElement a1 = new ArooaElement(
                new URI("urn:test:test"), "a");
        ArooaElement a2 = new ArooaElement(
                new URI("urn:test:test2"),
                "a");

        assertThat(a1, not(a2));
    }

    @Test
    public void testEquals4() throws URISyntaxException {

        ArooaElement a1 = new ArooaElement(
                new URI("urn:test:test"), "a");
        ArooaElement a2 = new ArooaElement(
                "a");

        assertThat(a1, not(a2));
    }

    @Test
    public void testEquals5() throws URISyntaxException {

        ArooaElement a1 = new ArooaElement("a");
        ArooaElement a2 = new ArooaElement("b");

        assertThat(a1, not(a2));
    }

    @Test
    public void testSerialize() throws URISyntaxException, IOException, ClassNotFoundException {

        ArooaElement test = new ArooaElement(
                new URL("http://rgordon.co.uk/oddjob/test").toURI(), "foo");

        ArooaElement copy = ArooaTestHelper.copy(test);

        assertThat(test, is(copy));
    }

    @Test
    public void testToString() throws MalformedURLException, URISyntaxException {

        ArooaElement test = new ArooaElement(
                new URL("http://rgordon.co.uk/oddjob/test").toURI(), "foo");

        assertThat(test.toString(), is("http://rgordon.co.uk/oddjob/test:foo"));
    }

    public void testToQTag() throws URISyntaxException {

        ArooaElement test = new ArooaElement(
                new URI("http://rgordon.co.uk/oddjob/test"), "foo");

        UriMapping mapping = uri -> "stuff";

        QTag result = mapping.getQName(test);

        assertThat(result.getTag(), is("foo"));
        assertThat(result.getPrefix(), is("stuff"));
        assertThat(result.getElement(), is(test));
        assertThat(result.toString(), is("stuff:foo"));
    }
}
