package org.oddjob.arooa.beanutils;

/**
 * @oddjob.description A property of a {@link MagicBeanDefinition}.
 * 
 * @author rob
 *
 */
public class MagicBeanProperty {

	/**
	 * @oddjob.property
	 * @oddjob.description The name of the property.
	 * @oddjob.required Yes.
	 */
	private String name;
	
	/**
	 * @oddjob.property
	 * @oddjob.description The class type of the property.
	 * @oddjob.required No. Defaults to String.
	 */
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
