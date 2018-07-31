/*
 * (c) Rob Gordon 2006
 */
package org.oddjob.arooa;

import java.net.URI;

import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.deploy.BeanDescriptorProvider;


/**
 * An ArooaDescriptor describes how an
 * {@link ArooaParser should process an 
 * {@link ArooaConfiguration}.
 *
 * @author Rob Gordon
 */
public interface ArooaDescriptor extends BeanDescriptorProvider {

	/**
	 * Provide type conversions.
	 * 
	 * @return A ConvertletProvider. May be null.
	 */
	public ConversionProvider getConvertletProvider();
	

	/**
	 * Provide element to class name mappings for components.
	 * 
	 * @return ElementMappings. Must not be null.
	 */
	public ElementMappings getElementMappings();
	
	/**
	 * Provide the default prefix for a URI. This is required when
	 * building an ArooaConfiguration using a ArooaDesigner so that
	 * the correct XML namespace mappings can defined.
	 * 
	 * @param namespace The namespace.
	 * @return The prefix.
	 */
	public String getPrefixFor(URI namespace);
	
	/**
	 * Provide a {@link ClassResolver}. The resolver will typically
	 * be able to resolve classes returned from the 
	 * {@link ElementMappings} provided by this descriptor.
	 * 
	 * @return A ClassResolver. Must not be null.
	 */
	public ClassResolver getClassResolver();
	
}
