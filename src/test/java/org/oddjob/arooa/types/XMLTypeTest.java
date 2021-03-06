package org.oddjob.arooa.types;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.DefaultConverter;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.ModificationRefusedException;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class XMLTypeTest {

    static final String EOL = System.getProperty("line.separator");

    public static class HasXMLProperty {

        String ourXml;

        public void setOurXml(String xml) {
            this.ourXml = xml;
        }
    }

    public static class HasXMLPropertyArooa extends MockArooaBeanDescriptor {

        @Override
        public ConfiguredHow getConfiguredHow(String property) {
            return ConfiguredHow.ELEMENT;
        }

        @Override
        public ParsingInterceptor getParsingInterceptor() {
            return null;
        }

        @Override
        public String getComponentProperty() {
            return null;
        }

        @Override
        public boolean isAuto(String property) {
            return false;
        }

        @Override
        public ArooaAnnotations getAnnotations() {
            return new NoAnnotations();
        }
    }

    @Test
    public void testAsValue() throws Exception {

        String xml =
                "<whatever>" +
                        " <ourXml>" +
                        "  <xml>" +
                        "   <snack>" +
                        "    <fruit>" +
                        "     <apple/>" +
                        "    </fruit>" +
                        "   </snack>" +
                        "  </xml>" +
                        " </ourXml>" +
                        "</whatever>";

        HasXMLProperty bean = new HasXMLProperty();

        StandardArooaParser parser = new StandardArooaParser(bean);

        parser.parse(new XMLConfiguration("TEST", xml));

        ArooaSession session = parser.getSession();

        session.getComponentPool().configure(bean);

        String expected =
                "<snack>" + EOL +
                        "    <fruit>" + EOL +
                        "        <apple/>" + EOL +
                        "    </fruit>" + EOL +
                        "</snack>" + EOL;

        assertThat(bean.ourXml, isSimilarTo(expected));

    }

    @Test
    public void testMultipleDocElementsInXml() {

        String xml =
                "<whatever>" +
                        " <ourXml>" +
                        "  <xml>" +
                        "   <apple/>" +
                        "   <apple/>" +
                        "  </xml>" +
                        " </ourXml>" +
                        "</whatever>";

        HasXMLProperty bean = new HasXMLProperty();

        StandardArooaParser parser = new StandardArooaParser(bean);

        try {
            parser.parse(new XMLConfiguration("TEST", xml));
            fail("Should throw exception.");
        } catch (ArooaParseException e) {
            // Ignore.
        }
    }

    @Test
    public void testAsFragment() throws Exception {

        String xml =
                "<xml>" +
                        " <apples/>" +
                        "</xml>";

        StandardFragmentParser parser = new StandardFragmentParser();

        parser.parse(new XMLConfiguration("TEST", xml));

        XMLType test = (XMLType) parser.getRoot();

        DefaultConverter converter = new DefaultConverter();

        String result = converter.convert(
                test, String.class);

        assertThat(result, isSimilarTo("<apples/>" + EOL));
    }


    DesignInstance design;

    private class HasXMLPropertyDesignF implements DesignFactory {

        public DesignInstance createDesign(
                ArooaElement element, ArooaContext parentContext) {
            return new HasXMLPropertyDesign(element, parentContext);
        }
    }

    private class HasXMLPropertyDesign extends DesignValueBase {

        SimpleDesignProperty ourXml;

        public HasXMLPropertyDesign(ArooaElement element, ArooaContext parentContext) {
            super(element,
                    new SimpleArooaClass(HasXMLProperty.class),
                    parentContext);

            ourXml = new SimpleDesignProperty("ourXml", this);
        }

        @Override
        public DesignProperty[] children() {
            return new DesignProperty[]{ourXml};
        }

        public Form detail() {
            return new StandardForm("Test", this).addFormItem(ourXml.view());
        }

    }

    @Test
    public void testDesign() throws Exception {

        String xml =
                "<whatever>" + EOL +
                        "    <ourXml>" + EOL +
                        "        <xml>" + EOL +
                        "            <snack>" + EOL +
                        "                <fruit>" + EOL +
                        "                    <apple/>" + EOL +
                        "                </fruit>" + EOL +
                        "            </snack>" + EOL +
                        "        </xml>" + EOL +
                        "    </ourXml>" + EOL +
                        "</whatever>" + EOL;

        DesignParser parser = new DesignParser(
                new StandardArooaSession(),
                new HasXMLPropertyDesignF());

        parser.setExpectedDocumentElement(new ArooaElement("whatever"));

        parser.parse(new XMLConfiguration("TEST", xml));

        HasXMLPropertyDesign design = (HasXMLPropertyDesign) parser.getDesign();

        XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());

        xmlParser.parse(design.getArooaContext().getConfigurationNode());

        assertThat(xml, isSimilarTo(xmlParser.getXml()));

        this.design = design;
    }

    public static void main(String args[]) throws Exception {

        XMLTypeTest test = new XMLTypeTest();
        test.testDesign();

        ViewMainHelper helper = new ViewMainHelper(test.design);
        helper.run();

    }

    public static class HasArooaConfigurationProperty {

        ArooaConfiguration ourConfig;

        public void setOurConfig(ArooaConfiguration ourConfig) {
            this.ourConfig = ourConfig;
        }
    }

    AtomicBoolean changed = new AtomicBoolean();

    private class ChangeListener implements ConfigurationNodeListener {

        public void childInserted(ConfigurationNodeEvent nodeEvent) {
            ConfigurationNode node = nodeEvent.getChild();
            node.addNodeListener(new ChangeListener());
            changed.set(true);
        }

        public void childRemoved(ConfigurationNodeEvent nodeEvent) {
            changed.set(true);
        }

        public void insertRequest(ConfigurationNodeEvent nodeEvent)
                throws ModificationRefusedException {
        }

        public void removalRequest(ConfigurationNodeEvent nodeEvent)
                throws ModificationRefusedException {
        }
    }


    @Test
    public void testAsConfiguration() throws Exception {


        String xml =
                "<whatever>" +
                        " <ourConfig>" +
                        "  <xml>" +
                        "   <snack>" +
                        "    <fruit>" +
                        "     <apple/>" +
                        "    </fruit>" +
                        "   </snack>" +
                        "  </xml>" +
                        " </ourConfig>" +
                        "</whatever>";

        HasArooaConfigurationProperty bean = new HasArooaConfigurationProperty();

        StandardArooaParser parser = new StandardArooaParser(bean);

        XMLConfiguration config = new XMLConfiguration("TEST", xml);

        final AtomicReference<String> savedXML = new AtomicReference<>();
        config.setSaveHandler(savedXML::set);

        ConfigurationHandle handle = parser.parse(config);

        ArooaSession session = parser.getSession();

        session.getComponentPool().configure(bean);

        XMLArooaParser xmlParser = new XMLArooaParser(NamespaceMappings.empty());

        ConfigurationHandle<SimpleParseContext> xmlHandle = xmlParser.parse(bean.ourConfig);

        String expected =
                "<snack>" + EOL +
                        "    <fruit>" + EOL +
                        "        <apple/>" + EOL +
                        "    </fruit>" + EOL +
                        "</snack>" + EOL;

        assertThat(xmlParser.getXml(), isSimilarTo(expected));

        ChangeListener listener = new ChangeListener();

        handle.getDocumentContext().getConfigurationNode(
        ).addNodeListener(listener);

        changed.set(false);

        CutAndPasteSupport.cut(xmlHandle.getDocumentContext(),
                new ChildCatcher<>(xmlHandle.getDocumentContext(), 0).getChild());

        assertFalse(changed.get());

        xmlHandle.save();

        assertTrue(changed.get());

        handle.save();

        String saved =
                "<whatever>" +
                        " <ourConfig>" +
                        "  <xml>" +
                        "   <snack/>" +
                        "    </xml>" +
                        " </ourConfig>" +
                        "</whatever>";

        XMLArooaParser xmlParser2 = new XMLArooaParser(NamespaceMappings.empty());

        xmlParser2.parse(handle.getDocumentContext().getConfigurationNode());

        Diff diff = DiffBuilder.compare(saved)
                .withTest(xmlParser2.getXml()).ignoreWhitespace().build();

        assertFalse(diff.toString(), diff.hasDifferences());
    }

    @Test
    public void testAsNullConfiguration() throws Exception {

        String xml =
                "<whatever>" +
                        " <ourConfig>" +
                        "  <xml/>" +
                        " </ourConfig>" +
                        "</whatever>";

        HasArooaConfigurationProperty bean = new HasArooaConfigurationProperty();

        StandardArooaParser parser = new StandardArooaParser(bean);

        parser.parse(new XMLConfiguration("TEST", xml));

        ArooaSession session = parser.getSession();

        session.getComponentPool().configure(bean);

        assertNull(bean.ourConfig);
    }

}
