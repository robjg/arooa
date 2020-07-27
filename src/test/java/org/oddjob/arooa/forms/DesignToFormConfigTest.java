package org.oddjob.arooa.forms;

import org.junit.Test;
import org.oddjob.arooa.*;
import org.oddjob.arooa.deploy.ArooaDescriptorDescriptor;
import org.oddjob.arooa.deploy.ListDescriptor;
import org.oddjob.arooa.deploy.URLDescriptorFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.layout.DesignerForEverythingMain;
import org.oddjob.arooa.standard.StandardArooaSession;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class DesignToFormConfigTest {

    @Test
    public void test() throws ArooaParseException {


        URL descriptorUrl = Objects.requireNonNull(
                DesignerForEverythingMain.class.getResource("ThingDescriptor.xml"));

        ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
                .createDescriptor(DesignerForEverythingMain.class.getClassLoader());

        ArooaSession session = new StandardArooaSession(
                new ListDescriptor(new ArooaDescriptorDescriptor(), descriptor));

        File configFile = new File(DesignerForEverythingMain.class.getResource("ThingConfig.xml").getFile());

        XMLConfiguration config = new XMLConfiguration(configFile);

        DesignParser parser = new DesignParser(session);
        parser.setArooaType(ArooaType.COMPONENT);

        parser.parse(config);

        DesignInstance designInstance = parser.getDesign();

        DesignToFormConfig test = new DesignToFormConfig();

        ArooaConfiguration result = test.configurationFor(designInstance);

        XMLArooaParser xmlParser = new XMLArooaParser(FormsLookup.formsNamespaces());
        xmlParser.parse(result);

        System.out.println(xmlParser.getXml());

    }


    @Test
    public void testBean() throws ArooaParseException {


        URL descriptorUrl = Objects.requireNonNull(
                DesignToFormConfigTest.class.getResource("FruitDescriptor.xml"));

        ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
                .createDescriptor(DesignerForEverythingMain.class.getClassLoader());

        ArooaSession session = new StandardArooaSession(
                new ListDescriptor(new ArooaDescriptorDescriptor(), descriptor));

        File configFile = new File(DesignToFormConfigTest
                .class.getResource("AppleBagConfig.xml").getFile());

        XMLConfiguration config = new XMLConfiguration(configFile);

        DesignParser parser = new DesignParser(session);
        parser.setArooaType(ArooaType.COMPONENT);

        parser.parse(config);

        DesignInstance designInstance = parser.getDesign();

        DesignToFormConfig test = new DesignToFormConfig();

        ArooaConfiguration result = test.configurationFor(designInstance);

        XMLArooaParser xmlParser = new XMLArooaParser(FormsLookup.formsNamespaces());
        xmlParser.parse(result);

        System.out.println(xmlParser.getXml());

    }


}