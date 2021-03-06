package org.oddjob.arooa.design;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.ListDescriptor;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.design.etc.UnknownInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class DesignParserTest {

    public static class Thing {

        public void setColour(String colour) {
        }
    }

    private static class OurDescriptor extends MockArooaDescriptor {

        @Override
        public ConversionProvider getConvertletProvider() {
            return null;
        }

        @Override
        public ArooaBeanDescriptor getBeanDescriptor(
                ArooaClass forClass, PropertyAccessor accessor) {
            return null;
        }

        @Override
        public ElementMappings getElementMappings() {
            return null;
        }

    }

    private static class OurDesignF implements DesignFactory {

        public DesignInstance createDesign(
                ArooaElement element, ArooaContext parentContext) {
            return new OurDesign(element, parentContext);
        }
    }

    private static class OurDesign extends DesignValueBase {

        private final SimpleTextAttribute colour;

        public OurDesign(ArooaElement element, ArooaContext parentContext) {
            super(element, new SimpleArooaClass(Thing.class), parentContext);

            colour = new SimpleTextAttribute("colour", this);
        }

        @Override
        public DesignProperty[] children() {
            return new DesignProperty[]{colour};
        }

        public Form detail() {
            throw new RuntimeException("Unexpected.");
        }
    }

    @Test
    public void testParse() throws ArooaParseException, URISyntaxException {

        DesignParser test = new DesignParser(
                new StandardArooaSession(new OurDescriptor()),
                new OurDesignF());

        test.setExpectedDocumentElement(new ArooaElement(new URI("http://cars"), "car"));

        String xml = "<cars:car xmlns:cars='http://cars' " +
                "colour='red'/>";

        test.parse(new XMLConfiguration("TEST", xml));

        OurDesign result = (OurDesign) test.getDesign();

        assertThat(result.colour.attribute(), is("red"));
    }

    String EOL = System.getProperty("line.separator");

    @Test
    public void testTestBadParse() throws Exception {

        DesignParser test = new DesignParser(
                new StandardArooaSession(new OurDescriptor()),
                new OurDesignF());

        test.setExpectedDocumentElement(new ArooaElement("car"));

        String xml =
                "<lorry colour=\"red\"><![CDATA[This isn't a Car!]]></lorry>";

        test.parse(new XMLConfiguration("TEST", xml));

        UnknownInstance result = (UnknownInstance) test.getDesign();

        assertThat(result.getXml(), isSimilarTo(xml).ignoreWhitespace());

        // Now correct it with paste.

        String xml2 = "<car colour='red'/>";

        CutAndPasteSupport.replace(
                result.getArooaContext().getParent(),
                result.getArooaContext(),
                new XMLConfiguration("TEST", xml2));

        OurDesign result2 = (OurDesign) test.getDesign();

        assertThat(result2.colour.attribute(), is("red"));

        //

        CutAndPasteSupport.replace(
                result2.getArooaContext().getParent(),
                result2.getArooaContext(),
                new XMLConfiguration("TEST", xml));


    }

    public static class OtherThing {

        private Thing thing;

        public Thing getThing() {
            return thing;
        }

        @ArooaComponent
        public void setThing(Thing thing) {
            this.thing = thing;
        }
    }

    private static class OtherDescriptor extends MockArooaDescriptor {

        @Override
        public ConversionProvider getConvertletProvider() {
            return null;
        }

        @Override
        public ElementMappings getElementMappings() {
            return new MappingsSwitch(new MockElementMappings() {
                @Override
                public ArooaClass mappingFor(ArooaElement element,
                                             InstantiationContext parentContext) {
                    if (new ArooaElement("other").equals(element)) {
                        return new SimpleArooaClass(OtherThing.class);
                    }
                    if (new ArooaElement("thing").equals(element)) {
                        return new SimpleArooaClass(Thing.class);
                    }
                    throw new RuntimeException("Unexepected.");
                }

                @Override
                public DesignFactory designFor(ArooaElement element,
                                               InstantiationContext parentContext) {
                    return null;
                }
            }, null);
        }

        @Override
        public ArooaBeanDescriptor getBeanDescriptor(
                ArooaClass classIdentifier, PropertyAccessor accessor) {
            return null;
        }
    }

    @Test
    public void testWithComponentProperties() throws ArooaParseException {

        ArooaDescriptor descriptor = new ListDescriptor(new OtherDescriptor(),
                new OurDescriptor());

        DesignParser test = new DesignParser(
                new StandardArooaSession(descriptor));
        test.setArooaType(ArooaType.COMPONENT);

        String xml =
                "<other>" +
                        " <thing>" +
                        "  <thing/>" +
                        " </thing>" +
                        "</other>";

        test.parse(new XMLConfiguration("TEST", xml));

        DesignComponentInstance result =
                (DesignComponentInstance) test.getDesign();

        DesignProperty[] children = result.children();

        SimpleDesignProperty child = (SimpleDesignProperty) children[0];

        assertThat(child.property(), is("thing"));

        DesignComponentInstance thing =
                (DesignComponentInstance) child.instanceAt(0);

        assertThat(thing, Matchers.notNullValue());
    }

    @Test
    public void testWithId() throws ArooaParseException, URISyntaxException, SAXException, IOException {

        ArooaDescriptor descriptor = new ListDescriptor(new OtherDescriptor(),
                new OurDescriptor());

        DesignParser test = new DesignParser(
                new StandardArooaSession(descriptor));
        test.setArooaType(ArooaType.COMPONENT);

        String xml = "<other id='apple'/>";

        test.parse(new XMLConfiguration("TEST", xml));

        DesignComponent design = (DesignComponent) test.getDesign();

        assertThat(design.getId(), is("apple"));

        XMLArooaParser xmlParser = new XMLArooaParser(descriptor);

        xmlParser.parse(design.getArooaContext().getConfigurationNode());

        assertThat(xmlParser.getXml(), isSimilarTo(xml));
    }

}
