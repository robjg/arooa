package org.oddjob.arooa.parsing;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.ConfigurationHandle;
import org.oddjob.arooa.runtime.RuntimeConfiguration;
import org.oddjob.arooa.runtime.ConfigurationNode;

/**
 * A Context for the parsing. This context is kind of like a
 * seed context from which everything grows. It has no 
 * RuntimeConfiguration or RuntimeNode as these require the
 * document node of the configuration to be parsed.
 * 
 * @author rob
 *
 */
public class RootContext implements ArooaContext {

	private final ArooaSession session;
	private final ArooaHandler rootHandler;
	
	private final ArooaType type;
	
	private final PrefixMappings prefixMappings = new SimplePrefixMappings();
		
	/** Configuration node is needed to track children. */
	private final ConfigurationNode configurationNode = 
		new AbstractConfigurationNode() {

			public ArooaContext getContext() {
				return RootContext.this;
			}
		
			public void addText(String text) {
				throw new UnsupportedOperationException("Should be Impossible!");
			}

			public ConfigurationHandle parse(ArooaContext parentContext)
					throws ArooaParseException {
				throw new UnsupportedOperationException("Should be Impossible!");
			}
	};
	
	/**
	 * Constructor.
	 * 
	 * @param type The type. May be null depending on parser 
	 * (XMLArooaParser for instance).
	 * @param session The session. Must be null depending on parser.
	 * @param rootHandler The handler. Must not be null.
	 */
	public RootContext(
			ArooaType type,
			ArooaSession session, 
			ArooaHandler rootHandler) {

		if (rootHandler == null) {
			throw new NullPointerException("No Handler");
		}
		
		this.type = type;
		this.rootHandler = rootHandler;
		this.session = session;
	}

	public ArooaType getArooaType() {
		return type;
	}
	
	public ArooaContext getParent() {
		return null;
	}
	
	public RuntimeConfiguration getRuntime() {
		return null;
	}
	
	public PrefixMappings getPrefixMappings() {
		return prefixMappings;
	}
	
	public ArooaSession getSession() {
		return session;
	}
	
	public ConfigurationNode getConfigurationNode() {
		return configurationNode;
	}
	
	public ArooaHandler getArooaHandler() {
		return rootHandler;
	}
}
