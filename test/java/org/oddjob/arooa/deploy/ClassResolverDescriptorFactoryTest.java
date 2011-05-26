package org.oddjob.arooa.deploy;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.MockClassResolver;

public class ClassResolverDescriptorFactoryTest extends TestCase {

	private class OurResolver extends MockClassResolver {
		
		@Override
		public ClassLoader[] getClassLoaders() {
			return new ClassLoader[] { 
					getClass().getClassLoader(), 
					getClass().getClassLoader()
			};
		}
	}
	
	public void testCreateDescriptor() throws URISyntaxException {
		
		ClassResolverDescriptorFactory test =
			new ClassResolverDescriptorFactory(
					"org/oddjob/arooa/deploy/descriptor.xml", 
					new OurResolver());
		
		ArooaDescriptor descriptor = test.createDescriptor(
				null);
		
		assertNotNull(descriptor);
		
		assertEquals("arooa", descriptor.getPrefixFor(
				new URI("http://rgordon.co.uk/oddjob/arooa")));
		
	}
}
