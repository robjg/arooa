package org.oddjob.arooa.types;

import org.junit.Test;
import org.mockito.Mockito;
import org.oddjob.arooa.*;
import org.oddjob.arooa.design.*;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.design.screem.StandardForm;
import org.oddjob.arooa.design.view.ViewMainHelper;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.SessionOverrideContext;
import org.oddjob.arooa.parsing.SimplePrefixMappings;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.utils.MutablesOverrideSession;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class InlineTypeDesignFactoryTest {

    public static class StuffDesignFactory implements DesignFactory {

        public DesignInstance createDesign(
                ArooaElement element, ArooaContext parentContext) {

            ArooaSession session = new MutablesOverrideSession(
                    parentContext.getSession());

            session.getBeanRegistry().register(
                    InlineType.INLINE_CONFIGURATION_DEFINITION,
                    InlineType.configurationDefinition(
                            new ArooaElement("stuff"),
                            this
                    )
            );

            SessionOverrideContext newContext = new SessionOverrideContext(
                    parentContext, session);

            return new StuffDesign(element, newContext);
        }
    }

    static private class StuffDesign extends DesignComponentBase {

        private final SimpleTextAttribute name;

        private final SimpleDesignProperty config;


        public StuffDesign(ArooaElement element, ArooaContext parentContext) {
            super(element,
                    parentContext);

            name = new SimpleTextAttribute("name", this);

            config = new SimpleDesignProperty("config", this);
        }

        @Override
        public DesignProperty[] children() {
            return new DesignProperty[]{ name, config };
        }

        public Form detail() {
            return new StandardForm("Test", this)
                    .addFormItem(name.view().setTitle("Name"))
                    .addFormItem(config.view().setTitle("Config"));
        }

    }

    DesignInstance design;

    @Test
    public void testDesignInstanceOnOwn() throws ArooaParseException {

        String xml = "<inline>" +
                "<stuff name='fred'/>" +
                "</inline>";

        DesignFactory test = new InlineTypeDesignFactory();

        ArooaDescriptor arooaDescriptor = InlineTypeTest.arooaDescriptor();

        ArooaSession session = Mockito.mock(ArooaSession.class);
        Mockito.when(session.getArooaDescriptor()).thenReturn(arooaDescriptor);

        ArooaContext parentContext = Mockito.mock( ArooaContext.class );
        Mockito.when(parentContext.getPrefixMappings()).thenReturn(new SimplePrefixMappings());

        DesignParser designParser = new DesignParser(session, test);

        ConfigurationHandle handle = designParser.parse(new XMLConfiguration("XML", xml));

        DesignInstance designInstance = designParser.getDesign();

        XMLArooaParser xmlParser = new XMLArooaParser();
        xmlParser.parse(handle.getDocumentContext().getConfigurationNode());

        assertThat(xmlParser.getXml(), isSimilarTo(xml).ignoreWhitespace());
    }


    @Test
    public void testDesign() throws Exception {


        DesignParser parser = new DesignParser(
                new StandardArooaSession(InlineTypeTest.arooaDescriptor()),
                new StuffDesignFactory());
        parser.setArooaType(ArooaType.COMPONENT);
        parser.setExpectedDocumentElement(new ArooaElement("stuff"));

        parser.parse(new XMLConfiguration("TEST", InlineTypeTest.SOME_XML));

        StuffDesign design = (StuffDesign) parser.getDesign();

        XMLArooaParser xmlParser = new XMLArooaParser();

        xmlParser.parse(design.getArooaContext().getConfigurationNode());

        assertThat(xmlParser.getXml(), isSimilarTo(InlineTypeTest.SOME_XML));

        this.design = design;
    }

    public static void main(String... args) throws Exception {

        InlineTypeDesignFactoryTest test = new InlineTypeDesignFactoryTest();
        test.testDesign();

        ViewMainHelper helper = new ViewMainHelper(test.design);
        helper.run();

    }
}
