package org.oddjob.arooa.parsing;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

import org.oddjob.arooa.ArooaException;

import org.junit.Assert;

public class PrefixMappingsTest extends Assert {

   @Test
	public void testQName() throws ArooaException, URISyntaxException {
		
		PrefixMappings test = new SimplePrefixMappings();
		
		test.put("oddjob", 
				new URI("http://www.rgordon.co.uk/projects/oddjob"));
		
		ArooaElement element = new ArooaElement(
				new URI("http://www.rgordon.co.uk/projects/oddjob"),
				"fruit");
		
		assertEquals("oddjob:fruit", test.getQName(element).toString());
		
	}
	
   @Test
	public void testPrefixForURI() throws ArooaException, URISyntaxException {
		
		PrefixMappings test = new SimplePrefixMappings();
		
		test.put("oddjob", 
				new URI("http://www.rgordon.co.uk/projects/oddjob"));
		
		assertEquals("oddjob", test.getPrefixFor(new URI("http://www.rgordon.co.uk/projects/oddjob")));
		
	}
	
   @Test
	public void testURIForPrefix() throws ArooaException, URISyntaxException {
		
		PrefixMappings test = new SimplePrefixMappings();
		
		test.put("oddjob", 
				new URI("http://www.rgordon.co.uk/projects/oddjob"));
		
		assertEquals(new URI("http://www.rgordon.co.uk/projects/oddjob"), 
				test.getUriFor("oddjob"));
		
	}
}
