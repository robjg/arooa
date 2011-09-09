/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.xml;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.Location;
import org.oddjob.arooa.runtime.ConfigurationNode;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * An {@link ArooaConfiguration} that wraps some XML data and
 * allows it to be parsed.
 * 
 * @author rob
 *
 */
public class XMLConfiguration implements ArooaConfiguration {

	/**
	 * Needed because the XML parser doesn't appear to close files after use.
	 */
	abstract class CloseableInputSource extends InputSource implements Closeable {
		
		public CloseableInputSource(InputStream in) {
			super(in);
		}
	}

	/**
	 * Encapsulate the source of the XML.
	 */
	interface SourceFactory {
		CloseableInputSource createInput() throws IOException;
		
		void save(ConfigurationNode rootConfigurationNode) throws ArooaParseException;
	}

	/** The source factory created on construction. */
	private final SourceFactory sourceFactory;
	
	/** The result of save being called on the parsed handle. */
	private String savedXml;
	
	/**
	 * Constructor for a file.
	 * 
	 * @param file 
	 */
	public XMLConfiguration(final File file) {
		if (file == null) {
			throw new NullPointerException();
		}
		sourceFactory = new SourceFactory() {
			@Override
			public CloseableInputSource createInput() throws IOException {
		        final InputStream inputStream = new FileInputStream(file);
		        
		        CloseableInputSource inputSource = new CloseableInputSource(inputStream) {
		        	@Override
		        	public void close() throws IOException {
		        		inputStream.close();
		        	}
		        };
		        
				inputSource.setSystemId(new File(file.getAbsolutePath()).toURI().toString());
				
				return inputSource;
			}
			@Override
			public void save(ConfigurationNode rootConfigurationNode) throws ArooaParseException {
				commonSave(rootConfigurationNode);
				try {
					
					PrintWriter out = new PrintWriter(new FileWriter((file)));
					out.print(savedXml);
					out.close();
					
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
			@Override
			public String toString() {
				return file.toString();
			}
		};
    }
	
	/**
	 * Constructor for Text.
	 * 
	 * @param systemId
	 * @param xml
	 */
	public XMLConfiguration(final String systemId, final String xml) {
		sourceFactory = new SourceFactory() {
			@Override
			public CloseableInputSource createInput() throws IOException {
		        final InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
		        CloseableInputSource inputSource = new CloseableInputSource(inputStream) {
		        	@Override
		        	public void close() throws IOException {
		        		inputStream.close();
		        	}
		        };
				inputSource.setSystemId(systemId);
				return inputSource;
			}			
			@Override
			public void save(ConfigurationNode rootConfigurationNode) throws ArooaParseException {
				commonSave(rootConfigurationNode);
			}
			@Override
			public String toString() {
				return systemId;
			}
		};
	}
	
	/**
	 * Constructor for a ClassLoader resource. Note that, like the
	 * underlying ClassLoader.getResourceAsStream, the resource does
	 * not have a '/' before the package name.
	 * <p>
	 * E.g. <code> new XMLConfiguration("org/oddjob/stuff/config.xml");
	 * </code>
	 *
	 * 
	 * @param resource
	 */
	public XMLConfiguration(final String resource, 
			final ClassLoader maybeClassLoader) {
		if (resource == null) {
			throw new NullPointerException();
		}
		sourceFactory = new SourceFactory() {
			@Override
			public CloseableInputSource createInput() throws IOException {
				ClassLoader classloader = maybeClassLoader;
				if (classloader == null) {
					classloader = getClass().getClassLoader();
				}
				
				final InputStream inputStream = classloader.getResourceAsStream(resource);
				
				if (inputStream == null) {
					throw new IOException("Can't find resource: " + resource);
				}
				
		        CloseableInputSource inputSource = new CloseableInputSource(inputStream) {
		        	@Override
		        	public void close() throws IOException {
		        		inputStream.close();
		        	}
		        };
				inputSource.setSystemId("/" + resource);
				return inputSource;
			}			
			@Override
			public void save(ConfigurationNode rootConfigurationNode) throws ArooaParseException {
				commonSave(rootConfigurationNode);
			}
			@Override
			public String toString() {
				return resource;
			}
		};
	}

	/**
	 * Constructor for an InputStream.
	 * 
	 * @param systemId
	 * @param in
	 */
	public XMLConfiguration(final String systemId, final InputStream in) {
		if (in == null) {
			throw new NullPointerException();
		}
		sourceFactory = new SourceFactory() {
			public CloseableInputSource createInput() throws IOException {
			    CloseableInputSource inputSource = new CloseableInputSource(in) {
			    	@Override
			    	public void close() throws IOException {
			    		in.close();
			    	}
			    };
				inputSource.setSystemId(systemId);
				return inputSource;
			}
			@Override			
			public void save(ConfigurationNode rootConfigurationNode) throws ArooaParseException {
				commonSave(rootConfigurationNode);
			}
			@Override
			public String toString() {
				return systemId;
			}
		};
	}
	
		
	/*
	 * (non-Javadoc)
	 * @see org.oddjob.arooa.ArooaConfiguration#parse(org.oddjob.arooa.parsing.ArooaContext)
	 */
	public ConfigurationHandle parse(ArooaContext parentContext) 
	throws ArooaParseException {
	
		CloseableInputSource inputSource = null;
		try {
			inputSource = sourceFactory.createInput();
		} catch (IOException e) {
			throw new ArooaException(e);
		}
		
    	final SAXHandler xmlHandler = new SAXHandler(parentContext);

        try {
            /**
             * SAX 2 style parser used to parse the given file.
             */
            XMLReader parser = JAXPUtils.getNamespaceXMLReader();

            parser.setContentHandler(xmlHandler);
            parser.setEntityResolver(xmlHandler);
            parser.setErrorHandler(xmlHandler);
            parser.setDTDHandler(xmlHandler);
            parser.parse(inputSource);
        } catch (SAXParseException exc) {
            Location location = new Location(exc.getSystemId(),
                exc.getLineNumber(), exc.getColumnNumber());

            Throwable t = exc.getException();
            if (t == null) {
                throw new ArooaParseException(exc.getMessage(), location, exc);                
            }
            if (t instanceof ArooaParseException) {
                ArooaParseException be = (ArooaParseException) t;
                throw be;
            }
            if (t instanceof ArooaException) {
                ArooaException ae = (ArooaException) t;
                throw new ArooaParseException(ae.getMessage(), location, ae);
            }
            throw new ArooaParseException(exc.getMessage(), location, t);
        } catch (SAXException exc) {
            throw new RuntimeException(exc);
        } catch (UnsupportedEncodingException exc) {
            Location location = new Location(inputSource.getSystemId(),
                    0, 0);
              throw new ArooaParseException("Encoding of input is invalid.", 
            		  location, exc);
        } catch (IOException exc) {
            Location location = new Location(inputSource.getSystemId(),
                    0, 0);
            throw new ArooaParseException("Error reading input.",
                      location, exc);
        } 
        finally {
        	try {
				inputSource.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
        }
        
        return new ConfigurationHandle() { 
        	public void save() throws ArooaParseException {
        		sourceFactory.save(
        				xmlHandler.getDocumentContext().getConfigurationNode());
        	}
        	
        	public ArooaContext getDocumentContext() {
        		return xmlHandler.getDocumentContext();
        	}
        };
	}

	void commonSave(ConfigurationNode rootConfigurationNode) throws ArooaParseException {
		if (rootConfigurationNode == null) {
			throw new NullPointerException("No Configuration To Save.");
		}
		
		XMLArooaParser parser = new XMLArooaParser();
		
		parser.parse(
				rootConfigurationNode);

		savedXml = parser.getXml();
	}
	
	/**
	 * Get the resultant XML from calling {@link ConfigurationHandle#save()}.
	 * 
	 * @return
	 */
	public String getSavedXml() {
		return savedXml;
	}
	
	@Override
	public String toString() {
		return sourceFactory.toString();
	}
}
