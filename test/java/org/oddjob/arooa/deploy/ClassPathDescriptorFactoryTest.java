package org.oddjob.arooa.deploy;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.oddjob.arooa.ArooaDescriptor;

public class ClassPathDescriptorFactoryTest extends TestCase {

	public void testCreate() throws URISyntaxException {
		ClassPathDescriptorFactory test = new ClassPathDescriptorFactory();
		
		String resource = "org/oddjob/arooa/deploy/descriptor.xml";
		
		assertNotNull(getClass().getClassLoader().getResource(resource));
		
		test.setResource(resource);
		
		ArooaDescriptor descriptor = test.createDescriptor(
				getClass().getClassLoader());
		
		assertNotNull(descriptor);
		
		assertEquals("arooa", descriptor.getPrefixFor(
				new URI("http://rgordon.co.uk/oddjob/arooa")));
	}

	public void testMissingResource() {
		
		ClassPathDescriptorFactory test = new ClassPathDescriptorFactory();

		String resource = "some/missing/thing.xml";
		
		assertNull(getClass().getClassLoader().getResource(resource));
		
		test.setResource(resource);
		
		ArooaDescriptor descriptor = test.createDescriptor(
				getClass().getClassLoader());
		
		assertEquals(null, descriptor);
	}
	
}
