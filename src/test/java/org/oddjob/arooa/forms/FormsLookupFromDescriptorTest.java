package org.oddjob.arooa.forms;

import org.junit.Test;
import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.deploy.ArooaDescriptorDescriptor;
import org.oddjob.arooa.deploy.ListDescriptor;
import org.oddjob.arooa.deploy.URLDescriptorFactory;
import org.oddjob.arooa.design.layout.DesignerForEverythingMain;
import org.oddjob.arooa.xml.XMLArooaParser;

import java.net.URL;
import java.util.Objects;

public class FormsLookupFromDescriptorTest {

    @Test
    public void testComponent() throws ArooaParseException {

        URL descriptorUrl = Objects.requireNonNull(
                FormsLookupFromDescriptorTest.class.getResource("FruitDescriptor.xml"));

        ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
                .createDescriptor(DesignerForEverythingMain.class.getClassLoader());

        FormsLookupFromDescriptor test = new FormsLookupFromDescriptor(
                new ListDescriptor(new ArooaDescriptorDescriptor(), descriptor));

        ArooaConfiguration result = test.blankForm(ArooaType.COMPONENT,
                "fruit:apple-bag", Apple.class.getName());

        XMLArooaParser xmlParser = new XMLArooaParser(FormsLookup.formsNamespaces());
        xmlParser.parse(result);

        System.out.println(xmlParser.getXml());

    }

    @Test
    public void testIs() throws ArooaParseException {

        URL descriptorUrl = Objects.requireNonNull(
                FormsLookupFromDescriptorTest.class.getResource("FruitDescriptor.xml"));

        ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
                .createDescriptor(DesignerForEverythingMain.class.getClassLoader());

        FormsLookupFromDescriptor test = new FormsLookupFromDescriptor(
                new ListDescriptor(new ArooaDescriptorDescriptor(), descriptor));

        ArooaConfiguration result = test.blankForm(ArooaType.VALUE,
                "is", Apple.class.getName());

        XMLArooaParser xmlParser = new XMLArooaParser(FormsLookup.formsNamespaces());
        xmlParser.parse(result);

        System.out.println(xmlParser.getXml());

    }

    @Test
    public void testBean() throws ArooaParseException {

        URL descriptorUrl = Objects.requireNonNull(
                FormsLookupFromDescriptorTest.class.getResource("FruitDescriptor.xml"));

        ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
                .createDescriptor(DesignerForEverythingMain.class.getClassLoader());

        FormsLookupFromDescriptor test = new FormsLookupFromDescriptor(
                new ListDescriptor(new ArooaDescriptorDescriptor(), descriptor));

        ArooaConfiguration result = test.blankForm(ArooaType.VALUE,
                "bean", Apple.class.getName());

        XMLArooaParser xmlParser = new XMLArooaParser(FormsLookup.formsNamespaces());
        xmlParser.parse(result);

        System.out.println(xmlParser.getXml());

    }
}