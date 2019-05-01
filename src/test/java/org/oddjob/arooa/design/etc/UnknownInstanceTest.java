package org.oddjob.arooa.design.etc;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class UnknownInstanceTest {

    String EOL = System.getProperty("line.separator");

    @Test
    public void testXmlCaptureByUnknownInstance() throws ArooaParseException {

        ArooaSession session = new MockArooaSession();

        DesignSeedContext context = new DesignSeedContext(
                ArooaType.VALUE, session);

        UnknownInstance test = new UnknownInstance(
                new ArooaElement("apple"),
                context);

        String xml = "<description>\n" +
                "<colour value='red'/>\n" +
                "</description>\n";

        ArooaConfiguration configuration = new XMLConfiguration("XML", xml);

        configuration.parse(test.getArooaContext());

        assertThat(test.getXml(), is( "") );

        test.getArooaContext().getRuntime().init();

        String expected = "<apple>\n" + xml + "</apple>\n";

        assertThat(test.getXml(), isSimilarTo( expected ).ignoreWhitespace() );

        // And now test replace too

        test.getArooaContext().getRuntime().destroy();

        String replacement = "<description>\n" +
                "<colour value='green'/>\n" +
                "</description>\n";

        CutAndPasteSupport.replace(test.getArooaContext(),
                new ChildCatcher(test.getArooaContext(), 0).getChild(),
                new XMLConfiguration("XML", replacement));

        test.getArooaContext().getRuntime().init();

        String expected2 = "<apple>\n" + replacement + "</apple>\n";

        assertThat(test.getXml(), isSimilarTo( expected2 ).ignoreWhitespace() );
    }

    /**
     * Test parsing the Component that has had xml set manually (i.e. from
     * a GUI.
     *
     */
    @Test
    public void testParse() throws Exception {

        ArooaSession session = new MockArooaSession();

        DesignSeedContext context = new DesignSeedContext(
                ArooaType.VALUE, session);

        UnknownInstance test = new UnknownInstance(
                new ArooaElement("apple"),
                context);

        String xml = "<fruit:ThisNeedntBeApple  xmlns:fruit=\"http://fruit\"/>";

        test.setXml(xml);

        XMLArooaParser parser = new XMLArooaParser();

        parser.parse(test.getArooaContext().getConfigurationNode());

        String expected = xml;

        assertThat(parser.getXml(), isSimilarTo(expected).ignoreWhitespace());
    }

    /**
     * Test More Complicated parsing.
     *
     * @throws URISyntaxException
     * @throws ArooaParseException
     */
    @Test
    public void testParse2() throws Exception {

        ArooaSession session = new MockArooaSession();

        DesignSeedContext context = new DesignSeedContext(
                ArooaType.VALUE, session);

        UnknownInstance test = new UnknownInstance(
                new ArooaElement("apple"),
                context);

        String xml =
                "<snack xmlns:fruit=\"http://fruit\">" + EOL +
                        "    <fruit>" + EOL +
                        "        <fruit:apple colour=\"red\">" + EOL +
                        "            <description>" + EOL +
                        "                <text><![CDATA[Yummy]]></text>" + EOL +
                        "            </description>" + EOL +
                        "        </fruit:apple>" + EOL +
                        "    </fruit>" + EOL +
                        "</snack>" + EOL;

        test.setXml(xml);

        XMLArooaParser parser = new XMLArooaParser();

        parser.parse(test.getArooaContext().getConfigurationNode());

        assertThat(parser.getXml(), isSimilarTo(xml));
    }

    /**
     * No prefix mapping throws exception.
     *
     * @throws URISyntaxException
     * @throws ArooaParseException
     */
    @Test
    public void testMissingPrefixMapping() {

        ArooaSession session = new MockArooaSession();

        DesignSeedContext context = new DesignSeedContext(
                ArooaType.VALUE, session);

        UnknownInstance test = new UnknownInstance(
                new ArooaElement("apple"),
                context);

        String xml = "<notmapped:ThisNeedntBeApple/>";

        test.setXml(xml);

        XMLArooaParser parser = new XMLArooaParser();

        try {
            parser.parse(test.getArooaContext().getConfigurationNode());
            fail("Invalid XML should throw exception.");
        } catch (ArooaParseException expected) {
            // expected
        }
    }


    class OurContext extends MockArooaContext {

        @Override
        public PrefixMappings getPrefixMappings() {
            return new SimplePrefixMappings();
        }

        @Override
        public ConfigurationNode getConfigurationNode() {
            return new MockConfigurationNode() {
                @Override
                public int insertChild(ConfigurationNode child) {
                    return -1;
                }
            };
        }

        @Override
        public ArooaSession getSession() {
            return new MockArooaSession() {
            };
        }

    }

    class OurInstance extends MockDesignInstance {

        @Override
        public ArooaContext getArooaContext() {
            return new OurContext();
        }
    }

    class OurListener implements DesignListener {
        DesignInstance result;

        public void childAdded(DesignStructureEvent event) {
            assertNull(result);
            assertEquals(0, event.getIndex());
            result = event.getChild();

        }

        public void childRemoved(DesignStructureEvent event) {
            throw new RuntimeException("Unexpected.");
        }
    }

    /**
     * Test Adding an unknown instance as a child.
     * Can't quite remember what this is really testing!
     */
    @Test
    public void testAsChild() {

        IndexedDesignProperty prop = new IndexedDesignProperty(
                "prop", Object.class, ArooaType.VALUE, new OurInstance());

        OurListener listener = new OurListener();

        prop.addDesignListener(listener);

        UnknownInstance test = new UnknownInstance(
                new ArooaElement("Whatever"),
                prop.getArooaContext());

        ConfigurationNode testConfigurationNode = test.getArooaContext().getConfigurationNode();

        prop.getArooaContext().getConfigurationNode().insertChild(
                testConfigurationNode);

        test.getArooaContext().getRuntime().init();

        assertNotNull(listener.result);

        assertEquals(UnknownInstance.class, listener.result.getClass());
    }

    UnknownInstance test;

    @Test
    public void testTheContext() throws Exception {

        ArooaSession session = new MockArooaSession();

        ArooaContext rootContext = new RootContext(
                ArooaType.VALUE,
                session,
                (element, parentContext) -> {
                    test = new UnknownInstance(element, parentContext);
                    return test.getArooaContext();
                });

        String xml =
                "<snack xmlns:fruit=\"http://fruit\">" + EOL +
                        "    <fruit>" + EOL +
                        "        <fruit:apple colour=\"red\">" + EOL +
                        "            <description>" + EOL +
                        "                <text><![CDATA[Yummy]]></text>" + EOL +
                        "            </description>" + EOL +
                        "        </fruit:apple>" + EOL +
                        "    </fruit>" + EOL +
                        "</snack>" + EOL;

        XMLConfiguration config = new XMLConfiguration("TEST", xml);

        config.parse(rootContext);

        assertThat(test.getXml(), isSimilarTo(xml));

        XMLArooaParser parser = new XMLArooaParser();

        parser.parse(test.getArooaContext().getConfigurationNode());

        assertThat(parser.getXml(), isSimilarTo(xml));
    }
}
