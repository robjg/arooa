package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.beandocs.element.BeanDocElement;

import java.util.List;

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

	List<BeanDocElement> getFirstSentence();

	List<BeanDocElement> getAllText();
	
	ConfiguredHow getConfiguredHow();
	
	boolean isAuto();
	
	Access getAccess();
	
	Multiplicity getMultiplicity();
	
	String getRequired();
}
