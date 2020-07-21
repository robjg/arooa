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

	private final NamespaceMappings namespaceMappings;

	public DragConfiguration(ArooaConfiguration configuration, NamespaceMappings namespaceMappings) {
		this.configuration = configuration;
		this.namespaceMappings = namespaceMappings;
	}

	@Override
	public DragTransaction beginChange(ChangeHow how) {
		// Changing a configuration - transaction doesn't apply.
		return new DragTransaction() {
			@Override
			public void commit() {
			}
			@Override
			public void rollback() {
			}
		};
	}

	@Override
	public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
			throws ArooaParseException {
		return configuration.parse(parentContext);
	}

	@Override
	public boolean supportsPaste() {
		return false;
	}

	@Override
	public boolean supportsCut() {
		return false;
	}

	@Override
	public String copy() {
		XMLArooaParser xmlParser = new XMLArooaParser(namespaceMappings);
		
		try {
			xmlParser.parse(configuration);
		}
		catch (ArooaParseException e) {
			throw new RuntimeException(e);
		}
		return xmlParser.getXml();
	}

	@Override
	public void cut() {
		throw new UnsupportedOperationException("Check supportsCut first!");
	}

	@Override
	public void paste(int index, String config) {
		throw new UnsupportedOperationException("Check supportsPaste first!");
	}

}
