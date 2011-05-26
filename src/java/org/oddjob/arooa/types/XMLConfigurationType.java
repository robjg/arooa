package org.oddjob.arooa.types;

import java.io.File;
import java.io.InputStream;

import javax.inject.Inject;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.xml.XMLConfiguration;

/**
 * @oddjob.description Provide Configuration in XML format.
 * 
 * @author rob
 *
 */
public class XMLConfigurationType implements ValueFactory<ArooaConfiguration> {

	public static final ArooaElement ELEMENT = new ArooaElement("config");

	/**
	 * @oddjob.property 
	 * @oddjob.description A file containing the configuration.
	 * @oddjob.required No.
	 */
	private File file;
	
	/**
	 * @oddjob.property 
	 * @oddjob.description The name of a class path resource containing
	 * the configuration.
	 * @oddjob.required No.
	 */
	private String resource;
	
	/**
	 * @oddjob.property 
	 * @oddjob.description The configuration as embedded XML text.
	 * @oddjob.required No.
	 */
	private String xml;
	
	/**
	 * @oddjob.property 
	 * @oddjob.description An input stream containing the configuration.
	 * @oddjob.required No.
	 */
	private InputStream input;
	
	private ClassLoader classLoader;
	
	@Override
	public ArooaConfiguration toValue() {
		
		if (file != null) {
			return new XMLConfiguration(file);			
		}
		else if (resource != null) {
			return new XMLConfiguration(resource, classLoader);
		}
		else if (input != null) {
			return new XMLConfiguration("InputStream", input);			
		}
		else if (xml != null) {
			return new XMLConfiguration("EmbeddedXML", xml);
		}
		else {
			return null;
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getXml() {
		return xml;
	}

	@org.oddjob.arooa.deploy.annotations.ArooaElement
	public void setXml(String xml) {
		this.xml = xml;
	}

	public InputStream getInput() {
		return input;
	}

	public void setInput(InputStream inputStream) {
		this.input = inputStream;
	}
	
	@Inject
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
}
