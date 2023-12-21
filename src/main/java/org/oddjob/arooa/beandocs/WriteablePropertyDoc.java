package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.List;
import java.util.Objects;

/**
 * Allows documentation for a property to be accumulated via setters.
 */
public class WriteablePropertyDoc implements PropertyDoc {

	private String propertyName;
	
	private List<BeanDocElement> firstSentence;
	
	private List<BeanDocElement> allText;
	
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
	public List<BeanDocElement> getFirstSentence() {
		return firstSentence;
	}

	public void setFirstSentence(List<BeanDocElement> firstLine) {
		this.firstSentence = firstLine;
	}
	
	@Override
	public List<BeanDocElement> getAllText() {
		return allText;
	}

	public void setAllText(List<BeanDocElement> allText) {
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
