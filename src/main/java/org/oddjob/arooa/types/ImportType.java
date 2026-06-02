package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaValue;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.convert.*;
import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaHidden;
import org.oddjob.arooa.life.ArooaSessionAware;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.xml.XMLConfiguration;

import java.io.*;
import java.net.URL;

/**
 * @author rob
 * @oddjob.description Import XML which is processed as if it's
 * in-line.
 * @oddjob.example Using import for a file list. The variables pathA and pathB are identical.
 * <p>
 * {@oddjob.xml.resource org/oddjob/arooa/types/ImportExample.xml}
 * <p>
 * The imported file is:
 * <p>
 * {@oddjob.xml.resource org/oddjob/arooa/types/ImportExampleImport.xml}
 *
 */
public class ImportType implements ArooaValue, ArooaSessionAware {

    public static final ArooaElement ELEMENT = new ArooaElement("import");

    private String resource;

    private File file;

    private InputStream input;

    private String xml;

    private ArooaSession session;

    static class ImportTypeJoker implements Joker<ImportType> {

        @Override
        public <T> ConversionStep<ImportType, T> lastStep(ConversionPath<?, ImportType> pathBefore,
                                                          TypeArooa<ImportType> from,
                                                          TypeArooa<T> to,
                                                          ConversionLookup conversions) {

            return new ConversionStep<>() {

                @Override
                public TypeArooa<ImportType> getFromType() {
                    return from;
                }

                @Override
                public TypeArooa<T> getToType() {
                    return to;
                }

                public T convert(ImportType from, ArooaConverter converter)
                        throws ArooaConversionException {
                    try {
                        return converter.convert(from.toObject(), to.getType());
                    } catch (Exception e) {
                        throw new ArooaConversionException(e);
                    }
                }
            };
        }
    }

    public static class Conversions implements ConversionProvider {

        public void registerWith(ConversionRegistry registry) {
            registry.registerJoker(ImportType.class,
                    new ImportTypeJoker());
        }
    }

    @ArooaHidden
    public void setArooaSession(ArooaSession session) {
        this.session = session;
    }

    public Object toObject() throws IOException {

        XMLConfiguration config;
        if (resource != null) {
            ClassResolver classFest = session.getArooaDescriptor().getClassResolver();
            URL url = classFest.getResource(resource);
            if (url == null) {
                throw new IOException("No such resource " + resource);
            }
            InputStream input = url.openStream();
            if (input == null) {
                throw new NullPointerException(resource + " does not exist.");
            }
            config = new XMLConfiguration(resource, input);
        } else if (file != null) {
            try {
                config = new XMLConfiguration(file.toString(),
                        new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else if (input != null) {
            config = new XMLConfiguration("InputStream",
                    input);
        } else if (xml != null) {
            config = new XMLConfiguration("TextString",
                    xml);
        } else {
            throw new IllegalStateException("Nothing to import.");
        }

        StandardFragmentParser parser = new StandardFragmentParser(session);

        try {
            parser.parse(config);
        } catch (ArooaParseException e) {
            throw new RuntimeException(e);
        }

        return parser.getRoot();
    }


    public String getResource() {
        return resource;
    }

    /**
     * @oddjob.property resource
     * @oddjob.description A resource file on the classpath.
     * @oddjob.required No.
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    public File getFile() {
        return file;
    }

    /**
     * @oddjob.property file
     * @oddjob.description A file.
     * @oddjob.required No.
     */
    @ArooaAttribute
    public void setFile(File file) {
        this.file = file;
    }

    public InputStream getInput() {
        return input;
    }

    /**
     * @oddjob.property input
     * @oddjob.description An input stream.
     * @oddjob.required No.
     */
    public void setInput(InputStream input) {
        this.input = input;
    }

    public String getXml() {
        return xml;
    }

    /**
     * @oddjob.property xml
     * @oddjob.description XML as text.
     * @oddjob.required No.
     */
    public void setXml(String xml) {
        this.xml = xml;
    }
}
