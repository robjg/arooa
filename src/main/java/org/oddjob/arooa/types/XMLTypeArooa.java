package org.oddjob.arooa.types;

import org.oddjob.arooa.ArooaAnnotations;
import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ConfiguredHow;
import org.oddjob.arooa.ParsingInterceptor;
import org.oddjob.arooa.xml.XMLInterceptor;

import javax.inject.Qualifier;

public class XMLTypeArooa implements ArooaBeanDescriptor{

	@Override
	public ParsingInterceptor getParsingInterceptor() {
		return new XMLInterceptor("xml");
	}

	@Override
	public String getComponentProperty() {
		return null;
	}

	@Override
	public ConfiguredHow getConfiguredHow(String property) {
		return null;
	}

	@Override
	public String getTextProperty() {
		return null;
	}

	@Override
	public String getFlavour(String property) {
		return null;
	}

	@Override
	public Qualifier getQualifier(String property) {
		return null;
	}

	@Override
	public boolean isAuto(String property) {
		return false;
	}
	
	@Override
	public ArooaAnnotations getAnnotations() {
		return null;
	}
}
