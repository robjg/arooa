package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.ConfiguredHow;

/**
 * Provides the documentation for the property of a bean.
 *
 * @see BeanDoc
 */
public interface PropertyDoc {

	enum Access {
		READ_ONLY,
		WRITE_ONLY,
		READ_WRITE,
	}
		
	enum Multiplicity {
		SIMPLE,
		INDEXED,
		MAPPED,
	}
	
	String getPropertyName();
	
	String getFirstSentence();
	
	String getAllText();
	
	ConfiguredHow getConfiguredHow();
	
	boolean isAuto();
	
	Access getAccess();
	
	Multiplicity getMultiplicity();
	
	String getRequired();
}
