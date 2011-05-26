package org.oddjob.arooa.parsing;

import java.net.URI;
import java.net.URISyntaxException;

import org.oddjob.arooa.ArooaException;

import junit.framework.TestCase;

public class PrefixMappingsTest extends TestCase {

	public void testQName() throws ArooaException, URISyntaxException {
		
		PrefixMappings test = new SimplePrefixMappings();
		
		test.put("oddjob", 
				new URI("http://www.rgordon.co.uk/projects/oddjob"));
		
		ArooaElement element = new ArooaElement(
				new URI("http://www.rgordon.co.uk/projects/oddjob"),
				"fruit");
		
		assertEquals("oddjob:fruit", test.getQName(element).toString());
		
	}
	
	public void testPrefixForURI() throws ArooaException, URISyntaxException {
		
		PrefixMappings test = new SimplePrefixMappings();
		
		test.put("oddjob", 
				new URI("http://www.rgordon.co.uk/projects/oddjob"));
		
		assertEquals("oddjob", test.getPrefixFor(new URI("http://www.rgordon.co.uk/projects/oddjob")));
		
	}
	
	public void testURIForPrefix() throws ArooaException, URISyntaxException {
		
		PrefixMappings test = new SimplePrefixMappings();
		
		test.put("oddjob", 
				new URI("http://www.rgordon.co.uk/projects/oddjob"));
		
		assertEquals(new URI("http://www.rgordon.co.uk/projects/oddjob"), 
				test.getUriFor("oddjob"));
		
	}
}
