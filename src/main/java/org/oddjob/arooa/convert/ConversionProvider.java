/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa.convert;


/**
 * Something that can provide a Conversion. It does so by registering its
 * conversion in a {@link ConversionRegistry}.
 */
public interface ConversionProvider {

	void registerWith(ConversionRegistry registry);
}
