package org.oddjob.arooa.parsing.interceptors;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.parsing.SessionDelegate;

/**
 * Override the {@link ArooaDescriptor} in an {@link ArooaSession}.
 * 
 * @author rob
 *
 */
public class DescriptorOverrideSession extends SessionDelegate {

	private final ArooaDescriptor descriptor;
	
	/**
	 * Constructor.
	 * 
	 * @param delegate Existing session.
	 * @param descriptor New descriptor.
	 */
	public DescriptorOverrideSession(
			ArooaSession delegate, ArooaDescriptor descriptor) {
		super(delegate);
		this.descriptor = descriptor;
	}

	@Override
	public ArooaDescriptor getArooaDescriptor() {
		return descriptor;
	}

}
