package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.net.URL;
import java.util.Objects;

/**
 * An {@link ArooaDescriptor} for a {@link ArooaConfiguration}
 * that provides and {@link ArooaDescriptorBean}.
 *
 * @author rob
 */
public class ArooaDescriptorDescriptorFactory implements ArooaDescriptorFactory {


    public ArooaDescriptor createDescriptor(ClassLoader classLoader) {

        URL descriptorDescriptor = Objects.requireNonNull(
                classLoader.getResource("org/oddjob/arooa/deploy/descriptor.xml"),
                "No Descriptor Descriptor.");

        URL designDescriptor = Objects.requireNonNull(
                classLoader.getResource("org/oddjob/arooa/design/descriptor.xml"),
                "No Descriptor Descriptor.");

        ListDescriptor listDescriptor =
                new ListDescriptor();

        listDescriptor.addDescriptor(descriptorFromUrl(descriptorDescriptor, classLoader));
        listDescriptor.addDescriptor(descriptorFromUrl(designDescriptor, classLoader));

        return listDescriptor;
    }

    public static ArooaDescriptor descriptorFromUrl(URL url, ClassLoader classLoader) {

        XMLConfiguration config = new XMLConfiguration(url);

        StandardFragmentParser parser = new StandardFragmentParser();

        try {
            parser.parse(config);
        } catch (ArooaParseException e) {
            throw new RuntimeException(e);
        }

        ArooaDescriptorFactory descriptorFactory =
                (ArooaDescriptorFactory) parser.getRoot();

        return descriptorFactory.createDescriptor(classLoader);
    }
}
