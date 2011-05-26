package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.xml.XMLInterceptor;

public class XMLTypeArooa implements ArooaBeanDescriptor{

	public ParsingInterceptor getParsingInterceptor() {
		return new XMLInterceptor("xml");
	}
	
	public String getComponentProperty() {
		return null;
	}
	
	public ConfiguredHow getConfiguredHow(String property) {
		return null;
	}
	
	public String getTextProperty() {
		return null;
	}
	
	public String getFlavour(String property) {
		return null;
	}
	
	public boolean isAuto(String property) {
		return false;
	}
}
