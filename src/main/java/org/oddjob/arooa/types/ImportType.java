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
 * @oddjob.description Import XML which is processed as if it's
 * in-line.
 * 
 * @oddjob.example
 * 
 * Using import for a file list. the variables pathA and pathB are identical.
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ImportExample.xml}
 * 
 * The imported file is:
 * 
 * {@oddjob.xml.resource org/oddjob/arooa/types/ImportExampleImport.xml}
 * 
 * @author rob
 *
 */
public class ImportType implements ArooaValue, ArooaSessionAware {

	public static final ArooaElement ELEMENT = new ArooaElement("import"); 
	
	private String resource;
	
	private File file;

	private InputStream input;
	
	private String xml;

	private ArooaSession session;
	
	public static class Conversions implements ConversionProvider {
		
		public void registerWith(ConversionRegistry registry) {
			registry.registerJoker(ImportType.class,
                    new Joker<>() {
                        public <T> ConversionStep<ImportType, T> lastStep(
                                Class<? extends ImportType> from,
                                final Class<T> to,
                                ConversionLookup conversions) {

                            return new ConversionStep<>() {
                                public Class<ImportType> getFromClass() {
                                    return ImportType.class;
                                }

                                public Class<T> getToClass() {
                                    return to;
                                }

                                public T convert(ImportType from, ArooaConverter converter)
                                        throws ArooaConversionException {
                                    try {
                                        return converter.convert(from.toObject(), to);
                                    } catch (Exception e) {
                                        throw new ArooaConversionException(e);
                                    }
                                }
                            };
                        }
                    });
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
		}
		else if (file != null) {
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
