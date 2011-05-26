/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.runtime;

/**
 * Allow different behaviour during substitution. Intended to
 * allow different handling of null values.
 *
 */
public interface SubstitutionPolicy {

	public <T> T substituteObject(T value) throws SubstitutionException;

	public String substituteString(String value) throws SubstitutionException;
}
