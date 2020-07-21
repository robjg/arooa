package org.oddjob.arooa.standard;

import org.junit.Assert;
import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockRuntimeConfiguration;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.arooa.xml.XMLConfigurationNode;

/**
 * Test a bespoke component handler can be used.
 */
public class BespokeBuildTest extends Assert {

    private class BespokeBuildComponent {

    }

    private class OurContext extends MockArooaContext {

        final ArooaContext parent;

        final ConfigurationNode runtimeNode;

        OurRuntime runtime;

        OurContext(ArooaContext parent, ConfigurationNode runtimeNode) {
            this.parent = parent;
            this.runtimeNode = runtimeNode;
        }

        @Override
        public ConfigurationNode getConfigurationNode() {
            return runtimeNode;
        }

        @Override
        public RuntimeConfiguration getRuntime() {
            return runtime;
        }

        @Override
        public ArooaContext getParent() {
            return parent;
        }
    }

    private class OurRuntime extends MockRuntimeConfiguration {

        String text = "";
        boolean endElement;

        ArooaContext ourContext;

        @Override
        public ArooaClass getClassIdentifier() {
            return new SimpleArooaClass(
                    BespokeBuildComponent.class);
        }

        public void init() {
            text = ((XMLConfigurationNode) ourContext.getConfigurationNode()).getText();
            endElement = true;
        }

    }


    private class OurBespokeHandler implements ArooaHandler {
        OurRuntime runtime;
        String startElement;

        public ArooaContext onStartElement(ArooaElement element, ArooaContext parentContext) throws ArooaException {

            startElement = element.getTag();
            runtime = new OurRuntime();

            XMLConfigurationNode runtimeNode = new XMLConfigurationNode(
                    element);

            OurContext newContext = new OurContext(parentContext, runtimeNode);
            newContext.runtime = runtime;

            runtime.ourContext = newContext;
            runtimeNode.setContext(newContext);

            return newContext;
        }
    }

    private class OurArooaDescriptor extends MockArooaDescriptor {
        ArooaHandler handler;

        @Override
        public ConversionProvider getConvertletProvider() {
            return null;
        }

        @Override
        public ElementMappings getElementMappings() {
            return null;
        }

        @Override
        public ArooaBeanDescriptor getBeanDescriptor(ArooaClass forClass,
                                                     PropertyAccessor accessor) {
            assertEquals("Descriptor class",
                    new SimpleArooaClass(BespokeBuildComponent.class), forClass);
            return new MockArooaBeanDescriptor() {
                @Override
                public ParsingInterceptor getParsingInterceptor() {
                    return new ParsingInterceptor() {
                        public ArooaContext intercept(ArooaContext context) {
                            return new HandlerOverrideContext(
                                    context, handler);
                        }
                    };
                }

                @Override
                public ArooaAnnotations getAnnotations() {
                    return new NoAnnotations();
                }
            };
        }
    }

    @Test
    public void testBuild() throws ArooaParseException {

        OurBespokeHandler handler = new OurBespokeHandler();

        OurArooaDescriptor descriptor = new OurArooaDescriptor();
        descriptor.handler = handler;

        BespokeBuildComponent b = new BespokeBuildComponent();

        XMLConfiguration config = new XMLConfiguration("bespoke-build-test.xml",
                getClass().getResourceAsStream("bespoke-build-test.xml"));

        StandardArooaParser parser = new StandardArooaParser(b, descriptor);

        parser.parse(config);

//		String eol = System.getProperty("line.separator");
        String eol = "\n";

        String expected =
                "This is being handled by our" + eol +
                        "bespoke handler.";

        assertEquals("Start element.", "bespoke", handler.startElement);
        assertEquals("Text", expected, handler.runtime.text.trim());
        assertTrue("End element.", handler.runtime.endElement);
    }

}
