package org.oddjob.arooa.design;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.MappingsSwitch;
import org.oddjob.arooa.design.model.MockDesignInstance;
import org.oddjob.arooa.design.screem.Form;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.*;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.oddjob.arooa.runtime.MockConfigurationNode;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;

public class SimpleDesignPropertyTest {

    public static class Fruit {

    }

    private class ParentContext extends MockArooaContext {

        ArooaSession session;

        @Override
        public ArooaSession getSession() {
            return session;
        }

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


    }

    private class OurDesign extends MockDesignInstance {
        ArooaContext context;

        @Override
        public ArooaContext getArooaContext() {
            return context;
        }
    }

    @Test
    public void testAddClassElement() {

        ArooaSession standardSession = new StandardArooaSession();

        ParentContext parentContext = new ParentContext();
        parentContext.session = standardSession;

        OurDesign design = new OurDesign();
        design.context = parentContext;

        SimpleDesignProperty test = new SimpleDesignProperty(
                "fruit", Fruit.class, ArooaType.VALUE, design);

        assertEquals(0, test.instanceCount());

        ArooaContext instanceContext =
                test.getArooaContext().getArooaHandler().onStartElement(
                        new ArooaElement("class"), test.getArooaContext());

        test.getArooaContext().getConfigurationNode().insertChild(
                instanceContext.getConfigurationNode());

        instanceContext.getRuntime().init();

        assertEquals(1, test.instanceCount());
    }

    private class OurInstance extends DesignInstanceBase {

        DesignProperty prop;

        public OurInstance(ArooaElement element, ArooaContext context) {
            super(element, new SimpleArooaClass(Fruit.class), context);
        }

        public Form detail() {
            throw new RuntimeException("Unexpected.");
        }

        @Override
        public DesignProperty[] children() {
            return new DesignProperty[]{prop};
        }
    }

    private class OurDescriptor extends MockArooaDescriptor {

        @Override
        public ArooaBeanDescriptor getBeanDescriptor(
                ArooaClass classIdentifier, PropertyAccessor accessor) {
            return new MockArooaBeanDescriptor() {
                @Override
                public String getComponentProperty() {
                    return "fruit";
                }
            };
        }

        @Override
        public ElementMappings getElementMappings() {
            return new MappingsSwitch(null,
                    new MockElementMappings() {
                        @Override
                        public ArooaClass mappingFor(ArooaElement element,
                                                     InstantiationContext parentContext) {
                            assertEquals("idontexist", element.getTag());

                            return null;
                        }

                        @Override
                        public DesignFactory designFor(ArooaElement element,
                                                       InstantiationContext parentContext) {
                            return null;
                        }

                        @Override
                        public ArooaElement[] elementsFor(InstantiationContext parentContext) {
                            return null;
                        }
                    });
        }

        @Override
        public ConversionProvider getConvertletProvider() {
            return null;
        }
    }


    @Test
    public void testAddRubbishElement() throws Exception {

        ArooaDescriptor descriptor = new OurDescriptor();

        ArooaContext context = new DesignSeedContext(
                ArooaType.VALUE,
                new StandardArooaSession(descriptor));

        OurInstance instance = new OurInstance(
                new ArooaElement("test"), context);

        final SimpleDesignProperty test = new SimpleDesignProperty(
                "fruit", Fruit.class, ArooaType.VALUE, instance);

        instance.prop = test;

        CutAndPasteSupport cutAndPaste = new CutAndPasteSupport(instance.getArooaContext());

        String xml = "<idontexist/>" + System.getProperty("line.separator");

        cutAndPaste.paste(0, new XMLConfiguration("TEST", xml));

        assertEquals(1, test.instanceCount());

        Unknown result = (Unknown) test.instanceAt(0);

        assertThat(result.getXml(), isSimilarTo(xml));
    }

}
