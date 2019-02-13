package org.oddjob.arooa.parsing;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.utils.ListSetterHelper;
import org.oddjob.arooa.xml.XMLConfiguration;

public class CutAndPasteSupport3Test extends Assert {

    public static class Component {

        List<Component> children = new ArrayList<>();

        @ArooaComponent
        public void setChild(int index, Component component) {
            new ListSetterHelper<>(children).set(index, component);
        }

        @Override
        public String toString() {
            return "OurComp";
        }
    }

    /**
     * Tracking down a bug that obfuscated the real exception.
     *
     * @throws ArooaParseException
     */
    @Test
    public void testPastingSameId() throws ArooaParseException {
        Component root = new Component();

        String middleBit =
                "<bean class='" + Component.class.getName() + "' id='a'>" +
                        " <child>" +
                        "  <bean class='" + Component.class.getName() + "' id='a'/>" +
                        " </child>" +
                        "</bean>";

        String xml =
                "<component/>";

        StandardArooaParser parser = new StandardArooaParser(root);

        ConfigurationHandle handle = parser.parse(new XMLConfiguration("XML", xml));

        CutAndPasteSupport test = new CutAndPasteSupport(handle.getDocumentContext());

        test.paste(0, new XMLConfiguration("Paste", middleBit));

        ArooaSession session = parser.getSession();

        assertNotNull(session.getBeanRegistry().lookup("a"));
        assertNotNull(session.getBeanRegistry().lookup("a2"));

        assertNotNull(session.getComponentPool().trinityForId("a"));
        assertNotNull(session.getComponentPool().trinityForId("a2"));

        handle.getDocumentContext().getRuntime().destroy();

        assertNull(session.getBeanRegistry().lookup("a"));
        assertNull(session.getBeanRegistry().lookup("a2"));

        assertNull(session.getComponentPool().trinityForId("a"));
        assertNull(session.getComponentPool().trinityForId("a2"));
    }

    @Test
    public void whenPasteComponentWithIdFailsThenComponentNotRegistered() throws ArooaParseException {

        Component root = new Component();

        String middleBit =
                "<bean class='" + Component.class.getName() + "' id='a'>" +
                        " <child>" +
                        "  <bean class='Idontexist'/>" +
                        " </child>" +
                        "</bean>";

        String xml =
                "<component/>";

        StandardArooaParser parser = new StandardArooaParser(root);

        ConfigurationHandle handle = parser.parse(new XMLConfiguration("XML", xml));

        CutAndPasteSupport test = new CutAndPasteSupport(handle.getDocumentContext());

        try {
            test.paste(0, new XMLConfiguration("Paste", middleBit));
            fail("Should fail.");
        }
        catch (ArooaParseException e) {
            // expected
        }

        ArooaSession session = parser.getSession();

        assertNull(session.getBeanRegistry().lookup("a"));

        handle.getDocumentContext().getRuntime().destroy();

    }
}
