package org.oddjob.arooa.xml;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.*;
import org.oddjob.arooa.standard.StandardArooaParser;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class XmlInterceptorTest {

    private class TestContext extends MockArooaContext {
        String name;
        String result;

        RuntimeListener listener;

        ConfigurationNode configurationNode = new AbstractConfigurationNode() {
            @Override
            public ArooaContext getContext() {
                return TestContext.this;
            }

            @Override
            public void addText(String text) {
                throw new RuntimeException("Unexpected");
            }

            @Override
            public ConfigurationHandle parse(ArooaContext parseParentContext) {
                throw new RuntimeException("Unexpected");
            }
        };

        @Override
        public PrefixMappings getPrefixMappings() {
            return new SimplePrefixMappings();
        }

        @Override
        public RuntimeConfiguration getRuntime() {
            return new MockRuntimeConfiguration() {
                @Override
                public void addRuntimeListener(
                        RuntimeListener listener) {
                    TestContext.this.listener = listener;
                }

                @Override
                public void setProperty(String name, Object value) throws ArooaException {
                    TestContext.this.name = name;
                    result = (String) value;
                }

                @Override
                public void init() throws ArooaException {
                    RuntimeEvent event = new RuntimeEvent(this);
//                    TestContext.this.listener.beforeInit(event);
                }
            };

        }

        @Override
        public ConfigurationNode getConfigurationNode() {
            return configurationNode;
        }
    }

    @Test
    public void testMethodAtATime() {

        ParsingInterceptor test = new XMLInterceptor("result");

        TestContext testContext = new TestContext();

        ArooaContext xmlContext = test.intercept(testContext);

        MutableAttributes a1 = new MutableAttributes();
        ArooaElement e1 = new ArooaElement("a", a1);

        MutableAttributes a2 = new MutableAttributes();
        a2.set("x", "y");
        ArooaElement e2 = new ArooaElement("b", a2);

        ArooaContext nextContext = xmlContext.getArooaHandler(
        ).onStartElement(e1, xmlContext);

        nextContext.getConfigurationNode().addText("Hello World");

        ArooaContext anotherContext = nextContext.getArooaHandler(
        ).onStartElement(e2, nextContext);

        anotherContext.getRuntime().init();

        nextContext.getRuntime().init();

        testContext.getRuntime().init();

        assertEquals("result", testContext.name);

        String ls = System.getProperty("line.separator");

        String expected = "<a>" + ls +
                "    <b x=\"y\"/><![CDATA[Hello World]]></a>" + ls;

        assertThat(testContext.result, isSimilarTo(expected));
    }


    public static class AComp {

        String xml;

        public void setXml(String xml) {
            if (this.xml != null) {
                throw new RuntimeException("Expected Once.");
            }
            this.xml = xml;
        }
    }

    private class OurDescriptor extends MockArooaDescriptor {
        @Override
        public ConversionProvider getConvertletProvider() {
            return null;
        }

        @Override
        public ElementMappings getElementMappings() {
            return null;
        }

        @Override
        public ArooaBeanDescriptor getBeanDescriptor(
                ArooaClass forClass, PropertyAccessor accessor) {
            if (forClass == null) {
                return null;
            }
            assertEquals(new SimpleArooaClass(AComp.class),
                    forClass);
            return new MockArooaBeanDescriptor() {
                @Override
                public ParsingInterceptor getParsingInterceptor() {
                    return new XMLInterceptor("xml");
                }

                @Override
                public String getComponentProperty() {
                    return null;
                }

                @Override
                public ArooaAnnotations getAnnotations() {
                    return new NoAnnotations();
                }
            };
        }
    }

    @Test
    public void testInParser() throws Exception {

        String xml = "<comp>" +
                "<a fruit='apple' colour='red'><b/></a>" +
                "</comp>";

        AComp comp = new AComp();

        StandardArooaParser parser = new StandardArooaParser(
                comp, new OurDescriptor());
        parser.parse(new XMLConfiguration("Test", xml));

        String ls = System.getProperty("line.separator");

        String expected =
                "<a fruit=\"apple\"" + ls +
                        "   colour=\"red\">" + ls +
                        "    <b/>" + ls +
                        "</a>";
        assertThat(comp.xml, isSimilarTo(expected));
    }

    @Test
    public void testReplaceSetsXml() throws ArooaParseException {

        XMLConfiguration xml = new XMLConfiguration("XLM",
                "<foo stuff='a'/>");

        TestContext testContext = new TestContext();

        ArooaContext context = new XMLInterceptor("xml").intercept(testContext);

        ConfigurationHandle handle = xml.parse(context);

        assertThat(handle.getDocumentContext().getParent(), sameInstance(context));

        testContext.getRuntime().init();

        assertThat(testContext.result, notNullValue());

        assertThat(testContext.result, isSimilarTo("<foo stuff='a'/>"));

        CutAndPasteSupport.ReplaceResult replaceResult =
                CutAndPasteSupport.replace(context,
                new ChildCatcher(context, 0).getChild(),
                new XMLConfiguration("REPLACEMENT", "<foo stuff='b'/>"));

        assertThat(replaceResult.getException(), nullValue());

        XmlHandler2.XMLContext xmlDocContext =
                (XmlHandler2.XMLContext) replaceResult.getHandle().getDocumentContext();

        assertThat(xmlDocContext.getParent(), sameInstance(context));

        assertThat(xmlDocContext.current.getAttribute("stuff"), is("b"));

        assertThat(testContext.result, isSimilarTo("<foo stuff='b'/>"));
    }
}
