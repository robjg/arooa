package org.oddjob.arooa.xml;

import org.junit.Test;
import org.oddjob.arooa.parsing.*;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class XMLHandler2Test {

    String ls = System.getProperty("line.separator");

    static class OurContext extends MockArooaContext {

        PrefixMappings prefixMappings = new SimplePrefixMappings();

        @Override
        public PrefixMappings getPrefixMappings() {
            return prefixMappings;
        }

    }

    @Test
    public void testSimple() throws Exception {

        XmlHandler2 test = new XmlHandler2();

        OurContext context = new OurContext();

        ArooaElement element1 = new ArooaElement(
                "fruit");

        ArooaContext context1 = test.onStartElement(element1, context);

        ArooaElement element2 = new ArooaElement(
                "apple");

        ArooaContext context2 = context1.getArooaHandler().onStartElement(
                element2, context);

        context2.getRuntime().init();

        context1.getRuntime().init();

        String expected = "<fruit>" + ls +
                "    <apple/>" + ls +
                "</fruit>" + ls;

        assertThat(test.getXml(), isSimilarTo(expected));
    }

    @Test
    public void testAttributes() throws Exception {

        XmlHandler2 test = new XmlHandler2();

        OurContext context = new OurContext();

        MutableAttributes attributes = new MutableAttributes();
        attributes.set("colour", "red");

        ArooaElement element1 = new ArooaElement(
                "fruit", attributes);

        ArooaContext context1 = test.onStartElement(element1, context);

        context1.getRuntime().init();

        String expected = "<fruit colour=\"red\"/>" + ls;

        assertThat(test.getXml(), isSimilarTo(expected));
    }

    @Test
    public void testQuotesInAttributes() throws Exception {

        XmlHandler2 test = new XmlHandler2();

        OurContext context = new OurContext();

        MutableAttributes attributes = new MutableAttributes();
        attributes.set("colour", "\"red\"");

        ArooaElement element1 = new ArooaElement(
                "fruit", attributes);

        ArooaContext context1 = test.onStartElement(element1, context);

        context1.getRuntime().init();

        String expected = "<fruit colour=\"&quot;red&quot;\"/>" + ls;

        assertThat(test.getXml(), isSimilarTo(expected));
    }

    @Test
    public void testText() throws Exception {

        XmlHandler2 test = new XmlHandler2();

        OurContext context = new OurContext();

        ArooaElement element1 = new ArooaElement(
                "fruit");

        ArooaContext context1 = test.onStartElement(element1, context);

        context1.getConfigurationNode().addText("Very <> Nice");

        context1.getRuntime().init();

        String expected = "<fruit><![CDATA[Very <> Nice]]></fruit>" + ls;

        assertThat(test.getXml(), isSimilarTo(expected).ignoreWhitespace());
    }

    @Test
    public void testComplicated() throws Exception {

        XmlHandler2 test = new XmlHandler2();

        OurContext context = new OurContext();

        MutableAttributes attributes = new MutableAttributes();
        attributes.set("colour", "red");

        ArooaElement element1 = new ArooaElement(
                "fruit", attributes);

        ArooaContext context1 = test.onStartElement(element1, context);

        context1.getConfigurationNode().addText("Very & Nice");

        ArooaElement element2 = new ArooaElement(
                "apple");

        ArooaContext context2 = context1.getArooaHandler().onStartElement(
                element2, context);

        context2.getRuntime().init();

        context1.getRuntime().init();

        String expected = "<fruit colour=\"red\">" + ls +
                "    <apple/><![CDATA[Very & Nice]]></fruit>" + ls;

        assertThat(test.getXml(), isSimilarTo(expected).ignoreWhitespace());
    }

    @Test
    public void testNS() throws Exception {

        XmlHandler2 test = new XmlHandler2();

        ArooaElement element = new ArooaElement(
                new URI("http://www.rgordon.co.uk/oddjob/arooa"),
                "fruit");

        OurContext context = new OurContext();
        context.getPrefixMappings().put(
                "arooa",
                new URI("http://www.rgordon.co.uk/oddjob/arooa"));

        ArooaContext newContext = test.onStartElement(element, context);
        newContext.getRuntime().init();

        assertThat(test.getXml(), isSimilarTo(
                "<arooa:fruit xmlns:arooa=\"http://www.rgordon.co.uk/oddjob/arooa\"/>" +
                        ls));
    }

    @Test
    public void testDefaultNS() throws Exception {

        XmlHandler2 test = new XmlHandler2();

        ArooaElement element = new ArooaElement(
                new URI("http://www.rgordon.co.uk/oddjob/arooa"),
                "fruit");

        OurContext context = new OurContext();
        context.getPrefixMappings().put(
                "",
                new URI("http://www.rgordon.co.uk/oddjob/arooa"));

        ArooaContext newContext = test.onStartElement(element, context);
        newContext.getRuntime().init();

        assertThat(test.getXml(), isSimilarTo(
                "<fruit xmlns=\"http://www.rgordon.co.uk/oddjob/arooa\"/>" +
                        ls));
    }

    @Test
    public void testReplace() throws Exception {

        XmlHandler2 test = new XmlHandler2();

        OurContext context = new OurContext();

        ArooaElement element1 = new ArooaElement(
                "fruit");

        ArooaContext context1 = test.onStartElement(element1, context);

        ArooaElement element2 = new ArooaElement(
                "apple");

        ArooaContext context2 = context1.getArooaHandler().onStartElement(
                element2, context1);

        context2.getRuntime().init();

        context1.getConfigurationNode().insertChild(
                context2.getConfigurationNode());

        context1.getRuntime().init();

        context2.getRuntime().destroy();

        context1.getConfigurationNode().removeChild(0);

        context1.getConfigurationNode().setInsertPosition(0);

        ArooaElement element3 = new ArooaElement(
                "orange");

        ArooaContext context3 = context1.getArooaHandler().onStartElement(
                element3, context1);

        context1.getConfigurationNode().insertChild(
                context3.getConfigurationNode());

        context3.getRuntime().init();

        String expected =
                "<fruit>" + ls +
                        "    <orange/>" + ls +
                        "</fruit>" + ls;

        assertThat(test.getXml(), isSimilarTo(expected));

        XMLArooaParser parser = new XMLArooaParser(NamespaceMappings.empty());

        parser.parse(context1.getConfigurationNode());

        assertThat(parser.getXml(), isSimilarTo(expected));
    }
}
