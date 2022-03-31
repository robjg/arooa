package org.oddjob.arooa.standard;

import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaElement;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.xml.XMLConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StandardRuntimeTest {

    public static class Component {

        String colour;

        @ArooaElement
        public void setColour(String colour) {
            this.colour = colour;
        }
    }

    @Test
    public void testManySave() throws ArooaParseException {

        Component root = new Component();

        StandardArooaParser parser = new StandardArooaParser(root);

        String xml =
                "<component>" +
                        "    <colour>" +
                        "        <value value='red'/>" +
                        "    </colour>" +
                        "</component>";

        ConfigurationHandle<ArooaContext> handle = parser.parse(
                new XMLConfiguration("TEST", xml));

        assertThat(root.colour, nullValue());

        handle.getDocumentContext().getRuntime().configure();

        assertThat(root.colour, is("red"));

        handle.getDocumentContext().getRuntime().destroy();

        // destroy no longer sets value property for null
        assertThat(root.colour, notNullValue());
    }
}
