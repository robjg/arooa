package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaConfiguration;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.registry.ChangeHow;
import org.oddjob.arooa.xml.XMLArooaParser;

/**
 * A {@link DragPoint} for an {@link ArooaConfiguration}.
 * <p>
 * This DragPoint only has enough implementation to support the Designer for
 * the configuration. It won't support drag and drop within the configuration.
 * That will be supported by the Design View once the configuration is
 * parsed.
 * 
 * @author rob
 *
 */
public class DragConfiguration implements DragPoint {

	private final ArooaConfiguration configuration;
	
	public DragConfiguration(ArooaConfiguration configuration) {
		this.configuration = configuration;
	}

	public DragTransaction beginChange(ChangeHow how) {
		// Changing a configuration - transaction doesn't apply.
		return new DragTransaction() {
			public void commit() {
			}
			public void rollback() {
			}
		};
	}
	
	public ConfigurationHandle parse(ArooaContext parentContext)
			throws ArooaParseException {
		return configuration.parse(parentContext);
	}
	
	public boolean supportsPaste() {
		return false;
	}
	
	public boolean supportsCut() {
		return false;
	}
	
	public String copy() {
		XMLArooaParser xmlParser = new XMLArooaParser();
		
		try {
			xmlParser.parse(configuration);
		}
		catch (ArooaParseException e) {
			throw new RuntimeException(e);
		}
		return xmlParser.getXml();
	}
	
	public void cut() {
		throw new UnsupportedOperationException("Check supportsCut first!");
	}
	
	public void paste(int index, String config) {
		throw new UnsupportedOperationException("Check supportsPaste first!");
	}

}
