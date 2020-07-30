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
import org.oddjob.arooa.utils.FileUtils;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.xmlunit.matchers.CompareMatcher;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;

public class FormsLookupFromDescriptorTest {

    @Test
    public void testFormFromConfiguration() throws ArooaParseException, IOException, URISyntaxException {

        URL descriptorUrl = Objects.requireNonNull(
                DesignerForEverythingMain.class.getResource("ThingDescriptor.xml"));

        ArooaDescriptor descriptor = new URLDescriptorFactory(descriptorUrl)
                .createDescriptor(DesignerForEverythingMain.class.getClassLoader());

        File configFile = new File(DesignerForEverythingMain.class.getResource("ThingConfig.xml").getFile());

        XMLConfiguration config = new XMLConfiguration(configFile);

        FormsLookupFromDescriptor test = new FormsLookupFromDescriptor(
                new ListDescriptor(new ArooaDescriptorDescriptor(), descriptor));

        ArooaConfiguration result = test.formFor(config);

        XMLArooaParser xmlParser = new XMLArooaParser(FormsLookup.formsNamespaces());
        xmlParser.parse(result);

        String expected = FileUtils.readToString(
                getClass().getResource("FormsLookupFromConfigurationExpected.xml"));

        assertThat(xmlParser.getXml(), CompareMatcher.isSimilarTo(expected));
    }

    @Test
    public void testBlankFormForComponent() throws ArooaParseException, IOException, URISyntaxException {

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

        String expected = FileUtils.readToString(
                getClass().getResource("FormsLookupBlankComponentExpected.xml"));

        assertThat(xmlParser.getXml(), CompareMatcher.isSimilarTo(expected));
    }

    @Test
    public void testBlankFormForIs() throws ArooaParseException, IOException, URISyntaxException {

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

        String expected = FileUtils.readToString(
                getClass().getResource("FormsLookupBlankIsExpected.xml"));

        assertThat(xmlParser.getXml(), CompareMatcher.isSimilarTo(expected));
    }

    @Test
    public void testBlankFormForBean() throws ArooaParseException, IOException, URISyntaxException {

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

        String expected = FileUtils.readToString(
                getClass().getResource("FormsLookupBlankBeanExpected.xml"));

        assertThat(xmlParser.getXml(), CompareMatcher.isSimilarTo(expected));
    }
}