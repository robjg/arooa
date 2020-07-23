package org.oddjob.arooa.types;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.deploy.ArooaDescriptorBean;
import org.oddjob.arooa.deploy.BeanDefinition;
import org.oddjob.arooa.deploy.ListDescriptor;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.parsing.SimpleParseContext;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.ConfigurationNodeEvent;
import org.oddjob.arooa.runtime.ConfigurationNodeListener;
import org.oddjob.arooa.runtime.ModificationRefusedException;
import org.oddjob.arooa.standard.StandardArooaDescriptor;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class InlineTypeTest {

    static final String INNER_CONFIG =
            "            <stuff name='Stuff2'/>\n";

    static final String SOME_XML =
            "<stuff name='Stuff1'>\n" +
                    "    <config>\n" +
                    "        <inline>\n" +
                    INNER_CONFIG +
                    "        </inline>\n" +
                    "    </config>\n" +
                    "</stuff>\n";


    public static class StuffBean {

        private String name;

        private ArooaConfiguration config;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArooaConfiguration getConfig() {
            return config;
        }

        public void setConfig(ArooaConfiguration config) {
            this.config = config;
        }
    }

    @Test
    public void testParseSetsConfig() throws ArooaParseException {

        ArooaSession arooaSession = new StandardArooaSession(arooaDescriptor());

        StuffBean root = new StuffBean();
        ConfigurationHandle<ArooaContext> handle1 = new StandardArooaParser(root, arooaSession )
                .parse(new XMLConfiguration("XML", SOME_XML));
        handle1.getDocumentContext().getRuntime().configure();

        assertThat(root.getConfig(), notNullValue());

        StuffBean root2 = new StuffBean();
        new StandardArooaParser(root2).parse(root.getConfig());

        assertThat(root2.getName(), is("Stuff2"));
    }

    static ArooaDescriptor arooaDescriptor() {

        BeanDefinition beanDef = new BeanDefinition();
        beanDef.setElement("stuff");
        beanDef.setClassName(StuffBean.class.getName());
        beanDef.setDesignFactory(InlineTypeDesignFactoryTest.StuffDesignFactory.class.getName());

        ArooaDescriptorBean ourDescriptorFactory = new ArooaDescriptorBean();
        ourDescriptorFactory.setComponents(0, beanDef);

        return new ListDescriptor(
                ourDescriptorFactory.createDescriptor(InlineTypeTest.class.getClassLoader()),
                new StandardArooaDescriptor());
    }


    AtomicBoolean changed = new AtomicBoolean();

    private class ChangeListener implements ConfigurationNodeListener<ArooaContext> {

        public void childInserted(ConfigurationNodeEvent<ArooaContext> nodeEvent) {
            ConfigurationNode<ArooaContext> node = nodeEvent.getChild();
            node.addNodeListener(new ChangeListener());
            changed.set(true);
        }

        public void childRemoved(ConfigurationNodeEvent<ArooaContext> nodeEvent) {
            changed.set(true);
        }

        public void insertRequest(ConfigurationNodeEvent<ArooaContext> nodeEvent)
                throws ModificationRefusedException {
        }

        public void removalRequest(ConfigurationNodeEvent<ArooaContext> nodeEvent)
                throws ModificationRefusedException {
        }
    }


    @Test
    public void testAsConfiguration() throws Exception {

        StuffBean bean = new StuffBean();

        StandardArooaParser parser = new StandardArooaParser(bean);

        XMLConfiguration config = new XMLConfiguration("TEST", SOME_XML);

        final AtomicReference<String> savedXML = new AtomicReference<>();
        config.setSaveHandler(savedXML::set);

        ConfigurationHandle<ArooaContext> handle = parser.parse(config);

        ArooaSession session = parser.getSession();

        session.getComponentPool().configure(bean);

        XMLArooaParser xmlParser = new XMLArooaParser(parser.getSession().getArooaDescriptor());

        ConfigurationHandle<SimpleParseContext> xmlHandle = xmlParser.parse(bean.getConfig());


        assertThat(xmlParser.getXml(), isSimilarTo(INNER_CONFIG));

        ChangeListener listener = new ChangeListener();

        handle.getDocumentContext().getConfigurationNode(
        ).addNodeListener(listener);

        changed.set(false);

        CutAndPasteSupport.replace(xmlHandle.getDocumentContext().getParent(),
                xmlHandle.getDocumentContext(),
                new XMLConfiguration("XML", "<stuff name='Stuff3'/>\n"));

        assertFalse(changed.get());

        xmlHandle.save();

        assertTrue(changed.get());

        handle.save();

        String expectedSaved =
                "<stuff name='Stuff1'>\n" +
                        "    <config>\n" +
                        "        <inline>\n" +
                        "<stuff name='Stuff3'/>\n" +
                        "        </inline>\n" +
                        "    </config>\n" +
                        "</stuff>\n";

        XMLArooaParser xmlParser2 = new XMLArooaParser(parser.getSession().getArooaDescriptor());
        xmlParser2.parse(handle.getDocumentContext().getConfigurationNode());
        String actualSaved = xmlParser2.getXml();

        Diff diff = DiffBuilder.compare(expectedSaved)
                .withTest(actualSaved).ignoreWhitespace().build();

        assertFalse("Expected: " + expectedSaved + "\n" +
                "Actual: " + actualSaved + "\n" +
                diff.toString(), diff.hasDifferences());
    }

    @Test
    public void testAsNullConfiguration() throws Exception {

        String xml =
                "<stuff>" +
                        " <config>" +
                        "  <inline/>" +
                        " </config>" +
                        "</stuff>";

        StuffBean bean = new StuffBean();

        StandardArooaParser parser = new StandardArooaParser(bean);

        parser.parse(new XMLConfiguration("TEST", xml));

        ArooaSession session = parser.getSession();

        session.getComponentPool().configure(bean);

        assertNull(bean.getConfig());
    }

}
