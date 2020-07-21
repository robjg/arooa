package org.oddjob.arooa.utils;

import org.oddjob.arooa.ArooaException;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.parsing.ElementConfiguration;
import org.oddjob.arooa.parsing.NamespaceMappings;
import org.oddjob.arooa.xml.XMLArooaParser;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class RootConfigurationFileCreator {

	private final ArooaElement rootElement;

	private final NamespaceMappings namespaceMappings;

	public RootConfigurationFileCreator(ArooaElement rootElement, NamespaceMappings namespaceMappings) {
		this.rootElement = rootElement;
		this.namespaceMappings = namespaceMappings;
	}
	
	/**
	 * Check the file exists. If it doesn't create it with only 
	 * the root element. This allows an Oddjob to be created on
	 * a server where there might not be direct access to the file
	 * system.  
	 * 
	 * @param file
	 */
	public void createIfNone(File file) {
		if (file.exists()) {
			return;
		}
		
		try {
			XMLArooaParser xmlParser = new XMLArooaParser(namespaceMappings);
			xmlParser.parse(new ElementConfiguration(rootElement));			
			
			PrintWriter writer = new PrintWriter(new FileWriter(file));
			writer.print(xmlParser.getXml());
			writer.close();
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new ArooaException(e);
		}
	}
	
}
