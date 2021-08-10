package org.oddjob.arooa.deploy;


import org.junit.Test;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.convert.gremlin.Gremlin;
import org.oddjob.arooa.convert.gremlin.GremlinSupplier;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ConversionDescriptorBeanTest {

    @Test
    public void testConversionDescriptor() throws ArooaParseException, NoConversionAvailableException, ConversionFailedException {

        ArooaConfiguration config = new XMLConfiguration(
                "Descriptor",
                ArooaDescriptorElementsTest.class.getResourceAsStream(
                        "ConversionProviderDescriptorTest1.xml"));

        StandardFragmentParser parser =
                new StandardFragmentParser(new ArooaDescriptorDescriptor());

        parser.parse(config);

        ArooaDescriptorFactory descriptorFactory = (ArooaDescriptorFactory) parser.getRoot();

        ArooaDescriptor descriptor = descriptorFactory.createDescriptor(getClass().getClassLoader());

        DefaultConversionRegistry conversionRegistry = new DefaultConversionRegistry();

        descriptor.getConvertletProvider().registerWith(conversionRegistry);

        DefaultConverter converter = new DefaultConverter(conversionRegistry);

        GremlinSupplier gremlinSupplier = new GremlinSupplier();
        gremlinSupplier.setName("Gizmo");

        Gremlin gremlin = converter.convert(gremlinSupplier, Gremlin.class);

        assertThat(gremlin.getName(), is("Gizmo"));
    }

    @Test
    public void testGremlinDescriptorReflectionExample() throws ArooaParseException, NoConversionAvailableException, ConversionFailedException {

        ArooaConfiguration config = new XMLConfiguration(
                "Descriptor",
                ArooaDescriptorElementsTest.class.getResourceAsStream(
                        "ConversionProviderDescriptorTest2.xml"));

        StandardFragmentParser parser =
                new StandardFragmentParser(new ArooaDescriptorDescriptor());

        parser.parse(config);

        ArooaDescriptorFactory descriptorFactory = (ArooaDescriptorFactory) parser.getRoot();

        ArooaDescriptor descriptor = descriptorFactory.createDescriptor(getClass().getClassLoader());

        DefaultConversionRegistry conversionRegistry = new DefaultConversionRegistry();

        descriptor.getConvertletProvider().registerWith(conversionRegistry);

        DefaultConverter converter = new DefaultConverter(conversionRegistry);

        GremlinSupplier gremlinSupplier = new GremlinSupplier();
        gremlinSupplier.setName("Gizmo");

        Gremlin gremlin = converter.convert(gremlinSupplier, Gremlin.class);

        assertThat(gremlin.getName(), is("Gizmo"));
    }

    @Test
    public void testWhyIsThereAConversionToStringArray() {

        ArooaSession session = new StandardArooaSession();

        ArooaConverter converter = session.getTools().getArooaConverter();

        ConversionPath<ConversionDescriptorBean, String[]> conversionPath =
                converter.findConversion(ConversionDescriptorBean.class, String[].class);

        // This just shouldn't happen!
        assertThat(conversionPath.toString(), is("ConversionDescriptorBean-Object-String[]"));
    }
}