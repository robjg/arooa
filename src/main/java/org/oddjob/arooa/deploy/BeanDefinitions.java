package org.oddjob.arooa.deploy;

import java.net.URI;
import java.util.List;

/**
 * Provide a set of element to class name 
 * mappings. A name space can be provided which apples to 
 * all elements.
 * 
 * @author rob
 *
 */
public class BeanDefinitions {

	/** 
	 * The name space that applies to 
     * all elements defined in definitions.
	 */
	private final URI namespace;
	
	/** 
     * The default prefix for the name space.
	 */
	private final String prefix;
	
	/** 
     * A list of {@link BeanDefinitionBean}s.
	 */
	private final List<BeanDefinitionBean> definitions;
	
	public BeanDefinitions(URI namespace, String prefix, 
			List<BeanDefinitionBean> defitions) {
		this.namespace = namespace;
		this.prefix = prefix;
		this.definitions = defitions;
	}
	
	
	public BeanDefinitionBean[] getDefinitions() {
		return definitions.toArray(new BeanDefinitionBean[0]);
	}

	public URI getNamespace() {
		return namespace;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
}
