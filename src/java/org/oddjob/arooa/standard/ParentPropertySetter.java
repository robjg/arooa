package org.oddjob.arooa.standard;

import org.oddjob.arooa.reflect.ArooaPropertyException;

interface ParentPropertySetter {

	void parentSetProperty(Object value)
	throws ArooaPropertyException;
}
