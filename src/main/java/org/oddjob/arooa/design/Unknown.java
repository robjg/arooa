package org.oddjob.arooa.design;

/**
 * A design where the actual form of the design is unknown. This is usually because it
 * couldn't be parsed.
 */
public interface Unknown {

	void setXml(String xml);

	String getXml();
	
}
