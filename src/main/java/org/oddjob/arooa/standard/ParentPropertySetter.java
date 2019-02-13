package org.oddjob.arooa.standard;

import org.oddjob.arooa.reflect.ArooaPropertyException;

/**
 * Used by various Standard Parsing Runtime do do
 */
interface ParentPropertySetter {

	void parentSetProperty(Object value)
	throws ArooaPropertyException;
}
