package org.oddjob.arooa.standard;

import org.junit.Test;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.xmlunit.matchers.CompareMatcher.isIdenticalTo;

public class StandardConfigurationNodeX2Test {


    public static class Component {

        List<Component> children = new ArrayList<Component>();

        String colour;

        @ArooaComponent
        public void setChild(int index, Component component) {
            if (component == null) {
                this.children.remove(index);
            } else {
                this.children.add(index, component);
            }
        }

        public void setColour(String colour) {
            this.colour = colour;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " " + colour;
        }
    }

    static final String EOL = System.getProperty("line.separator");

    @Test
    public void testManySave() throws ArooaParseException {

        Component root = new Component();

        StandardArooaParser parser = new StandardArooaParser(root);

        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
                "<component>" + EOL +
                "    <child>" + EOL +
                "        <bean class=\"" + Component.class.getName() + "\"" + EOL +
                "               colour=\"blue\"/>" + EOL +
                "    </child>" + EOL +
                "</component>" + EOL;

        parser.parse(
                new XMLConfiguration("TEST", xml));

        ArooaContext childContext = parser.getSession(
        ).getComponentPool().contextFor(root.children.get(0));

        XMLArooaParser xmlParser = new XMLArooaParser(parser.getSession().getArooaDescriptor());

        ConfigurationHandle<ArooaContext> handle = xmlParser.parse(
                childContext.getConfigurationNode());

        ArooaContext xmlDoc = handle.getDocumentContext();

        CutAndPasteSupport.replace(xmlDoc.getParent(), xmlDoc,
                                   new XMLConfiguration("Replace",
                                                        "        <bean class=\"" + Component.class.getName() + "\"" + EOL +
                                                                "               colour=\"green\"/>"));

        handle.save();

        assertEquals("green", root.children.get(0).colour);

        xmlDoc = handle.getDocumentContext();
        CutAndPasteSupport.replace(xmlDoc.getParent(), xmlDoc,
                                   new XMLConfiguration("Replace",
                                                        "        <bean class=\"" + Component.class.getName() + "\"" + EOL +
                                                                "               colour=\"yellow\"/>"));

        handle.save();

        assertEquals("yellow", root.children.get(0).colour);
    }

    @Test
    public void testBadSave() throws Exception {

        Component root = new Component();

        StandardArooaParser parser = new StandardArooaParser(root);

        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
                "<component>" + EOL +
                "    <child>" + EOL +
                "        <bean class=\"" + Component.class.getName() + "\"" + EOL +
                "               colour=\"blue\"/>" + EOL +
                "    </child>" + EOL +
                "</component>" + EOL;

        parser.parse(
                new XMLConfiguration("TEST", xml));

        ArooaContext childContext = parser.getSession(
        ).getComponentPool().contextFor(root.children.get(0));

        XMLArooaParser xmlParser = new XMLArooaParser(parser.getSession().getArooaDescriptor());

        ConfigurationHandle<ArooaContext> handle = xmlParser.parse(
                childContext.getConfigurationNode());

        ArooaContext xmlDoc = handle.getDocumentContext();

        CutAndPasteSupport.replace(xmlDoc.getParent(), xmlDoc,
                                   new XMLConfiguration("Replace", "<rubbish/>"));

        try {
            handle.save();
            fail("Should fail.");
        } catch (Exception e) {
            // expected.
        }

        XMLArooaParser xmlParser2 = new XMLArooaParser(parser.getSession().getArooaDescriptor());

        xmlParser2.parse(parser.getSession(
        ).getComponentPool().contextFor(root).getConfigurationNode());


        assertThat(xmlParser2.getXml(), isIdenticalTo(xml));

        // Test replaced node is still useable.

        xmlDoc = handle.getDocumentContext();
        CutAndPasteSupport.replace(xmlDoc.getParent(), xmlDoc,
                                   new XMLConfiguration("Replace",
                                                        "        <bean class=\"" + Component.class.getName() + "\"" + EOL +
                                                                "               colour=\"yellow\"/>"));

        handle.save();

        assertEquals("yellow", root.children.get(0).colour);

        // tracking down a bug where a failed save doesn't
        // restore itself correctly...
        ArooaContext newContext =
                parser.getSession().getComponentPool().contextFor(
                        root.children.get(0));

        assertNotNull(newContext);

        ArooaContext rootContext =
                parser.getSession().getComponentPool().contextFor(
                        root);

        rootContext.getRuntime().destroy();
    }

    @Test
    public void testReplaceWithBadChild() throws Exception {

        Component root = new Component();
        root.setColour("root");

        StandardArooaParser parser = new StandardArooaParser(root);

        String xml = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
                "<component>" + EOL +
                "    <child>" + EOL +
                "        <bean class=\"" + Component.class.getName() + "\"" + EOL +
                "               colour=\"green\"" + EOL +
                "               id=\"blue\"/>" + EOL +
                "    </child>" + EOL +
                "</component>" + EOL;

        ConfigurationHandle<ArooaContext> handle = parser.parse(
                new XMLConfiguration("TEST", xml));

        ArooaContext rootContext = parser.getSession()
                                         .getComponentPool()
                                         .contextFor(root);

        String replacementXml = "<?xml version='1.0' encoding='UTF-8' standalone='no'?>" + EOL +
                "<component>" + EOL +
                "    <child>" + EOL +
                "       <bean class=\"" + Component.class.getName() + "\"" +
                "              id='blue'/>" + EOL +
                "       <rubbish/>" +
                "    </child>" + EOL +
                "</component>" + EOL;

        CutAndPasteSupport.ReplaceResult replaceResult =
                CutAndPasteSupport.replace(
                        rootContext.getParent(),
                        rootContext,
                        new XMLConfiguration("Replace", replacementXml));

        assertNotNull(replaceResult.getException());

        XMLArooaParser xmlParser2 = new XMLArooaParser(parser.getSession().getArooaDescriptor());

        xmlParser2.parse(parser.getSession()
                               .getComponentPool()
                               .contextFor(root)
                               .getConfigurationNode());

        assertThat(xmlParser2.getXml(), isIdenticalTo(xml));

        handle.getDocumentContext().getRuntime().destroy();
    }

}
