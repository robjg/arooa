package org.oddjob.arooa.beandocs;

import org.oddjob.arooa.ConfiguredHow;

public interface PropertyDoc {

	enum Access {
		READ_ONLY,
		WRITE_ONLY,
		READ_WRITE,
		;
	}
		
	enum Multiplicity {
		SIMPLE,
		INDEXED,
		MAPPED,
		;
	}
	
	public String getPropertyName();
	
	public String getFirstSentence();
	
	public String getAllText();
	
	public ConfiguredHow getConfiguredHow();
	
	public boolean isAuto();
	
	public Access getAccess();
	
	public Multiplicity getMultiplicity();
	
	public String getRequired();
}
