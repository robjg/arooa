package org.oddjob.arooa.xml;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.OurDirs;
import org.oddjob.arooa.*;
import org.oddjob.arooa.deploy.NoAnnotations;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.CutAndPasteSupport;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.xmlunit.matchers.CompareMatcher.isSimilarTo;


public class XMLConfigurationFileTest {

    private static final Logger logger = LoggerFactory.getLogger(XMLConfigurationFileTest.class);

    private Path file;

    @Rule
    public TestName name = new TestName();

    public String getName() {
        return name.getMethodName();
    }

    @Before
    public void setUp() throws IOException {

        logger.info("----------------------   " + getName() + "   -------------------------");

        Path workDir = OurDirs.workPathDir(getClass().getSimpleName(),
                                           true);
        this.file = workDir.resolve("XMLConfigurationFileText.xml");

        Files.write(this.file, "<snack/>".getBytes());
    }

    private final String EOL = System.lineSeparator();

    /*
     * Check the configuration is re-readable.
     */
    @Test
    public void testReParse() throws Exception {

        XMLConfiguration config = new XMLConfiguration(file.toFile());

        XMLArooaParser parser = new XMLArooaParser();

        parser.parse(config);

        assertThat(parser.getXml(), isSimilarTo("<snack/>" + EOL));

        parser = new XMLArooaParser();

        // should force re-read of the file.
        parser.parse(config);

        assertThat(parser.getXml(), isSimilarTo("<snack/>" + EOL));
    }

    public static class Snack {
        String stuff;

        public void setStuff(String stuff) {
            this.stuff = stuff;
        }
    }

    public static class SnackArooa extends MockArooaBeanDescriptor {

        @Override
        public ConfiguredHow getConfiguredHow(String property) {
            return ConfiguredHow.ELEMENT;
        }

        @Override
        public ParsingInterceptor getParsingInterceptor() {
            return null;
        }

        @Override
        public String getComponentProperty() {
            return null;
        }

        @Override
        public boolean isAuto(String property) {
            return false;
        }

        @Override
        public ArooaAnnotations getAnnotations() {
            return new NoAnnotations();
        }
    }


    @Test
    public void testChangeAndSave() throws ArooaParseException, SAXException, IOException {

        XMLConfiguration config = new XMLConfiguration(file.toFile());

        Snack root = new Snack();

        StandardArooaParser parser = new StandardArooaParser(root);

        ConfigurationHandle handle = parser.parse(config);

        ArooaContext context = parser.getSession().getComponentPool().contextFor(root);

        String pasteXML = "<stuff><value value='apple'/></stuff>";

        CutAndPasteSupport.paste(
                context,
                0,
                new XMLConfiguration("Paste XML", pasteXML));

        context.getRuntime().configure();

        assertEquals("apple", root.stuff);

        handle.save();

        XMLConfiguration configCheck = new XMLConfiguration(file.toFile());

        XMLArooaParser xmlParser = new XMLArooaParser();

        xmlParser.parse(configCheck);

        String expected =
                "<snack>" + EOL +
                        "    <stuff>" + EOL +
                        "        <value value=\"apple\"/>" + EOL +
                        "    </stuff>" + EOL +
                        "</snack>" + EOL;

        assertThat(xmlParser.getXml(), isSimilarTo(expected));

    }

    @Test
    public void testChangeRoot() throws Exception {

        XMLConfiguration config = new XMLConfiguration(file.toFile());

        Snack root = new Snack();

        StandardArooaParser parser = new StandardArooaParser(root);

        ConfigurationHandle handle = parser.parse(config);

        ArooaContext context = parser.getSession().getComponentPool().contextFor(root);

        context.getRuntime().destroy();

        String pasteXML = "<meal/>";

        CutAndPasteSupport.paste(
                context.getParent(),
                0,
                new XMLConfiguration("Paste XML", pasteXML));

        handle.save();

        XMLConfiguration configCheck = new XMLConfiguration(file.toFile());

        XMLArooaParser xmlParser = new XMLArooaParser();

        xmlParser.parse(configCheck);

        String expected =
                "<meal/>" + EOL;

        assertThat(xmlParser.getXml(), isSimilarTo(expected));
    }

}
