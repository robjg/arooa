package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.ConfiguredHow;

import java.util.Objects;

/**
 * Allows documentation for a property to be accumulated via setters.
 */
public class WriteablePropertyDoc implements PropertyDoc {

	private String propertyName;
	
	private String firstSentence;
	
	private String allText;
	
	private ConfiguredHow configuredHow;
	
	private Access access;
	
	private Multiplicity multiplicity;
	
	private boolean auto;
	
	private boolean advanced;
	
	private String required;
	
	@Override
	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	
	@Override
	public String getFirstSentence() {
		return firstSentence;
	}

	public void setFirstSentence(String firstLine) {
		this.firstSentence = firstLine;
	}
	
	@Override
	public String getAllText() {
		return allText;
	}

	public void setAllText(String allText) {
		this.allText = allText;
	}
	
	@Override
	public Access getAccess() {
		return access;
	}
	
	public void setAccess(Access access) {
		this.access = access;
	}
	
	@Override
	public ConfiguredHow getConfiguredHow() {
		return configuredHow;
	}

	public void setConfiguredHow(ConfiguredHow configuredHow) {
		this.configuredHow = Objects.requireNonNull(configuredHow);
	}
	
	@Override
	public Multiplicity getMultiplicity() {
		return multiplicity;
	}
	
	public void setMultiplicity(Multiplicity multiplicity) {
		this.multiplicity = multiplicity;
	}
	
	@Override
	public boolean isAuto() {
		return auto;
	}
	
	public void setAuto(boolean auto) {
		this.auto = auto;
	}
	
	public boolean isAdvanced() {
		return advanced;
	}
	
	public void setAdvanced(boolean advanced) {
		this.advanced = advanced;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}
}
