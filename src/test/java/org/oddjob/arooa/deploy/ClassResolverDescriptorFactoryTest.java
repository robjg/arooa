package org.oddjob.arooa.deploy;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;

import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.MockClassResolver;

public class ClassResolverDescriptorFactoryTest extends Assert {

	private class OurResolver extends MockClassResolver {
		
		@Override
		public ClassLoader[] getClassLoaders() {
			return new ClassLoader[] { 
					getClass().getClassLoader(), 
					getClass().getClassLoader()
			};
		}
	}
	
   @Test
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
