package org.oddjob.arooa.deploy;

import org.oddjob.arooa.ArooaBeanDescriptor;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ClassResolver;
import org.oddjob.arooa.ElementMappings;
import org.oddjob.arooa.convert.ConversionProvider;
import org.oddjob.arooa.reflect.ArooaClass;
import org.oddjob.arooa.reflect.PropertyAccessor;

import java.net.URI;

/**
 * Combine two {@link ArooaDescriptor}s. The two descriptors are 
 * treated as a primary and a secondary. Combination is as follows:
 * <ul>
 *  <li>Both primary and secondary conversions are registered with 
 *  primary conversions overriding any identical secondary conversions.</li>
 *
 *  <li>First a primary then a secondary BeanDescriptor is sought.
 *  <em>Should BeanDescriptors be chained to allow 'global' intercepts
 *  to be applied?</em></li>
 *  
 *  <li>Component and Value mappings are searched such that any match
 *  in the primary is used before searching the secondary.</li>
 * </ul>
 *  
 * @author rob
 *
 */
public class LinkedDescriptor implements ArooaDescriptor {

	private final ArooaDescriptor delegate;
	
	/**
	 * Constructor.
	 * 
	 * @param primary
	 * @param secondary
	 */
	public LinkedDescriptor(ArooaDescriptor primary,
			ArooaDescriptor secondary) {
		this.delegate = new ListDescriptor(
				new ArooaDescriptor[] { secondary, primary });
	}

	@Override
	public ConversionProvider getConvertletProvider() {
		
		return delegate.getConvertletProvider();
		
	}
	
	@Override
	public ArooaBeanDescriptor getBeanDescriptor(
			ArooaClass forClass, PropertyAccessor accessor) {
		
		return delegate.getBeanDescriptor(forClass, accessor);
	}
	
	@Override
	public ElementMappings getElementMappings() {

		return delegate.getElementMappings();
	}
	
	@Override
	public String getPrefixFor(URI namespace) {
		return delegate.getPrefixFor(namespace);
	}

	@Override
	public String[] getPrefixes() {
		return delegate.getPrefixes();
	}

	@Override
	public URI getUriFor(String prefix) {
		return delegate.getUriFor(prefix);
	}

	@Override
	public ClassResolver getClassResolver() {
		return delegate.getClassResolver();	
	}	
}
